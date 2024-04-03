package hello.jpql.basic;

import hello.jpql.domain.Member;
import hello.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Commit
@Slf4j
class SubQueryTest {
    /*
    서브 쿼리
        • [NOT] EXISTS: 서브쿼리에 결과가 존재하면 참
            • {ALL | ANY | SOME}
            • ALL: 모두 만족하면 참
            • ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
        • [NOT] IN: 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

    JPA 서브 쿼리 한계
        • JPA 는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
        • SELECT 절도 가능(하이버네이트에서 지원)
        • FROM 절의 서브 쿼리는 현재 JPQL 에서 불가능(하이버네이트6 부터는 FROM 절의 서브쿼리를 지원)
            • 조인으로 풀 수 있으면 풀어서 해결
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    void test1() {
        Team team = new Team();
        team.setName("팀");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select m from Member m where exists (select t from m.team t where t.name = '팀')";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        resultList.forEach(m -> {
            log.info("member : {}", m);
        });
    }

    @Test
    void test2() {
        Team team = new Team();
        team.setName("팀");
        em.persist(team);

        Member member1 = new Member("홍길동", 10);
        member1.setTeam(team);

        Member member2 = new Member("홍길동", 20);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        String query = "select m from Member m where m.team = any (select t from Team t)";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        resultList.forEach(m -> {
            log.info("member : {}", m);
        });
    }
}
