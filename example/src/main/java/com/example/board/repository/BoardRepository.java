package com.example.board.repository;

import com.example.board.domain.Board;
import com.example.board.domain.QBoard;
import com.example.board.dto.BoardListDTO;
import com.example.user.domain.QUser;
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

    public List<Board> findAll(PaginationInfo<BoardListDTO> paginationInfo) {
        String searchType = paginationInfo.getSearchType();
        String searchWord = paginationInfo.getSearchWord();

        QBoard board = QBoard.board;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if("title".equals(searchType)) {
            builder.and(board.boardTitle.like("%" + searchWord + "%"));
        } else if("content".equals(searchType)) {
            builder.and(board.boardContent.like("%" + searchWord + "%"));
        } else if("writer".equals(searchType)) {
            builder.and(user.username.like("%" + searchWord + "%"));
        }

        return query.select(board)
                    .from(board)
                    .join(board.user, user)
                    .fetchJoin()
                    .where(builder)
                    .orderBy(board.id.desc())
                    .limit(paginationInfo.getScreenSize())
                    .offset(paginationInfo.getStartRow())
                    .fetch();
    }

    public Long findAllTotalCount(PaginationInfo<BoardListDTO> paginationInfo) {
        String searchType = paginationInfo.getSearchType();
        String searchWord = paginationInfo.getSearchWord();

        QBoard board = QBoard.board;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if("title".equals(searchType)) {
            builder.and(board.boardTitle.like("%" + searchWord + "%"));
        } else if("content".equals(searchType)) {
            builder.and(board.boardContent.like("%" + searchWord + "%"));
        } else if("writer".equals(searchType)) {
            builder.and(user.username.like("%" + searchWord + "%"));
        }

        return query.select(board.count())
                    .from(board)
                    .join(board.user, user)
                    .where(builder)
                    .fetchOne();
    }

    public Long save(Board board) {
        em.persist(board);
        return board.getId();
    }

    public Optional<Board> findById(Long id) {
        return em.createQuery("select b from Board b join fetch b.user left join fetch b.files where b.id = :id", Board.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public void remove(Board board) {
        em.remove(board);
    }
}