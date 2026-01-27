package com.example.jpashop.controller;

import com.example.jpashop.controller.dto.BookForm;
import com.example.jpashop.domain.item.Book;
import com.example.jpashop.domain.item.Item;
import com.example.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@ModelAttribute("form") BookForm form, RedirectAttributes ra) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setQuantity(form.getQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        service.saveItem(book);

        ra.addFlashAttribute("message", "상품이 등록되었습니다.");
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = service.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) service.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setQuantity(item.getQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable("itemId") Long itemId) {
        service.updateItem(itemId, form.getName(), form.getPrice(), form.getQuantity());
        return "redirect:/items";
    }
}
