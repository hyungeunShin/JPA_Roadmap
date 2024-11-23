package com.example.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStore {
	private final String uploadPath;

	private final List<String> allowedExtension;

	public FileStore(@Value("${image.upload.path}") String uploadPath, @Value("${file.allow.extension}") String allowedExtension) {
		this.uploadPath = uploadPath;
		this.allowedExtension = Arrays.stream(allowedExtension.split(",")).filter(StringUtils::hasText).toList();
	}

	public String getFullPath(String filename) {
		return uploadPath + File.separator + filename;
	}

	public String uploadFile(MultipartFile multipartFile) throws IOException {
		String filename = createStoreFileName(multipartFile.getOriginalFilename());
		try {
			multipartFile.transferTo(new File(getFullPath(filename)));
		} catch(IOException e) {
			throw new IOException("Upload.Fail.File");
		}
		return filename;
	}

	private String createStoreFileName(String originalFilename) throws IOException {
		String ext = extractExt(originalFilename);
		if(!checkExtension(ext)) {
			throw new IOException("Not.Allow.Extension");
		}
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + ext;
	}

	public String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}

	private boolean checkExtension(String ext) {
		return allowedExtension.contains(ext);
	}
}
