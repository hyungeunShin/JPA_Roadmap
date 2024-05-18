package com.example.attachfile.domain;

import com.example.board.domain.Board;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Table(name = "file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String originalFileName;

    private String uploadFileName;

    private String uploadFilePath;

    private long fileSize;

    private String fileFancySize;

    private String fileExt;

    private int fileDownloadCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public AttachFile(MultipartFile file, String uploadFileName, String uploadFilePath, String fileExt) {
        this.originalFileName = file.getOriginalFilename();
        this.uploadFileName = uploadFileName;
        this.uploadFilePath = uploadFilePath;
        this.fileSize = file.getSize();
        this.fileFancySize = FileUtils.byteCountToDisplaySize(file.getSize());
        this.fileExt = fileExt;
        this.fileDownloadCount = 0;
    }

    public void attachBoard(Board board) {
        this.board = board;
    }
}
