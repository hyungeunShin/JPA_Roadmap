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

    private Long memberNo;

    private String boardTitle;

    private String boardContent;

    private int boardHit;

    private String boardWriter;

    private LocalDateTime boardRegisterDate;

    private List<BoardFileDetailDTO> boardFileList;

    public BoardDetailDTO(Board board, List<BoardFileDetailDTO> boardFileList) {
        this.id = board.getId();
        this.memberNo = board.getMember().getId();
        this.boardTitle = board.getBoardTitle();
        this.boardContent = board.getBoardContent();
        this.boardHit = board.getBoardHit();
        this.boardWriter = board.getMember().getMemberId();
        this.boardRegisterDate = board.getRegisterDate();
        this.boardFileList = boardFileList;
    }
}
