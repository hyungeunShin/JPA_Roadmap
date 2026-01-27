package com.example.jpashop.controller;

import com.example.jpashop.controller.dto.OrderSearch;
import com.example.jpashop.domain.Member;
import com.example.jpashop.domain.Order;
import com.example.jpashop.domain.item.Item;
import com.example.jpashop.exception.NotEnoughStockException;
import com.example.jpashop.service.ItemService;
import com.example.jpashop.service.MemberService;
import com.example.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @ModelAttribute("members")
    public List<Member> members() {
        return memberService.findMembers();
    }

    @ModelAttribute("items")
    public List<Item> items() {
        return itemService.findItems();
    }

    @GetMapping(value = "/order")
    public String createForm() {
        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId, @RequestParam("itemId") Long itemId, @RequestParam("count") int count, Model model) {
        try {
            orderService.order(memberId, itemId, count);
        } catch(NotEnoughStockException e) {
            model.addAttribute("message", e.getMessage());
            return "order/orderForm";
        }

        return "redirect:/orders";
    }

    @GetMapping(value = "/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @PostMapping(value = "/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId, RedirectAttributes ra) {
        try {
            orderService.cancelOrder(orderId);
        } catch(IllegalStateException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/orders";
    }
}
