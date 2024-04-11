package com.example.common.controller;

import com.example.util.FileStore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {
	private final FileStore fileStore;

	@Value("${resource.handler}")
	private String ckEditorPath;

	@ResponseBody
	@GetMapping("/images/{filename}")
	public Resource renderImage(@PathVariable("filename") String filename) throws MalformedURLException {
		return new UrlResource("file:" + fileStore.getFullPath(filename));
	}

	@PostMapping(value = "/fileUpload")
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
