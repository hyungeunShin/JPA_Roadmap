package jpabook.jpashop.api;

import jpabook.jpashop.api.dto.OrderSimpleQueryDto;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order -> Member
 * Order -> Delivery
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository repository;

    /**
     * V1. 엔티티 직접 노출
     *  - 양방향 관계 문제 발생 -> @JsonIgnore
     *  - Hibernate5JakartaModule 빈 등록
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        /*
            양쪽을 서로 호출하면서 무한 루프가 걸림 -> 엔티티를 직접 노출할 때는 양방향 연관관계가 걸린 곳은 꼭 한 곳을 @JsonIgnore 처리 해야 한다
            (Member.class, OrderItem.class, Delivery.class 에 @JsonIgnore 추가)

            무한루프를 해결하면 return 하는 과정에서
            No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer 에러가 발생

            order 의 member 와 delivery 는 지연 로딩이다 -> member 와 delivery 에는 프록시 객체가 들어있다 -> 프록시를 직렬화 하는 과정에서 에러 발생 -> Hibernate5JakartaModule 로 에러 해결
        */
        return repository.findAll();
    }

    /**
     * V2. 엔티티를 조회해서 DTO 로 변환(fetch join 사용X)
     *  - 단점: 지연로딩으로 N + 1 문제 발생
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = repository.findAll();
        return orders.stream()
                     .map(SimpleOrderDto::new)
                     .collect(Collectors.toList());
    }

    /**
     * V3. 엔티티를 조회해서 DTO 로 변환(fetch join 사용O)
     *  - fetch join 으로 쿼리 1번 호출
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = repository.findAllWithMemberDelivery();
        return orders.stream()
                     .map(SimpleOrderDto::new)
                     .collect(Collectors.toList());
    }

    /**
     * V4. JPA 에서 DTO 로 바로 조회
     *  - 쿼리 1번 호출
     *  - select 절에서 원하는 데이터만 선택해서 조회
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return repository.findOrderDto();
    }

    /*
    쿼리 방식 선택 권장 순서
        1. 우선 엔티티를 DTO 로 변환하는 방법을 선택한다.
        2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
        3. 그래도 안되면 DTO 로 직접 조회하는 방법을 사용한다.
        4. 최후의 방법은 JPA 가 제공하는 네이티브 SQL 이나 스프링 JDBC Template 을 사용해서 SQL 을 직접 사용한다
    */

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
