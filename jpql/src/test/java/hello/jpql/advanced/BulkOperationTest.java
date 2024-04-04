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

@SpringBootTest
@Transactional
@Commit
@Slf4j
class BulkOperationTest {
    /*
    벌크 연산 예제
        • 쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
        • executeUpdate()의 결과는 영향받은 엔티티 수 반환
        • UPDATE, DELETE 지원
        • INSERT(insert into .. select, 하이버네이트 지원)

    벌크 연산 주의
        • 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
            • 벌크 연산을 먼저 수행
            • 벌크 연산 수행 후 영속성 컨텍스트 초기화
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("벌크 연산")
    void test1() {
        Member member1 = new Member("홍길동", 10);
        Member member2 = new Member("홍길동", 10);
        em.persist(member1);
        em.persist(member2);

        //영속성 컨텍스트를 무시하고 db에 직접 쿼리 실행
        String query = "update Member m set m.age = 20 where m.id = :id";
        int result = em.createQuery(query).setParameter("id", member1.getId()).executeUpdate();
        //flush 자동 호출
        log.info("영향을 받은 결과 개수 : {}", result);

        //주의
        //Member{id=1, name='홍길동', age=10}
        Member m1 = em.find(Member.class, member1.getId());
        log.info("m1 : {}", m1);

        em.clear();

        //벌크 연산을 한 후에 영속성 컨텍스트 초기화 필요
        Member m2 = em.find(Member.class, member1.getId());
        log.info("m2 : {}", m2);
    }
}
