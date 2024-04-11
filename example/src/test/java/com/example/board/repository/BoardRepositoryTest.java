package com.example.board.repository;

import com.example.board.domain.Board;
import com.example.board.dto.BoardDetailDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class BoardRepositoryTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    void findById() {
        Board board = em.createQuery("select b from Board b left join fetch b.files join fetch b.member where b.id = :boardNo", Board.class)
                .setParameter("boardNo", 1)
                .getSingleResult();

        log.info("board.member : {}", board.getMember().getMemberId());
        log.info("board.files : {}", board.getFiles().size());
    }

    @Test
    void find() {
        em.createQuery("select new com.example.board.dto.BoardDetailDTO(b.id, b.boardTitle, b.boardContent, b.boardHit, b.member.memberId, b.registerDate) from Board b where b.id = :boardNo", BoardDetailDTO.class)
                .setParameter("boardNo", 1)
                .getSingleResult();
    }
}