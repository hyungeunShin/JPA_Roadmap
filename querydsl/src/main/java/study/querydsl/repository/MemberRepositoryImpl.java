package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDTO;
import study.querydsl.dto.QMemberTeamDTO;
import study.querydsl.entity.Member;

import java.util.List;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

public class MemberRepositoryImpl /*extends QuerydslRepositorySupport*/ implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /*
    QuerydslRepositorySupport
        장점
            - getQuerydsl().applyPagination() 스프링 데이터가 제공하는 페이징을 Querydsl 로 편리하게 변환 가능(단! Sort 는 오류발생)
            - from() 으로 시작 가능(QueryFactory 를 사용해서 select() 로 시작하는 것이 더 명시적)
            - EntityManager 제공
        한계
            - Querydsl 3.x 버전을 대상으로 만듬
            - Querydsl 4.x에 나온 JPAQueryFactory 로 시작할 수 없음
            - select 로 시작할 수 없음 (from 으로 시작해야함)
            - QueryFactory 를 제공하지 않음
            - 스프링 데이터 Sort 기능이 정상 동작하지 않음
    
    Querydsl4RepositorySupport.class 참고
    
    //extends QuerydslRepositorySupport
    public MemberRepositoryImpl() {
        super(Member.class);
    }
    */

    @Override
    public List<MemberTeamDTO> search(MemberSearchCondition condition) {
        return queryFactory.select(new QMemberTeamDTO(member.id, member.username, member.age, team.id, team.name))
                           .from(member)
                           .leftJoin(member.team, team)
                           .where(usernameEq(condition.getUsername())
                                  , teamNameEq(condition.getTeamName())
                                  , ageGoe(condition.getAgeGoe())
                                  , ageLoe(condition.getAgeLoe()))
                           .fetch();
    }

    /*
    //extends QuerydslRepositorySupport
    @Override
    public List<MemberTeamDTO> search(MemberSearchCondition condition) {
        return from(member).leftJoin(member.team, team)
                           .where(usernameEq(condition.getUsername())
                                  , teamNameEq(condition.getTeamName())
                                  , ageGoe(condition.getAgeGoe())
                                  , ageLoe(condition.getAgeLoe()))
                           .select(new QMemberTeamDTO(member.id, member.username, member.age, team.id, team.name))
                           .fetch();
    }
    */

    @Override
    public Page<MemberTeamDTO> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        //Querydsl 이 제공하는 fetchResults() 를 사용하면 내용과 전체 카운트를 한번에 조회할 수 있다.(실제 쿼리는 2번 호출)
        //fetchResult() 는 카운트 쿼리 실행시 필요없는 order by 는 제거한다.
        QueryResults<MemberTeamDTO> results = queryFactory.select(new QMemberTeamDTO(member.id, member.username, member.age, team.id, team.name))
                                                          .from(member)
                                                          .leftJoin(member.team, team)
                                                          .where(usernameEq(condition.getUsername())
                                                                 , teamNameEq(condition.getTeamName())
                                                                 , ageGoe(condition.getAgeGoe())
                                                                 , ageLoe(condition.getAgeLoe()))
                                                          .orderBy(member.id.asc())
                                                          .offset(pageable.getOffset())
                                                          .limit(pageable.getPageSize())
                                                          .fetchResults();  //fetchResults() is deprecated

        List<MemberTeamDTO> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    /*
    //extends QuerydslRepositorySupport
    @Override
    public Page<MemberTeamDTO> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        JPQLQuery<MemberTeamDTO> query = from(member).leftJoin(member.team, team)
                                                     .where(usernameEq(condition.getUsername())
                                                            , teamNameEq(condition.getTeamName())
                                                            , ageGoe(condition.getAgeGoe())
                                                            , ageLoe(condition.getAgeLoe()))
                                                     .select(new QMemberTeamDTO(member.id, member.username, member.age, team.id, team.name))
                                                     .orderBy(member.id.asc());

        QueryResults<MemberTeamDTO> results = getQuerydsl().applyPagination(pageable, query).fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
    */

    @Override
    public Page<MemberTeamDTO> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        //전체 카운트를 조회 하는 방법을 최적화 할 수 있으면 이렇게 분리하면 된다. (예를 들어서 전체 카운트를 조회할 때 조인 쿼리를 줄일 수 있다면 상당한 효과가 있다.)
        //코드를 리펙토링해서 내용 쿼리과 전체 카운트 쿼리를 읽기 좋게 분리하면 좋다.
        List<MemberTeamDTO> content = queryFactory.select(new QMemberTeamDTO(member.id, member.username, member.age, team.id, team.name))
                                                  .from(member)
                                                  .leftJoin(member.team, team)
                                                  .where(usernameEq(condition.getUsername())
                                                         , teamNameEq(condition.getTeamName())
                                                         , ageGoe(condition.getAgeGoe())
                                                         , ageLoe(condition.getAgeLoe()))
                                                  .orderBy(member.id.asc())
                                                  .offset(pageable.getOffset())
                                                  .limit(pageable.getPageSize())
                                                  .fetch();

//        long total = queryFactory.select(member)
//                                 .from(member)
//                                 .leftJoin(member.team, team)
//                                 .where(usernameEq(condition.getUsername())
//                                        , teamNameEq(condition.getTeamName())
//                                        , ageGoe(condition.getAgeGoe())
//                                        , ageLoe(condition.getAgeLoe()))
//                                 .fetchCount();

//        return new PageImpl<>(content, pageable, total);

        /*
        CountQuery 최적화
        - 스프링 데이터 라이브러리가 제공 -> PageableExecutionUtils
        - count 쿼리가 생략 가능한 경우 생략해서 처리
            - 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
            - 마지막 페이지일 때(offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함, 더 정확히는 마지막 페이지이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때)
        */
//        JPAQuery<Member> countQuery = queryFactory.select(member)
//                                                  .from(member)
//                                                  .leftJoin(member.team, team)
//                                                  .where(usernameEq(condition.getUsername())
//                                                         , teamNameEq(condition.getTeamName())
//                                                         , ageGoe(condition.getAgeGoe())
//                                                         , ageLoe(condition.getAgeLoe()));

//        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount); //fetchCount() is deprecated

        JPAQuery<Long> countQuery = queryFactory.select(member.count())
                                                .from(member)
                                                .leftJoin(member.team, team)
                                                .where(usernameEq(condition.getUsername())
                                                       , teamNameEq(condition.getTeamName())
                                                       , ageGoe(condition.getAgeGoe())
                                                       , ageLoe(condition.getAgeLoe()));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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
