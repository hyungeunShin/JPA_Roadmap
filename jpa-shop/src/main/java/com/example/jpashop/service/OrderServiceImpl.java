package com.example.jpashop.service;

import com.example.jpashop.controller.dto.OrderSearch;
import com.example.jpashop.domain.*;
import com.example.jpashop.domain.item.Item;
import com.example.jpashop.repository.ItemRepository;
import com.example.jpashop.repository.MemberRepositoryOld;
import com.example.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final MemberRepositoryOld memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) throws IllegalStateException {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    @Override
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByQuerydsl(orderSearch);
    }
}
