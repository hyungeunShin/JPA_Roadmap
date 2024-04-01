package hello.jpa.KeyStrategy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
class KeyTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("IDENTITY 전략")
    void identity() {
        //@GeneratedValue 전력이 IDENTITY 일 때
        KeyIdentity key = new KeyIdentity();
        key.setName("홍길동");

        //em.persist()할 때 insert 실행
        System.out.println("==========");
        em.persist(key);
        System.out.println("==========");
    }

    @Test
    @DisplayName("SEQUENCE 전략")
    void sequence() {
        //@GeneratedValue 전력이 SEQUENCE 일 때
        KeySequence key1 = new KeySequence();
        key1.setName("A");

        KeySequence key2 = new KeySequence();
        key2.setName("B");

        KeySequence key3 = new KeySequence();
        key3.setName("C");

        /*
        JPA 는 em.persist() 를 할 때 DB에 접근하여 sequence 값을 가져온다.
        JPA 가 sequence 에 접근 횟수를 줄이기 위해 allocationSize 를 활용한다.
        allocationSize 에 설정 값 만큼 DB에 sequence 를 증가 시키고 그만큼 Memory 에 sequence 값을 할당한다.
        */
        System.out.println("==========");
        em.persist(key1);
        em.persist(key2);
        em.persist(key3);
        System.out.println("==========");
    }
}
