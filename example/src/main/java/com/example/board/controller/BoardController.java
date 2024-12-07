package com.example.board.controller;

import com.example.board.dto.EditBoardDTO;
import com.example.board.dto.RegisterBoardDTO;
import com.example.board.service.BoardService;
import com.example.user.domain.CustomUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;
    private final MessageSource messageSource;

    @ModelAttribute("searchTypes")
    public Map<String, String> searchTypes() {
        Map<String, String> searchTypes = new HashMap<>();
        searchTypes.put("title", "제목");
        searchTypes.put("content", "내용");
        searchTypes.put("writer", "작성자");

        return searchTypes;
    }

    @GetMapping("/list")
    public String list(
            @PageableDefault Pageable pageable
            , @RequestParam(name = "searchType", required = false, defaultValue = "title") String searchType
            , @RequestParam(name = "searchWord", required = false) String searchWord
            , Model model) {
        log.info("currentPage: " + pageable);
        log.info("searchType: " + searchType);
        log.info("searchWord: " + searchWord);

        if(StringUtils.isNotBlank(searchWord)) {
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchWord", searchWord);
        }

        model.addAttribute("boardList", service.getBoardList(pageable, searchType, searchWord));

        return "board/list";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("board") RegisterBoardDTO dto) {
        return "board/addForm";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("board") RegisterBoardDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra, HttpServletRequest request) {
        log.info("게시판 등록 : {}", dto);

        if(bindingResult.hasErrors()) {
            return "board/addForm";
        }

        try {
            CustomUser principal = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = service.saveBoard(dto, principal.getId());
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Save.Board", null, request.getLocale()));
            ra.addAttribute("id", id);
            return "redirect:/board/detail/{id}";
        } catch(IOException e) {
            model.addAttribute("message", messageSource.getMessage(e.getMessage(), null, request.getLocale()));
            return "board/addForm";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletRequest request) {
        try {
            model.addAttribute("board", service.findBoardDetail(id));
            return "board/detail";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", messageSource.getMessage("Not.Found.Board", null, request.getLocale()));
            return "redirect:/board/list";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletRequest request) {
        try {
            model.addAttribute("board", service.findBoardDetail(id));
            return "board/editForm";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", messageSource.getMessage("Not.Found.Board", null, request.getLocale()));
            return "redirect:/board/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, @Validated @ModelAttribute("board") EditBoardDTO dto, BindingResult bindingResult, Model model,
                       RedirectAttributes ra, HttpServletRequest request) {
        log.info("수정할 게시판 번호 : {}", id);
        log.info("수정할 게시판 데이터 : {}", dto);

        if(bindingResult.hasErrors()) {
            model.addAttribute("board", service.findBoardDetail(id));
            return "board/editForm";
        }

        try {
            service.editBoard(dto);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Edit.Board", null, request.getLocale()));
            return "redirect:/board/detail/{id}";
        } catch(IOException e) {
            model.addAttribute("message", messageSource.getMessage(e.getMessage(), null, request.getLocale()));
            model.addAttribute("board", service.findBoardDetail(id));
            return "board/editForm";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra, HttpServletRequest request) {
        log.info("{}", id);

        try {
            service.deleteBoard(id);
            ra.addFlashAttribute("message", messageSource.getMessage("Success.Delete.Board", null, request.getLocale()));
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", messageSource.getMessage("Not.Found.Board", null, request.getLocale()));
        }

        return "redirect:/board/list";
    }
}
