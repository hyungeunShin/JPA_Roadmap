package com.example.jpa.basic;

import com.example.jpa.BasicAndRelation.Member;
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
class BasicTest {
    @PersistenceContext
    private EntityManager em;

    private static Long sequence = 0L;

    @Test
    @DisplayName("JPA 캐시")
    void cache() {
        Member member = new Member(++sequence, "홍길동");
        em.persist(member);

        //select 쿼리 안나타남
        Member selectMember = em.find(Member.class, member.getId());

        log.info("아이디 : {}", selectMember.getId());
        log.info("이름 : {}", selectMember.getName());

        em.flush();
        em.clear();

        //처음 한번만 select 쿼리가 나타남
        em.find(Member.class, member.getId());   //DB 에서 데이터를 찾고 영속성 컨텍스트에 등록
        em.find(Member.class, member.getId());   //1차캐시에서 조회
    }

    @Test
    @DisplayName("JPA 더티체킹")
    void dirtyCheck() {
        Member member = new Member(++sequence, "홍길동");
        em.persist(member);

        em.flush();
        em.clear();

        //영속성 컨텍스트 내에서 처음 상태의 Entity 에 대한 스냅샷을 보관
        Member findMember = em.find(Member.class, member.getId());
        findMember.setName("가나다");

        //스냅샷과 비교하여 변경된 부분이 있으면 트랜잭션이 끝나는 시점에 Update Query 수행하여 DB에 반영
    }

    @Test
    @DisplayName("JPA Flush")
    void jpa_flush() {
        Member member = new Member(++sequence, "홍길동");
        em.persist(member);
        //Insert 쿼리 수행
        em.flush();

        log.info("==========");
    }
    
    @Test
    @DisplayName("JPA 동일성 보장")
    void identity() {
        Member member = new Member(++sequence, "홍길동");
        em.persist(member);

        Member selectMember1 = em.find(Member.class, member.getId());
        Member selectMember2 = em.find(Member.class, member.getId());

        //1차 캐시를 통해 같은 식별자(@Id 값)에 대해 매번 같은 인스턴스에 접근하게 되므로 동일성이 보장
        log.info("selectMember1 == selectMember2 : {}", selectMember1 == selectMember2);
    }

    @Test
    @DisplayName("JPA 쓰기 지연")
    void write_behind() {
        Member member1 = new Member(++sequence, "홍길동");

        log.info("== member1 before ==");
        //쓰기 지연 SQL 저장소에 쿼리 저장
        em.persist(member1);
        log.info("== member1 after ==");

        Member member2 = new Member(++sequence, "박길동");

        log.info("== member2 before ==");
        //쓰기 지연 SQL 저장소에 쿼리 저장
        em.persist(member2);
        log.info("== member2 after ==");

        //커밋하는 순간 데이터베이스에 쓰기 지연 SQL 저장소에 있는 쿼리를 날린다.
    }
}
