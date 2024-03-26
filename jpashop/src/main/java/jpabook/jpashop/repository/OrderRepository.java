package jpabook.jpashop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.controller.dto.PaginationInfo;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
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

    public List<Order> findAllByQuerydsl(PaginationInfo<Order> paginationInfo) {
        OrderStatus orderStatus = paginationInfo.getOrderStatus();
        String memberName = paginationInfo.getMemberName();

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
                    .limit(paginationInfo.getScreenSize())
                    .offset(paginationInfo.getStartRow() - 1)
                    .fetch();
    }

    public Long ordersTotalCount(PaginationInfo<Order> paginationInfo) {
        OrderStatus orderStatus = paginationInfo.getOrderStatus();
        String memberName = paginationInfo.getMemberName();

        QOrder order = QOrder.order;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if(orderStatus != null) {
            builder.and(order.status.eq(orderStatus));
        }

        if(StringUtils.hasText(memberName)) {
            builder.and(member.name.like("%" + memberName + "%"));
        }

        return query.select(order.count())
                    .from(order)
                    .join(order.member, member)
                    .where(builder)
                    .fetchOne();
    }
}
