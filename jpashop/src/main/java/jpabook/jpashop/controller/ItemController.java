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

        if("BOOK".equals(form.getItemType())) {
            Book book = new Book();
            book.setName(form.getName());
            book.setPrice(form.getPrice());
            book.setQuantity(form.getQuantity());
            book.setAuthor(form.getAuthor());
            book.setIsbn(form.getIsbn());
            service.saveItem(book);
        } else if("ALBUM".equals(form.getItemType())) {
            Album album = new Album();
            album.setName(form.getName());
            album.setPrice(form.getPrice());
            album.setQuantity(form.getQuantity());
            album.setArtist(form.getArtist());
            album.setEtc(form.getEtc());
            service.saveItem(album);
        } else if("MOVIE".equals(form.getItemType())) {
            Movie movie = new Movie();
            movie.setName(form.getName());
            movie.setPrice(form.getPrice());
            movie.setQuantity(form.getQuantity());
            movie.setDirector(form.getDirector());
            movie.setActor(form.getActor());
            service.saveItem(movie);
        }

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
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") @Validated ItemEditForm form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "items/updateItemForm";
        }

        service.updateItem(form);
        return "redirect:/items";
    }
}
