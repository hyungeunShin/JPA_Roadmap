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
class JoinTest {
    /*
    조인
        • 내부 조인: SELECT m FROM Member m [INNER] JOIN m.team t
        • 외부 조인: SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
        • 세타 조인: select count(m) from Member m, Team t where m.team.id = t.id

    조인 - ON 절
        • ON 절을 활용한 조인(JPA 2.1부터 지원)
            1. 조인 대상 필터링
            2. 연관관계 없는 엔티티 외부 조인(하이버네이트 5.1부터)
    */
    @PersistenceContext
    private EntityManager em;
    
    @Test
    @DisplayName("내부 조인")
    void test1() {
        Team team = new Team();
        team.setName("A");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select m from Member m inner join m.team t";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        resultList.forEach(m -> {
            log.info("member.team_id : {}", m.getTeam().getId());
        });
    }

    @Test
    @DisplayName("외부 조인")
    void test2() {
        Team team = new Team();
        team.setName("B");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select m from Member m left outer join m.team t";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        resultList.forEach(m -> {
            log.info("member.team_id : {}", m.getTeam().getId());
        });
    }

    @Test
    @DisplayName("세타 조인")
    void test3() {
        Team team = new Team();
        team.setName("C");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select m from Member m, Team t where m.team.id = t.id";
        //String query = "select m from Member m, m.team t where m.team.id = t.id"; //에러
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        resultList.forEach(m -> {
            log.info("member.team_id : {}", m.getTeam().getId());
        });
    }

    @Test
    @DisplayName("ON 절을 활용한 조인 대상 필터링")
    void test4() {
        Team team = new Team();
        team.setName("D");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select m from Member m join m.team t on t.name = 'A'";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();

        resultList.forEach(m -> {
            log.info("member.team_id : {}", m.getTeam().getId());
        });
    }

    @Test
    @DisplayName("ON 절을 활용한 연관관계 없는 엔티티 외부 조인")
    void test5() {
        Team team = new Team();
        team.setName("D");
        em.persist(team);

        Member member = new Member("D", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        String query = "select t from Member m left outer join Team t on m.name = t.name";
        List<Team> resultList = em.createQuery(query, Team.class).getResultList();

        resultList.forEach(t -> {
            log.info("team : {}", t);
        });
    }
}
