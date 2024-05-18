package com.example.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStore {
	@Value("${image.upload.path}")
	private String uploadPath;

	public String getFullPath(String filename) {
		return uploadPath + File.separator + filename;
	}

	public String uploadFile(MultipartFile multipartFile) throws IOException {
		String filename = createStoreFileName(multipartFile.getOriginalFilename());
		multipartFile.transferTo(new File(getFullPath(filename)));
		return filename;
	}

	public String createStoreFileName(String originalFilename) {
		String ext = extractExt(originalFilename);
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + ext;
	}

	public String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}

	public void deleteFile(String uploadFullPath) {
		File file = new File(uploadFullPath);
		file.delete();
	}
}
