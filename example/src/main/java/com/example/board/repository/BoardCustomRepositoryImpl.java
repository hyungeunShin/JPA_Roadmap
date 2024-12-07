package com.example.board.repository;

import com.example.board.domain.QBoard;
import com.example.board.dto.BoardListDTO;
import com.example.board.dto.QBoardListDTO;
import com.example.user.domain.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

public class BoardCustomRepositoryImpl implements BoardCustomRepository {
    private final JPAQueryFactory queryFactory;

    public BoardCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<BoardListDTO> findAllWithPaging(Pageable pageable, String searchType, String searchWord) {
        QUser user = QUser.user;
        QBoard board = QBoard.board;

        BooleanBuilder builder = new BooleanBuilder();

        if("title".equals(searchType) && StringUtils.hasText(searchWord)) {
            builder.and(board.boardTitle.like("%" + searchWord + "%"));
        } else if("content".equals(searchType) && StringUtils.hasText(searchWord)) {
            builder.and(board.boardContent.like("%" + searchWord + "%"));
        } else if("writer".equals(searchType) && StringUtils.hasText(searchWord)) {
            builder.and(user.username.like("%" + searchWord + "%"));
        }

        List<BoardListDTO> content = queryFactory.select(new QBoardListDTO(Expressions.numberTemplate(Integer.class, "ROW_NUMBER() OVER(ORDER BY {0} DESC)", board.id).as("rn")
                                                                           , board.id, board.boardTitle, board.boardHit, user.username, board.registerDate))
                                                 .from(board)
                                                 .join(board.user, user)
                                                 .where(builder)
                                                 .limit(pageable.getPageSize())
                                                 .offset(pageable.getOffset())
                                                 .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(board.count())
                                                .from(board)
                                                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
