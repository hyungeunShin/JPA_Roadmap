package hello.jpql.basic;

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
class PagingTest {
    /*
    페이징 API
        • setFirstResult(int startPosition) : 조회 시작 위치(0부터 시작)
        • setMaxResults(int maxResult) : 조회할 데이터 수
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("페이징 API")
    void test1() {
        for(int i = 0; i < 100; i++) {
            Member member = new Member("Member" + i, i);
            em.persist(member);
        }

        em.flush();
        em.clear();

        List<Member> resultList = em.createQuery("select m from Member m order by m.age", Member.class)
                .setFirstResult(90)
                .setMaxResults(100)
                .getResultList();

        resultList.forEach(m -> {
            log.info("member : {}", m);
        });
    }
}
