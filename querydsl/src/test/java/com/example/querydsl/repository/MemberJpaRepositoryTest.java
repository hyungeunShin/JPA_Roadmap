package com.example.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDTO;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).orElseThrow(NullPointerException::new);
        assertThat(findMember).isEqualTo(member);

        List<Member> list1 = memberJpaRepository.findAll();
        assertThat(list1).containsExactly(member);

        List<Member> list2 = memberJpaRepository.findByUsername("member1");
        assertThat(list2).containsExactly(member);
    }

    @Test
    void basicQuerydslTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).orElseThrow(NullPointerException::new);
        assertThat(findMember).isEqualTo(member);

        List<Member> list1 = memberJpaRepository.findAll_Querydsl();
        assertThat(list1).containsExactly(member);

        List<Member> list2 = memberJpaRepository.findByUsername_Querydsl("member1");
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

        List<MemberTeamDTO> list = memberJpaRepository.searchByBuilder(condition);
        assertThat(list).extracting("username").containsExactly("member4");
    }
}