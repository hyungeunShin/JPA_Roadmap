package com.example.jpashop.api.simplequery;

import jakarta.persistence.EntityManager;
import com.example.jpashop.api.dto.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDto() {
        return em.createQuery(
                "select" +
                        " new com.example.jpashop.api.dto.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d"
                , OrderSimpleQueryDto.class).getResultList();
    }
}
