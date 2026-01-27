package com.example.jpql.basic;

import com.example.jpql.domain.Member;
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
class ConditionalExpressionTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("case when then")
    void test1() {
        Member member1 = new Member("홍길동", 10);
        Member member2 = new Member("홍길동", 30);
        Member member3 = new Member("홍길동", 60);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        em.flush();
        em.clear();

        String query = "select case when m.age <= 10 then '학생' " +
                "                   when m.age >= 60 then '경로'" +
                "                   else '일반' end as text from Member m";

        List<String> resultList = em.createQuery(query, String.class).getResultList();
        resultList.forEach(s -> log.info("s : {}", s));
    }

    @Test
    @DisplayName("coalesce nullif")
    void test2() {
        Member member = new Member(null, 10);
        em.persist(member);

        em.flush();
        em.clear();

        //String query = "select coalesce(m.name, '이름 없음') from Member m";
        String query = "select nullif('이름', '이름 없음') from Member m";

        List<String> resultList = em.createQuery(query, String.class).getResultList();
        resultList.forEach(s -> log.info("s : {}", s));
    }
}
