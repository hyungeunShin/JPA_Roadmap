package com.example.board.repository;

import com.example.board.domain.Board;
import com.example.board.domain.QBoard;
import com.example.board.dto.*;
import com.example.common.domain.AttachFile;
import com.example.member.domain.QMember;
import com.example.util.PaginationInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BoardRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public BoardRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Board> boardList(PaginationInfo<BoardListDTO> paginationInfo) {
        String searchType = paginationInfo.getSearchType();
        String searchWord = paginationInfo.getSearchWord();

        QBoard board = QBoard.board;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if("title".equals(searchType)) {
            builder.and(board.boardTitle.like("%" + searchWord + "%"));
        } else if("content".equals(searchType)) {
            builder.and(board.boardContent.like("%" + searchWord + "%"));
        } else if("writer".equals(searchType)) {
            builder.and(member.memberId.like("%" + searchWord + "%"));
        }

        return query.select(board)
                    .from(board)
                    .join(board.member, member)
                    .fetchJoin()
                    .where(builder)
                    .orderBy(board.id.desc())
                    .limit(paginationInfo.getScreenSize())
                    .offset(paginationInfo.getStartRow() - 1)
                    .fetch();
    }

    public Long boardTotalCount(PaginationInfo<BoardListDTO> paginationInfo) {
        String searchType = paginationInfo.getSearchType();
        String searchWord = paginationInfo.getSearchWord();

        QBoard board = QBoard.board;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if("title".equals(searchType)) {
            builder.and(board.boardTitle.like("%" + searchWord + "%"));
        } else if("content".equals(searchType)) {
            builder.and(board.boardContent.like("%" + searchWord + "%"));
        } else if("writer".equals(searchType)) {
            builder.and(member.memberId.like("%" + searchWord + "%"));
        }

        return query.select(board.count())
                    .from(board)
                    .join(board.member, member)
                    .where(builder)
                    .fetchOne();
    }

    public Long save(Board board) {
        em.persist(board);
        return board.getId();
    }

    public Optional<Board> findBoardDTOById(Long boardNo) {
        return em.createQuery("select b from Board b join fetch b.member left join fetch b.files where b.id = :boardNo", Board.class)
                 .setParameter("boardNo", boardNo)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<Board> findBoardById(Long boardNo) {
        return em.createQuery("select b from Board b where b.id = :boardNo", Board.class)
                 .setParameter("boardNo", boardNo)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<AttachFile> findAttachFileById(Long fileNo) {
        return em.createQuery("select f from AttachFile f where f.id = :fileNo", AttachFile.class)
                 .setParameter("fileNo", fileNo)
                 .getResultStream()
                 .findFirst();
    }
}