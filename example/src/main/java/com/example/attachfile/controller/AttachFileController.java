package com.example.attachfile.controller;

import com.example.attachfile.dto.BoardFileDownloadDTO;
import com.example.attachfile.service.AttachFileService;
import com.example.util.FileStore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AttachFileController {
    private final AttachFileService service;

    private final FileStore fileStore;

    @Value("${resource.handler}")
    private String ckEditorPath;

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
                originalFileName = URLEncoder.encode(originalFileName, "UTF-8");	 		        //IE, Chrome
            } else {
                originalFileName = new String(originalFileName.getBytes(), "ISO-8859-1");	//Firefox, chrome
            }

            String contentDisposition = "attachment; filename=\"" + originalFileName + "\"";

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
        } catch(NullPointerException e) {
            return ResponseEntity.ok().body(null);
        }
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource renderImage(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @PostMapping(value = "/attach/upload")
    public String imageUpload(HttpServletResponse response, MultipartHttpServletRequest multiFile) throws IOException {
        log.info("ckeditor 파일 업로드 시작");

        JsonObject json = new JsonObject();
        PrintWriter printWriter = null;
        long limitSize = 1024 * 1024 * 2;
        MultipartFile file = multiFile.getFile("upload");

        if (file != null && file.getSize() > 0 && StringUtils.isNotBlank(file.getName())) {
            if (file.getContentType().toLowerCase().startsWith("image/")) {
                if (file.getSize() > limitSize) {
                    JsonObject jsonMsg = new JsonObject();
                    JsonArray jsonArr = new JsonArray();
                    jsonMsg.addProperty("message", "2MB미만의 이미지만 업로드 가능합니다");
                    jsonArr.add(jsonMsg);
                    json.addProperty("uploaded", 0);
                    json.add("error", jsonArr.get(0));
                    // 실패 시 CKEDIOTOR의 규칙

                    response.setCharacterEncoding("UTF-8");
                    printWriter = response.getWriter();
                    printWriter.println(json);
                    printWriter.flush();
                } else {
                    try {
                        String uploadFile = fileStore.uploadFile(file);

                        printWriter = response.getWriter();
                        response.setContentType("text/html");
                        String fileUrl = ckEditorPath + uploadFile;

                        // 성공 시 필수로 3개 모두 다시 내보내줘야 한다.
                        json.addProperty("uploaded", 1);
                        json.addProperty("fileName", uploadFile);
                        json.addProperty("url", fileUrl);

                        printWriter.println(json);
                        printWriter.flush();
                    } catch (IOException e) {
                        log.error("CKEditor Image Upload Error", e);
                    } finally {
                        if (printWriter != null) printWriter.close();
                    }
                }
            }
        }

        return null;
    }
}
