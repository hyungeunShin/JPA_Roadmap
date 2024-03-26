package jpabook.jpashop.service;

import jpabook.jpashop.controller.dto.PaginationInfo;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.exception.NotEnoughStockException;

import java.util.List;

public interface OrderService {
    Long order(Long memberId, Long itemId, int count) throws NotEnoughStockException;

    void cancelOrder(Long orderId) throws IllegalStateException;

    List<Order> findOrders(PaginationInfo<Order> paginationInfo);

    Long orderTotalCount(PaginationInfo<Order> paginationInfo);
}
