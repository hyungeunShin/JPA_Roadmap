package com.example.jpashop.api;

import com.example.jpashop.api.dto.OrderDto;
import com.example.jpashop.api.dto.OrderFlatDto;
import com.example.jpashop.api.dto.OrderItemQueryDto;
import com.example.jpashop.api.dto.OrderQueryDto;
import com.example.jpashop.api.query.OrderQueryRepository;
import com.example.jpashop.domain.Order;
import com.example.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * xToMany(OneToMany) 관계 최적화
 * Order -> OrderItem
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository repository;

    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = repository.findAll();
        //강제 초기화
        for(Order order : all) {
            log.info(order.getMember().getName());
            log.info("{}", order.getDelivery().getAddress());
            order.getOrderItems().forEach(o -> log.info(o.getItem().getName()));
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        return repository.findAll().stream().map(OrderDto::new).toList();
    }

    /**
     * 페이징 문제
     *  - 컬렉션을 페치 조인하면 페이징이 불가능하다.
     *  - 컬렉션을 페치 조인하면 일대다 조인이 발생하므로 데이터가 예측할 수 없이 증가한다.
     *  - 일다대에서 일(1)을 기준으로 페이징을 하는 것이 목적이다. 그런데 데이터는 다(N)를 기준으로 row 가 생성된다.
     *  - Order 를 기준으로 페이징 하고 싶은데, 다(N)인 OrderItem 을 조인하면 OrderItem 이 기준이 되어버린다.
     *
     * 이 경우 하이버네이트는 경고 로그를 남기고(HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory) 모든 DB 데이터를 읽어서 메모리에서 페이징을 시도한다.
     * 최악의 경우 장애로 이어질 수 있다.
     */
    //페치 조인으로 쿼리 수 최적화
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return repository.findAllWithItem().stream().map(OrderDto::new).toList();
    }

    /**
     * 페이징과 한계 돌파
     *  1. 먼저 ToOne(OneToOne, ManyToOne) 관계를 모두 페치조인 한다. ToOne 관계는 row 수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
     *  2. 컬렉션은 지연 로딩으로 조회한다.
     *  3. 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize 를 적용한다.
     *      - hibernate.default_batch_fetch_size: 글로벌 설정
     *      - @BatchSize: 개별 최적화
     *
     *  장점
     *      - 쿼리 호출 수가 1 + N -> 1 + 1 로 최적화 된다.
     *      - 조인보다 DB 데이터 전송량이 최적화 된다. (Order 와 OrderItem 을 조인하면 Order 가 OrderItem 만큼 중복해서 조회된다. 이 방법은 각각 조회하므로 전송해야할 중복 데이터가 없다.)
     *      - 페치 조인 방식과 비교해서 쿼리 호출 수가 약간 증가하지만, DB 데이터 전송량이 감소한다.
     *      - 컬렉션 페치 조인은 페이징이 불가능 하지만 이 방법은 페이징이 가능하다.
     *  결론
     *      - ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는 페치조인으로 쿼리 수를 줄이고 해결하고 나머지는 hibernate.default_batch_fetch_size 로 최적화
     */
    //컬렉션 페이징과 한계 돌파(hibernate.default_batch_fetch_size)
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "limit", defaultValue = "100") int limit) {
        /*
        select oi1_0.order_id, oi1_0.order_item_id, oi1_0.count, oi1_0.item_id, ... from order_item oi1_0 where oi1_0.order_id in (?, ?, ?, ....)
        select i1_0.item_id, i1_0.dtype, i1_0.name, i1_0.price, i1_0.quantity, ... from item i1_0 where i1_0.item_id in (?, ?, ?, ....)

        스프링 부트 3.1 - 하이버네이트 6.2 변경사항 - array_contains
        하이버네이트 6.2 부터는 where in 대신에 array_contains 를 사용한다. => SQL 캐싱 성능

        where in 쿼리는 동적으로 데이터가 변하는 것을 넘어서 SQL 구문 자체가 변해버리는 문제가 발생
        다음 예시는 in에 들어가는 데이터 숫자에 따라서 총 3개의 SQL 구문이 생성
            1. where item.item_id in(?)
            2. where item.item_id in(?,?)
            3. where item.item_id in(?,?,?,?)
        SQL 입장에서는 ? 로 바인딩 되는 숫자 자체가 다르기 때문에 완전히 다른 SQL 이다. 따라서 총 3개의 SQL 구문이 만들어지고 캐싱도 3개를 따로 해야한다. 이렇게 되면 성능 관점에서 좋지않다.

        array_contains 를 사용하면 이런 문제를 깔끔하게 해결할 수 있다.
        이 문법은 결과적으로 where in 과 동일하다. array_contains 은 왼쪽에 배열을 넣는데 배열에 들어있는 숫자가 오른쪽(item_id)에 있다면 참(TRUE)
            참고) 다음 둘은 동일
            select ... where array_contains([1,2,3],item.item_id)
            select ... item.item_id where in(1,2,3)
        이 문법은 ?에 바인딩 되는 것이 딱 1개 이다. 배열1개가 들어가는 것이다.
        select ... where array_contains(?,item.item_id)
        따라서 배열에 들어가는 데이터가 늘어도 SQL 구문 자체가 변하지 않는다. ? 에는 배열 하나만 들어가면 된다.
        이런 방법을 사용하면 앞서 이야기한 동적으로 늘어나는 SQL 구문을 걱정하지 않아도 된다.
        결과적으로 데이터가 동적으로 늘어나도 같은 SQL 구문을 그대로 사용해서 성능을 최적화 할 수 있다.
        참고로 array_contains 에서 default_batch_fetch_size 에 맞추어 배열에 null 값을 추가하는데 이 부분은 아마도 특정 데이터베이스에 따라서 배열의 데이터 숫자가 같아야 최적화가 되기 때문에 그런 것으로 추정
        */
        return repository.findAllWithMemberDelivery(offset, limit).stream().map(OrderDto::new).toList();
    }

    /**
     * ToOne(N:1, 1:1) 관계들을 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리한다.
     *  - ToOne 관계는 조인해도 데이터 row 수가 증가하지 않는다.
     *  - ToMany(1:N) 관계는 조인하면 row 수가 증가한다.
     */
    //DTO 를 직접 조회
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDto();
    }

    //컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN 절을 활용해서 메모리에 미리 조회해서 최적화
    //DTO 방식으로 조회를 해야한다면 이 방식을 추천
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    //플랫 데이터 최적화 - JOIN 결과를 그대로 조회 후 애플리케이션에서 원하는 모양으로 직접 변환
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        //Order 기준으로 페이징이 불가능
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        //OrderFlatDto 형식을 OrderQueryDto 형식으로
        //OrderQueryDto 를 기준으로 하나로 묶어야 되니까 @EqualsAndHashCode(of = "orderId") 추가
        return flats.stream()
                .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList());
    }

    /*
    권장 순서
    1. 엔티티 조회 방식으로 우선 접근
        1-1. 페치조인으로 쿼리 수를 최적화
        1-2. 컬렉션 최적화
            - 페이징 필요 hibernate.default_batch_fetch_size, @BatchSize 로 최적화
            - 페이징 필요X 페치 조인 사용
    2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
    3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate

    엔티티 조회 방식은 페치 조인이나 hibernate.default_batch_fetch_size, @BatchSize 같이 코드를 거의 수정하지 않고 옵션만 약간 변경해서 다양한 성능 최적화를 시도할 수 있다.
    반면에 DTO 를 직접 조회하는 방식은 성능을 최적화 하거나 성능 최적화 방식을 변경할 때 많은 코드를 변경해야 한다.
    */
}
