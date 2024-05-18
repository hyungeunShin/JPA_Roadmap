package com.example.board.controller;

import com.example.board.dto.BoardDetailDTO;
import com.example.board.dto.BoardListDTO;
import com.example.board.dto.EditBoardDTO;
import com.example.board.dto.RegisterBoardDTO;
import com.example.board.service.BoardService;
import com.example.util.FileStore;
import com.example.util.PaginationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;

    private final FileStore fileStore;

    @ModelAttribute("searchTypes")
    public Map<String, String> searchTypes() {
        Map<String, String> searchTypes = new HashMap<>();
        searchTypes.put("title", "제목");
        searchTypes.put("content", "내용");
        searchTypes.put("writer", "작성자");

        return searchTypes;
    }

    @GetMapping("/list")
    public String list(@RequestParam(name = "page", required = false, defaultValue = "1") int currentPage
                       , @RequestParam(name = "searchType", required = false, defaultValue = "title") String searchType
                       , @RequestParam(name = "searchWord", required = false) String searchWord
                       , Model model) {
        log.info("currentPage: " + currentPage);
        log.info("searchType: " + searchType);
        log.info("searchWord: " + searchWord);

        PaginationInfo<BoardListDTO> paginationInfo = new PaginationInfo<>();

        if(StringUtils.isNotBlank(searchWord)) {
            paginationInfo.setSearchType(searchType);
            paginationInfo.setSearchWord(searchWord);
        }

        paginationInfo.setCurrentPage(currentPage);

        int totalRecord = service.getBoardTotalCount(paginationInfo);
        paginationInfo.setTotalRecord(totalRecord);

        List<BoardListDTO> dataList = service.getBoardList(paginationInfo);
        paginationInfo.setDataList(dataList);

        model.addAttribute("paginationInfo", paginationInfo);

        return "board/list";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("board") RegisterBoardDTO dto) {
        return "board/addForm";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("board") RegisterBoardDTO dto, BindingResult bindingResult, Model model, RedirectAttributes ra) {
        log.info("register.board = {}", dto.toString());
        log.info("files.length : {}", dto.getBoardFile().length);

        if(bindingResult.hasErrors()) {
            return "board/addForm";
        }

        Long id = null;
        try {
            id = service.saveBoard(dto);
        } catch(IOException e) {
            model.addAttribute("message", "다시 시도해주세요.");
            return "board/addForm";
        }

        if(id != null) {
            ra.addFlashAttribute("message", "게시글이 등록되었습니다.");
            ra.addAttribute("id", id);
            return "redirect:/board/detail/{id}";
        } else {
            model.addAttribute("message", "다시 시도해주세요.");
            return "board/addForm";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            BoardDetailDTO dto = service.findBoardDetail(id);
            model.addAttribute("board", dto);
            return "board/detail";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            BoardDetailDTO boardDetailDTO = service.findBoardDetail(id);
            model.addAttribute("board", boardDetailDTO);
            return "board/editForm";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, @Validated @ModelAttribute("board") EditBoardDTO dto, BindingResult bindingResult,
                       Model model, RedirectAttributes ra) {
        log.info("수정할 게시판 번호 : {}", id);
        log.info("수정할 게시판 데이터 : {}", dto);

        if(bindingResult.hasErrors()) {
            BoardDetailDTO boardDetailDTO = service.findBoardDetail(id);
            model.addAttribute("board", boardDetailDTO);
            return "board/editForm";
        }

        try {
            List<String> uploadPaths = service.editBoard(dto);
            if(uploadPaths != null) {
                for(String uploadPath : uploadPaths) {
                    fileStore.deleteFile(uploadPath);
                }
            }

            ra.addFlashAttribute("message", "수정되었습니다.");
            return "redirect:/board/detail/{id}";
        } catch (IOException e) {
            model.addAttribute("message", "다시 시도해주세요.");
            BoardDetailDTO boardDetailDTO = service.findBoardDetail(id);
            model.addAttribute("board", boardDetailDTO);
            return "board/editForm";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        log.info("{}", id);

        try {
            service.deleteBoard(id);
            ra.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", "다시 시도해주세요.");
        }

        return "redirect:/board/list";
    }
}
