package com.example.board.service;

import com.example.board.dto.*;
import com.example.util.PaginationInfo;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    List<BoardListDTO> getBoardList(PaginationInfo<BoardListDTO> paginationInfo);

    int getBoardTotalCount(PaginationInfo<BoardListDTO> paginationInfo);

    Long saveBoard(RegisterBoardDTO dto) throws IOException, NullPointerException;

    BoardDetailDTO findBoardDetail(Long id) throws NullPointerException;

    List<String> editBoard(EditBoardDTO dto) throws IOException, NullPointerException;

    void deleteBoard(Long id) throws NullPointerException;
}
