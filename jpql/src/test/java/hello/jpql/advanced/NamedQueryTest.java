package hello.jpql.advanced;

import hello.jpql.domain.Member;
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
class NamedQueryTest {
    /*
    Named 쿼리 - 정적 쿼리
        • 미리 정의해서 이름을 부여해두고 사용하는 JPQL
        • 정적 쿼리
        • 어노테이션, XML 에 정의
        • 애플리케이션 로딩 시점에 초기화 후 재사용
        • 애플리케이션 로딩 시점에 쿼리를 검증
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("어노테이션 네임드 쿼리")
    void test1() {
        Member member = new Member("홍길동", 10);
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> resultList = em.createNamedQuery("Member.findByName", Member.class)
                                    .setParameter("name", "홍길동").getResultList();
        resultList.forEach(m -> {
            log.info("m : {}", m);
        });
    }
}
