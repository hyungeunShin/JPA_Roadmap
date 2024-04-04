package hello.jpql.basic;

import hello.jpql.domain.Member;
import hello.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Commit
@Slf4j
class GeneralFunctionTest {
    /*
    JPQL 기본 함수
        • COALESCE, NULLIF
        • CONCAT
        • SUBSTRING
        • TRIM
        • LOWER, UPPER
        • LENGTH
        • LOCATE
        • ABS, SQRT, MOD
        • SIZE, INDEX(JPA 용도)

    사용자 정의 함수 호출
        • 하이버네이트는 사용전 방언에 추가해야 한다.
        • 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다.
          select function('group_concat', i.name) from Item i
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("문자열 합치기")
    void test1() {
        Member member = new Member(null, 10);
        em.persist(member);

        em.flush();
        em.clear();

        //String query = "select concat('a', 'b') from Member m";
        String query = "select 'a' || 'b' from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();

        resultList.forEach(s -> {
            log.info("s : {}", s);
        });
    }

    @Test
    @DisplayName("문자열 찾기")
    void test2() {
        Member member = new Member(null, 10);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select locate('de', 'abcdef') from Member m";
        List<Integer> resultList = em.createQuery(query, Integer.class).getResultList();

        resultList.forEach(i -> {
            log.info("i : {}", i);
        });
    }

    @Test
    @DisplayName("컬렉션 크기")
    void test3() {
        Team team = new Team();
        team.setName("팀");
        em.persist(team);

        Member member1 = new Member(null, 10);
        Member member2 = new Member(null, 20);
        member1.setTeam(team);
        member2.setTeam(team);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        String query = "select size(t.members) from Team t";
        List<Integer> resultList = em.createQuery(query, Integer.class).getResultList();

        resultList.forEach(i -> {
            log.info("i : {}", i);
        });
    }

    @Test
    @DisplayName("사용자 정의 함수")
    void test4() {
        Member member1 = new Member("홍길동", 10);
        Member member2 = new Member("박길동", 20);
        
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();
        
        //datasource mysql 로 변경
        //스프링부트 3.0 부터 따로 함수를 등록하지 않아도 사용 가능한듯
        //String query = "select function('fn_test') from Member m";

        String query = "select function('group_concat', m.name) from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();

        resultList.forEach(s -> {
            log.info("s : {}", s);
        });
    }
}
