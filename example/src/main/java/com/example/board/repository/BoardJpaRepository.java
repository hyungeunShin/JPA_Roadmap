package com.example.board.repository;

import com.example.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardJpaRepository extends JpaRepository<Board, Long>, BoardCustomRepository {
    @Modifying
    @Query("update Board b set b.boardHit = b.boardHit + 1 where b.id = :id")
    void increaseHit(@Param("id") Long id);
}
