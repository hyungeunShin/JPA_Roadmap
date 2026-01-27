package com.example.jpql.advanced;

import com.example.jpql.domain.Member;
import com.example.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Commit
@Transactional
@SpringBootTest
class PathExpressionTest {
    /*
    JPQL - 경로 표현식
        • .(점)을 찍어 객체 그래프를 탐색하는 것
            select m.username   --> 상태 필드
              from Member m
              join m.team t     --> 단일 값 연관 필드
              join m.orders o   --> 컬렉션 값 연관 필드
             where t.name = '팀A'
        • 상태 필드(state field): 단순히 값을 저장하기 위한 필드(ex: m.username)
        • 연관 필드(association field): 연관관계를 위한 필드
            • 단일 값 연관 필드: @ManyToOne, @OneToOne, 대상이 엔티티(ex: m.team)
            • 컬렉션 값 연관 필드: @OneToMany, @ManyToMany, 대상이 컬렉션(ex: m.orders)

    경로 표현식 특징
        • 상태 필드(state field): 경로 탐색의 끝, 탐색X
        • 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색O
        • 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색X
            • FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능

    명시직 조인, 묵시적 조인
        • 명시적 조인: join 키워드 직접 사용
            • select m from Member m join m.team t
        • 묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL 조인 발생(내부 조인만 가능)
            • select m.team from Member m

    경로 탐색을 사용한 묵시적 조인 시 주의사항
        • 항상 내부 조인
        • 컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야함
        • 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만 묵시적 조인으로 인해 SQL 의 FROM (JOIN) 절에 영향을 줌

    ★★★ 가급적 묵시적 조인 대신에 명시적 조인 사용 ★★★
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("상태 필드")
    void test1() {
        Member member = new Member("홍길동", 10);
        em.persist(member);

        em.flush();
        em.clear();

        //쿼리 그대로 실행
        String query = "select m.name from Member m";

        List<String> resultList = em.createQuery(query, String.class).getResultList();
        resultList.forEach(s -> log.info("s : {}", s));
    }

    @Test
    @DisplayName("단일 값 연관경로")
    void test2() {
        Team team = new Team();
        team.setName("팀");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        //묵시적 내부 조인 발생(쿼리랑 다르게 조인 발생)
        //추가 탐색 가능(m.team 에서 id, name, ... 으로 갈 수 있다)
        String query = "select m.team from Member m";

        List<Team> resultList = em.createQuery(query, Team.class).getResultList();
        resultList.forEach(t -> log.info("t : {}", t));
    }

    @Test
    @DisplayName("컬렉션 값 연관경로")
    void test3() {
        Team team1 = new Team();
        team1.setName("팀1");
        Team team2 = new Team();
        team2.setName("팀2");

        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("홍길동", 10);
        member1.setTeam(team1);
        Member member2 = new Member("박길동", 20);
        member2.setTeam(team2);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        //묵시적 내부 조인 발생(쿼리랑 다르게 조인 발생)
        //추가 탐색 불가능
        //String query = "select t.members from Team t";
        //List<Collection> result = em.createQuery(query, Collection.class).getResultList();
        //log.info("result : {}", result);

        //String query = "select t.members.size from Team t";   //에러
        //String query = "select size(t.members) from Team t";  //hibernate 6 부터 바뀐듯
        //Integer result = em.createQuery(query, Integer.class).getSingleResult();
        //log.info("result : {}", result);

        //추가적인 탐색을 하고 싶으면? 명시적 조인을 통해 별칭을 얻어서 별칭을 통해 탐색
        String query = "select m.name from Team t join t.members m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
        resultList.forEach(s -> log.info("s : {}", s));
    }
}
