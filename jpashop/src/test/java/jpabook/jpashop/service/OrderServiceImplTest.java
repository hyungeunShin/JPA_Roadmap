package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class OrderServiceImplTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService service;

    @Autowired
    OrderRepository repository;

    @DisplayName("상품 주문")
    @Test
    void order() throws NotEnoughStockException {
        //given
        Member member = createMember();
        Item item = createBook();
        int orderCount = 2;

        //when
        Long orderId = service.order(member.getId(), item.getId(), orderCount);

        //then
        Order order = repository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, order.getStatus(), "상품 주문 시 상태는 ORDER");
        assertEquals(1, order.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다");
        assertEquals(10000 * 2, order.getTotalPrice(), "주문 가격은 가격 * 수량이다");
        assertEquals(8, item.getQuantity(), "주문 수량만큼 재고가 줄어야 한다");
    }

    @DisplayName("상품 주문_재고 수량 초과")
    @Test
    void order2() {
        Member member = createMember();
        Item item = createBook();

        int orderCount = 11;

        assertThatThrownBy(() -> service.order(member.getId(), item.getId(), orderCount)).isInstanceOf(NotEnoughStockException.class);
    }

    @DisplayName("주문 취소")
    @Test
    void cancel() throws NotEnoughStockException {
        //given
        Member member = createMember();
        Item item = createBook();
        int orderCount = 3;

        Long orderId = service.order(member.getId(), item.getId(), orderCount);

        //when
        service.cancelOrder(orderId);

        //then
        Order findOrder = repository.findOne(orderId);
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getQuantity()).isEqualTo(10);
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("JPA 책");
        book.setPrice(10000);
        book.setQuantity(10);
        em.persist(book);
        return book;
    }
}