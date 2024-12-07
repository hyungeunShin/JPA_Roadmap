package com.example.board.service;

import com.example.board.dto.BoardDetailDTO;
import com.example.board.dto.BoardListDTO;
import com.example.board.dto.EditBoardDTO;
import com.example.board.dto.RegisterBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    Page<BoardListDTO> getBoardList(Pageable pageable, String searchType, String searchWord);

    Long saveBoard(RegisterBoardDTO dto, Long userId) throws IOException;

    BoardDetailDTO findBoardDetail(Long id);

    void editBoard(EditBoardDTO dto) throws IOException;

    void deleteBoard(Long id);
}
