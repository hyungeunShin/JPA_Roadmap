package com.example.board.dto;

import com.example.common.domain.AttachFile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardFileDetailDTO {
    private Long id;

    private String originalFileName;

    private String fileFancySize;

    private int fileDownloadCount;

    public BoardFileDetailDTO(AttachFile file) {
        this.id = file.getId();
        this.originalFileName = file.getOriginalFileName();
        this.fileFancySize = file.getFileFancySize();
        this.fileDownloadCount = file.getFileDownloadCount();
    }
}
