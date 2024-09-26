package jpabook.jpashop.controller;

import jpabook.jpashop.controller.dto.OrderSearch;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
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
