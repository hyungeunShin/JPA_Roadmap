package com.example.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDTO;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).orElseThrow(NullPointerException::new);
        assertThat(findMember).isEqualTo(member);

        List<Member> list1 = memberRepository.findAll();
        assertThat(list1).containsExactly(member);

        List<Member> list2 = memberRepository.findByUsername("member1");
        assertThat(list2).containsExactly(member);
    }

    @Test
    void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDTO> list = memberRepository.search(condition);
        assertThat(list).extracting("username").containsExactly("member4");
    }

    @Test
    void searchPageSimpleTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDTO> page = memberRepository.searchPageSimple(new MemberSearchCondition(), pageRequest);

        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
    }

    @Test
    void searchPageComplexTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDTO> page = memberRepository.searchPageComplex(new MemberSearchCondition(), pageRequest);

        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
    }

    @Test
    void queryDSLPredicateExecutorTest() {
        /*
        QuerydslPredicateExecutor 인터페이스
            - public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member>

        한계점
            - 조인X (묵시적 조인은 가능하지만 left join 이 불가능하다.)
            - 클라이언트가 Querydsl 에 의존해야 한다. 서비스 클래스가 Querydsl 이라는 구현 기술에 의존해야 한다.
            - 복잡한 실무환경에서 사용하기에는 한계가 명확하다.

        참고: https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html
        */
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        QMember member = QMember.member;
        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40).and(member.username.eq("member1")));
        result.forEach(m -> log.info("{}", m));
    }
}