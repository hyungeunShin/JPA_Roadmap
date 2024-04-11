package com.example.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardFileDownloadDTO {
    private String uploadFileName;

    private String originalFileName;

    public BoardFileDownloadDTO(String uploadFileName, String originalFileName) {
        this.uploadFileName = uploadFileName;
        this.originalFileName = originalFileName;
    }
}
