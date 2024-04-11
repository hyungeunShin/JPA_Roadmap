package com.example.board.dto;

import com.example.board.domain.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardListDTO {
    private Long id;

    private String boardTitle;

    private String boardContent;

    private int boardHit;

    private String boardWriter;

    private LocalDateTime boardRegisterDate;

    public BoardListDTO(Board board) {
        this.id = board.getId();
        this.boardTitle = board.getBoardTitle();
        this.boardContent = board.getBoardContent();
        this.boardHit = board.getBoardHit();
        this.boardWriter = board.getMember().getMemberId();
        this.boardRegisterDate = board.getRegisterDate();
    }
}
