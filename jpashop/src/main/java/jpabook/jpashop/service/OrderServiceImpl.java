package jpabook.jpashop.service;

import jpabook.jpashop.controller.dto.PaginationInfo;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public Long order(Long memberId, Long itemId, int count) throws NotEnoughStockException {
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

    @Transactional
    @Override
    public void cancelOrder(Long orderId) throws IllegalStateException {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    @Override
    public List<Order> findOrders(PaginationInfo<Order> paginationInfo) {
        return orderRepository.findAllByQuerydsl(paginationInfo);
    }

    @Override
    public Long orderTotalCount(PaginationInfo<Order> paginationInfo) {
        return orderRepository.ordersTotalCount(paginationInfo);
    }
}
