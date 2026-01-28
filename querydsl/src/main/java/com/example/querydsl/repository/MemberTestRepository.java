package com.example.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.entity.Member;
import com.example.querydsl.repository.support.Querydsl4RepositorySupport;

import java.util.List;

import static com.example.querydsl.entity.QMember.member;
import static com.example.querydsl.entity.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {
    public MemberTestRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect() {
        return select(member).from(member).fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member).fetch();
    }

    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {
        JPAQuery<Member> query = selectFrom(member).leftJoin(member.team, team)
                                                   .where(usernameEq(condition.getUsername())
                                                          , teamNameEq(condition.getTeamName())
                                                          , ageGoe(condition.getAgeGoe())
                                                          , ageLoe(condition.getAgeLoe()))
                                                   .orderBy(member.id.asc());

        List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    public Page<Member> applyPagination1(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable
                , query -> query.selectFrom(member)
                                .leftJoin(member.team, team)
                                .where(usernameEq(condition.getUsername())
                                       , teamNameEq(condition.getTeamName())
                                       , ageGoe(condition.getAgeGoe())
                                       , ageLoe(condition.getAgeLoe()))
                                .orderBy(member.id.asc())
        );
    }

    public Page<Member> applyPagination2(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable
                , contentQuery -> contentQuery.selectFrom(member)
                                              .leftJoin(member.team, team)
                                              .where(usernameEq(condition.getUsername())
                                                     , teamNameEq(condition.getTeamName())
                                                     , ageGoe(condition.getAgeGoe())
                                                     , ageLoe(condition.getAgeLoe()))
                                              .orderBy(member.id.asc())
                , countQuery -> countQuery.selectFrom(member)
                                          .leftJoin(member.team, team)
                                          .where(usernameEq(condition.getUsername())
                                                 , teamNameEq(condition.getTeamName())
                                                 , ageGoe(condition.getAgeGoe())
                                                 , ageLoe(condition.getAgeLoe()))
        );
    }

    private BooleanExpression usernameEq(String username) {
        return !StringUtils.hasText(username) ? null : member.username.eq(username);
    }

    private BooleanExpression teamNameEq(String teamName) {
        return !StringUtils.hasText(teamName) ? null : team.name.eq(teamName);
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe == null ? null : member.age.goe(ageGoe);
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }
}
