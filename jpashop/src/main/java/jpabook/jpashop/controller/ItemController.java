package jpabook.jpashop.controller;

import jpabook.jpashop.controller.dto.ItemCreateForm;
import jpabook.jpashop.controller.dto.ItemEditForm;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @ModelAttribute("itemType")
    public Map<String, String> itemType() {
        Map<String, String> map = new HashMap<>();
        map.put("책", "BOOK");
        map.put("앨범", "ALBUM");
        map.put("영화", "MOVIE");
        return map;
    }

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new ItemCreateForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@Validated @ModelAttribute("form") ItemCreateForm form, BindingResult bindingResult, RedirectAttributes ra) {
        if(bindingResult.hasErrors()) {
            return "items/createItemForm";
        }

        Item item = null;

        if("BOOK".equals(form.getItemType())) {
            item = new Book(form.getName(), form.getPrice(), form.getQuantity(), form.getAuthor(), form.getIsbn());
        } else if("ALBUM".equals(form.getItemType())) {
            item = new Album(form.getName(), form.getPrice(), form.getQuantity(), form.getArtist(), form.getEtc());
        } else if("MOVIE".equals(form.getItemType())) {
            item = new Movie(form.getName(), form.getPrice(), form.getQuantity(), form.getDirector(), form.getActor());
        }

        if(item != null) {
            service.saveItem(item);
            ra.addFlashAttribute("message", "상품이 등록되었습니다.");
            return "redirect:/items";
        } else {
            return "items/createItemForm";
        }
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = service.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = service.findOne(itemId);

        ItemEditForm form = new ItemEditForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setQuantity(item.getQuantity());

        if(item instanceof Book book) {
            form.setItemType("BOOK");
            form.setAuthor(book.getAuthor());
            form.setIsbn(book.getIsbn());
        } else if(item instanceof Album album) {
            form.setItemType("ALBUM");
            form.setArtist(album.getArtist());
            form.setEtc(album.getEtc());
        } else if(item instanceof Movie movie) {
            form.setItemType("MOVIE");
            form.setDirector(movie.getDirector());
            form.setActor(movie.getActor());
        }

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") ItemEditForm form) {
        Item item = null;

        if("BOOK".equals(form.getItemType())) {
            item = new Book(form.getId(), form.getName(), form.getPrice(), form.getQuantity(), form.getAuthor(), form.getIsbn());
        } else if("ALBUM".equals(form.getItemType())) {
            item = new Album(form.getId(), form.getName(), form.getPrice(), form.getQuantity(), form.getArtist(), form.getEtc());
        } else if("MOVIE".equals(form.getItemType())) {
            item = new Movie(form.getId(), form.getName(), form.getPrice(), form.getQuantity(), form.getDirector(), form.getActor());
        }

        if(item != null) {
            service.saveItem(item);
            return "redirect:/items";
        } else {
            return "items/updateItemForm";
        }
    }
}
