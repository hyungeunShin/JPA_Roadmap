package jpabook.jpashop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.api.dto.OrderSimpleQueryDto;
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

    public List<Order> findAll() {
        return em.createQuery("select o from Order o join o.member m", Order.class).setMaxResults(1000).getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o join fetch o.member m join fetch o.delivery d", Order.class).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDto() {
        return em.createQuery("select new jpabook.jpashop.api.dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o join o.member m join o.delivery d"
                        , OrderSimpleQueryDto.class).getResultList();
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
