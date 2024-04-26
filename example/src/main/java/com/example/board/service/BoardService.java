package com.example.board.service;

import com.example.board.dto.*;
import com.example.user.domain.User;
import com.example.util.PaginationInfo;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    List<BoardListDTO> getBoardList(PaginationInfo<BoardListDTO> paginationInfo);

    int getBoardTotalCount(PaginationInfo<BoardListDTO> paginationInfo);

    Long saveBoard(RegisterBoardDTO dto, User user) throws IOException;

    BoardDetailDTO findBoardDetail(Long boardNo) throws NullPointerException;

    void increaseHit(Long boardNo);

    BoardFileDownloadDTO findAttachFile(Long fileNo) throws NullPointerException;

    List<String> editBoard(EditBoardDTO dto) throws IOException;
}
