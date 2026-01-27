package com.example.jpa.relation;

import com.example.jpa.BasicAndRelation.Member;
import com.example.jpa.BasicAndRelation.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Commit
@Transactional
@SpringBootTest
class RelationTest {
    @PersistenceContext
    private EntityManager em;

    private static Long memSeq = 0L;
    private static Long teamSeq = 0L;

    @Test
    @DisplayName("테이블 기반 모델링")
    void modelingBasedTable() {
        Team team = new Team(++teamSeq, "TeamA");
        em.persist(team);

        Member member = new Member(++memSeq, "홍길동");
        //member.setTeamId(team.getId());
        em.persist(member);

        em.clear();

        /*
        객체를 테이블에 맞추어 데이터 중심으로 모델링하면 협력 관계를 만들 수 없다.
            • 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾는다.
            • 객체는 참조를 사용해서 연관된 객체를 찾는다.
            • 테이블과 객체 사이에는 이런 큰 간격이 있다.
        */
        Member findMember = em.find(Member.class, member.getId());
        //Team findTeam = em.find(Team.class, findMember.getTeamId());
    }

    @Test
    @DisplayName("객체 기반 모델링")
    void modelingBasedObject() {
        Team teamA = new Team(++teamSeq, "TeamA");
        em.persist(teamA);

        Member member = new Member(++memSeq, "홍길동");
        member.setTeam(teamA);
        em.persist(member);

        em.flush();
        em.clear();

        Member findMember = em.find(Member.class, member.getId());
        Team findTeam = findMember.getTeam();
        log.info("findTeam : {}", findTeam.getName());
        findTeam.getMembers().forEach(m -> log.info("memberName : {}", m.getName()));

        Team teamB = new Team(++teamSeq, "TeamB");
        em.persist(teamB);
        findMember.setTeam(teamB);
    }

    @Test
    @DisplayName("관계 주인이 잘못된")
    void wrongRelationOwner() {
        Member member = new Member(++memSeq, "홍길동");
        em.persist(member);

        Team team = new Team(++teamSeq, "TeamA");
        team.getMembers().add(member);
        em.persist(team);

        em.flush();
        em.clear();

        Member findMember = em.find(Member.class, member.getId());
        log.info("findMember.id : {}", findMember.getId());
        //Member 의 team_id는 null
        //log.info("findMember.team_id : {}", findMember.getTeam().getId());
        Assertions.assertThatThrownBy(() -> findMember.getTeam().getId()).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("올바른 관계 주인")
    void correctRelationOwner() {
        Team team = new Team(++teamSeq, "TeamA");
        em.persist(team);

        Member member = new Member(++memSeq, "홍길동");
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        Member findMember = em.find(Member.class, member.getId());
        log.info("findMember.id : {}", findMember.getId());
        log.info("findMember.team_id : {}", findMember.getTeam().getId());
    }

    @Test
    @DisplayName("관계 매핑 시 주의점")
    void relationCaution() {
        Team team = new Team(++teamSeq, "TeamA");
        em.persist(team);

        Member member = new Member(++memSeq, "홍길동");
        member.setTeam(team);
        em.persist(member);

        //team.getMembers().add(member);

        //em.flush();
        //em.clear();

        /*
        em.clear() 를 하지 않으면 1차 캐시에서 조회한다.
        team.getMembers().add(member); 를 하지 않으면 team 의 members 는 비어있다.
        그렇기 때문에 순수객체 상태를 고려하여 양방향 연결을 했으면 양쪽에 값을 세팅해야 한다.
        */
        Team findTeam = em.find(Team.class, team.getId());
        log.info("==========");
        findTeam.getMembers().forEach(m -> log.info("member : {}", m.getName()));
        log.info("==========");
    }
}
