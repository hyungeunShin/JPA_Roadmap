package com.example.attachfile.controller;

import com.example.attachfile.dto.BoardFileDownloadDTO;
import com.example.attachfile.service.AttachFileService;
import com.example.util.FileStore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AttachFileController {
    private final AttachFileService service;

    private final FileStore fileStore;

    @GetMapping("/attach/download")
    public ResponseEntity<Resource> downloadAttach(@RequestParam("id") Long id, HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        log.info("다운로드 파일 번호 : {}", id);
        try {
            BoardFileDownloadDTO dto = service.findAttachFile(id);
            String fileName = dto.getUploadFileName();
            UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(fileName));

            String originalFileName = dto.getOriginalFileName();

            String agent = request.getHeader("User-Agent");
            if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(agent, "msie") || org.apache.commons.lang3.StringUtils.containsIgnoreCase(agent, "trident")) {
                originalFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);
            } else {
                originalFileName = new String(originalFileName.getBytes(), StandardCharsets.ISO_8859_1);
            }

            String contentDisposition = "attachment; filename=\"" + originalFileName + "\"";

            return ResponseEntity.ok()
                                 .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                 .body(resource);
        } catch(NullPointerException e) {
            return ResponseEntity.ok().body(null);
        }
    }
}
