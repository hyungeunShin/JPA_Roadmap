package com.example.jpashop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import com.example.jpashop.controller.dto.OrderSearch;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.OrderStatus;
import com.example.jpashop.domain.QMember;
import com.example.jpashop.domain.QOrder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class OrderRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll() {
        return em.createQuery("select o from Order o join o.member m", Order.class).setMaxResults(1000).getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o join fetch o.member join fetch o.delivery", Order.class).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o join fetch o.member join fetch o.delivery", Order.class)
                 .setFirstResult(offset)
                 .setMaxResults(limit)
                 .getResultList();
    }

    public List<Order> findAllWithItem() {
        /*
        하이버네이트 6부터 fetch join 이 하위 컬렉션을 가져올 때 동일한 상위 엔터티 참조를 필터링하기 위해 distinct 를 더 이상 사용할 필요가 없다.
        https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#distinct
        */
        return em.createQuery(
                "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                /*.setFirstResult(0)
                .setMaxResults(100)*/
                .getResultList();
    }

    public List<Order> findAllByQuerydsl(OrderSearch orderSearch) {
        OrderStatus orderStatus = orderSearch.getOrderStatus();
        String memberName = orderSearch.getMemberName();

        QOrder order = QOrder.order;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if(orderStatus != null) {
            builder.and(order.status.eq(orderStatus));
        }

        if(StringUtils.hasText(memberName)) {
            builder.and(member.name.like("%" + memberName + "%"));
        }

        return query.select(order)
                    .from(order)
                    .join(order.member, member)
                    .where(builder)
                    .orderBy(order.id.desc())
                    .fetch();
    }
}
