package com.example.jpashop.controller.dto;

import com.example.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;
}
