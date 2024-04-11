package com.example.board.controller;

import com.example.board.dto.*;
import com.example.board.service.BoardService;
import com.example.member.domain.Member;
import com.example.util.FileStore;
import com.example.util.PaginationInfo;
import com.example.util.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
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
    public String list(@RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
                       @RequestParam(name = "searchType", required = false, defaultValue = "title") String searchType,
                       @RequestParam(name = "searchWord", required = false) String searchWord,
                       Model model) {
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
    public String register(@Validated @ModelAttribute("board") RegisterBoardDTO dto, BindingResult bindingResult,
                           @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member, Model model, RedirectAttributes ra) {
        log.info("register.board = {}", dto.toString());
        log.info("files.length : {}", dto.getBoardFile().length);

        if(bindingResult.hasErrors()) {
            return "board/addForm";
        }

        Long boardNo = null;
        try {
            boardNo = service.saveBoard(dto, member);
        } catch (IOException e) {
            model.addAttribute("message", "다시 시도해주세요.");
            return "board/addForm";
        }

        if(boardNo != null) {
            ra.addFlashAttribute("message", "게시글이 등록되었습니다.");
            ra.addAttribute("boardNo", boardNo);
            return "redirect:/board/detail/{boardNo}";
        } else {
            model.addAttribute("message", "다시 시도해주세요.");
            return "board/addForm";
        }
    }

    @GetMapping("/detail/{boardNo}")
    public String detail(@PathVariable("boardNo") Long boardNo, Model model, RedirectAttributes ra, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        try {
            BoardDetailDTO dto = service.findBoardDetail(boardNo);

            if(!dto.getMemberNo().equals(member.getId())) {
                service.increaseHit(boardNo);
            }

            model.addAttribute("board", dto);
            return "board/detail";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }
    }

    @GetMapping("/attach/download")
    public ResponseEntity<Resource> downloadAttach(@RequestParam("fileNo") Long fileNo, HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        log.info("다운로드 파일 번호 : {}", fileNo);
        try {
            BoardFileDownloadDTO dto = service.findAttachFile(fileNo);
            String fileName = dto.getUploadFileName();
            UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(fileName));

            String originalFileName = dto.getOriginalFileName();

            String agent = request.getHeader("User-Agent");
            if(StringUtils.containsIgnoreCase(agent, "msie") || StringUtils.containsIgnoreCase(agent, "trident")) {
                originalFileName = URLEncoder.encode(originalFileName, "UTF-8");	 		//IE, Chrome
            } else {
                originalFileName = new String(originalFileName.getBytes(), "ISO-8859-1");	// Firefox, chrome
            }

            String contentDisposition = "attachment; filename=\"" + originalFileName + "\"";

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
        } catch(NullPointerException e) {
            return ResponseEntity.ok().body(null);
        }
    }

    @GetMapping("/edit/{boardNo}")
    public String editForm(@PathVariable("boardNo") Long boardNo, Model model, RedirectAttributes ra) {
        try {
            BoardDetailDTO boardDetailDTO = service.findBoardDetail(boardNo);
            model.addAttribute("board", boardDetailDTO);
            return "board/editForm";
        } catch(NullPointerException e) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }
    }

    @PostMapping("/edit/{boardNo}")
    public String edit(@PathVariable("boardNo") Long boardNo, @Validated @ModelAttribute("board") EditBoardDTO dto, BindingResult bindingResult,
                       Model model, RedirectAttributes ra) {
        log.info("수정할 게시판 번호 : {}", boardNo);
        log.info("수정할 게시판 데이터 : {}", dto);

        if(bindingResult.hasErrors()) {
            BoardDetailDTO boardDetailDTO = service.findBoardDetail(boardNo);
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
            return "redirect:/board/detail/{boardNo}";
        } catch (IOException e) {
            model.addAttribute("message", "다시 시도해주세요.");
            BoardDetailDTO boardDetailDTO = service.findBoardDetail(boardNo);
            model.addAttribute("board", boardDetailDTO);
            return "board/editForm";
        }
    }
}
