package com.example.board.repository;

import com.example.board.dto.BoardListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardCustomRepository {
    Page<BoardListDTO> findAllWithPaging(Pageable pageable, String searchType, String searchWord);
}
