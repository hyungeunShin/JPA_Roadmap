package com.example.attachfile.domain;

import com.example.board.domain.Board;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "board")
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

    @Builder
    public AttachFile(String originalFileName, String uploadFileName, String uploadFilePath, long fileSize, String fileFancySize, String fileExt) {
        this.originalFileName = originalFileName;
        this.uploadFileName = uploadFileName;
        this.uploadFilePath = uploadFilePath;
        this.fileSize = fileSize;
        this.fileFancySize = fileFancySize;
        this.fileExt = fileExt;
        this.fileDownloadCount = 0;
    }

    public void attachToBoard(Board board) {
        this.board = board;
    }
}
