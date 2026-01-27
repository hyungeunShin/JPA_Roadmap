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

@Slf4j
@Commit
@Transactional
@SpringBootTest
class UseEntityDirectTest {
    /*
    엔티티 직접 사용
        • JPQL 에서 엔티티를 직접 사용하면 SQL 에서 해당 엔티티의 기본 키 값을 사용
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("기본키 값")
    void test1() {
        Member member = new Member("홍길동", 10);
        em.persist(member);

        em.flush();
        em.clear();

        //select m1_0.id, m1_0.age, m1_0.name, m1_0.team_id, m1_0.type from member m1_0 where m1_0.id=?
        String query = "select m from Member m where m = :member";
        Member m = em.createQuery(query, Member.class).setParameter("member", member).getSingleResult();
        log.info("m : {}", m);
    }

    @Test
    @DisplayName("외래키 값")
    void test2() {
        Team team = new Team();
        team.setName("팀");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        //select m1_0.id, m1_0.age, m1_0.name, m1_0.team_id, m1_0.type from member m1_0 where m1_0.team_id=?
        String query = "select m from Member m where m.team = :team";
        Member m = em.createQuery(query, Member.class).setParameter("team", team).getSingleResult();
        log.info("m : {}, m.team : {}", m, m.getTeam());
    }
}
