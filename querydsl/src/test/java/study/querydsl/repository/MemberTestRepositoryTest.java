package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
class MemberTestRepositoryTest {
    @Autowired
    MemberTestRepository repository;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void before() {
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
    }

    @Test
    void test1() {
        List<Member> list1 = repository.basicSelect();
        log.info("{}", list1);

        List<Member> list2 = repository.basicSelectFrom();
        log.info("{}", list2);
    }

    @Test
    void test2() {
        Page<Member> page1 = repository.searchPageByApplyPage(new MemberSearchCondition(), PageRequest.of(0, 3));
        Page<Member> page2 = repository.applyPagination1(new MemberSearchCondition(), PageRequest.of(0, 3));
        Page<Member> page3 = repository.applyPagination2(new MemberSearchCondition(), PageRequest.of(0, 3));

        log.info("{}", page1.getContent());
        log.info("{}", page2.getContent());
        log.info("{}", page3.getContent());
    }
}