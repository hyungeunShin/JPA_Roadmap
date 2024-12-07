package com.example.board.dto;

import com.example.board.domain.Board;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardListDTO {
    private int rn;

    private Long id;

    private String boardTitle;

    private int boardHit;

    private String boardWriter;

    private LocalDateTime boardRegisterDate;

    @QueryProjection
    public BoardListDTO(int rn, Long id, String boardTitle, int boardHit, String boardWriter, LocalDateTime boardRegisterDate) {
        this.rn = rn;
        this.id = id;
        this.boardTitle = boardTitle;
        this.boardHit = boardHit;
        this.boardWriter = boardWriter;
        this.boardRegisterDate = boardRegisterDate;
    }
}
