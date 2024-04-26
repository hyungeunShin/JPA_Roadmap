package com.example.board.dto;

import com.example.board.domain.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class BoardDetailDTO {
    private Long id;

    private Long userId;

    private String boardTitle;

    private String boardContent;

    private int boardHit;

    private String boardWriter;

    private LocalDateTime boardRegisterDate;

    private List<BoardFileDetailDTO> boardFileList;

    public BoardDetailDTO(Board board) {
        this.id = board.getId();
        this.userId = board.getUser().getId();
        this.boardTitle = board.getBoardTitle();
        this.boardContent = board.getBoardContent();
        this.boardHit = board.getBoardHit();
        this.boardWriter = board.getUser().getUsername();
        this.boardRegisterDate = board.getRegisterDate();
        this.boardFileList = board.getFiles().stream().map(BoardFileDetailDTO::new).toList();
    }
}
