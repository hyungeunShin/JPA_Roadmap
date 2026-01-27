package com.example.jpashop.api.dto;

import com.example.jpashop.domain.Address;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.OrderItem;
import com.example.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.address = order.getDelivery().getAddress();
        this.orderItems = order.getOrderItems().stream().map(OrderItemDto::new).toList();
    }

    @Data
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
