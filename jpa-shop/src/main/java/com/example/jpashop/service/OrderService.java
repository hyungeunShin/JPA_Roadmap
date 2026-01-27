package com.example.jpashop.service;

import com.example.jpashop.controller.dto.OrderSearch;
import com.example.jpashop.domain.Order;

import java.util.List;

public interface OrderService {
    Long order(Long memberId, Long itemId, int count);

    void cancelOrder(Long orderId) throws IllegalStateException;

    List<Order> findOrders(OrderSearch orderSearch);
}
