package jpabook.jpashop.controller;

import jpabook.jpashop.controller.dto.PaginationInfo;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();
        model.addAttribute("members", members);
        model.addAttribute("items", items);
        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId, @RequestParam("itemId") Long itemId
            , @RequestParam("count") int count, RedirectAttributes ra, Model model) {
        try {
            orderService.order(memberId, itemId, count);
            ra.addFlashAttribute("message", "주문이 완료되었습니다.");
        } catch(NotEnoughStockException e) {
            Item item = itemService.findOne(itemId);
            model.addAttribute("message", "재고가 부족합니다.\n" + item.getName() + "의 재고는 " + item.getQuantity() + "개 입니다.");
            List<Member> members = memberService.findMembers();
            List<Item> items = itemService.findItems();
            model.addAttribute("members", members);
            model.addAttribute("items", items);
            model.addAttribute("memberId", memberId);
            model.addAttribute("itemId", itemId);
            return "order/orderForm";
        }
        return "redirect:/orders";
    }

    @GetMapping(value = "/orders")
    public String orderList(@RequestParam(name = "page", required = false, defaultValue = "1") int currentPage, @RequestParam(name = "orderStatus", required = false) OrderStatus orderStatus
            , @RequestParam(name = "memberName", required = false) String memberName, Model model) {
        PaginationInfo<Order> paginationInfo = new PaginationInfo<>();
        paginationInfo.setCurrentPage(currentPage);
        paginationInfo.setMemberName(memberName);
        paginationInfo.setOrderStatus(orderStatus);

        Long totalRecord = orderService.orderTotalCount(paginationInfo);
        paginationInfo.setTotalRecord(totalRecord.intValue());

        List<Order> dataList = orderService.findOrders(paginationInfo);
        paginationInfo.setDataList(dataList);

        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("memberName", memberName);
        model.addAttribute("paginationInfo", paginationInfo);
        return "order/orderList";
    }

    @PostMapping(value = "/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId, RedirectAttributes ra) {
        try {
            orderService.cancelOrder(orderId);
            ra.addFlashAttribute("message", "주문이 취소되었습니다.");
        } catch(IllegalStateException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/orders";
    }
}
