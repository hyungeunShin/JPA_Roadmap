package hello.jpa.Type;

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
class TypeTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("컬렉션 타입")
    void collectionTest() {
        CollectionTest test = new CollectionTest();
        test.getFavoriteFoods().add("가");
        test.getFavoriteFoods().add("나");
        test.getFavoriteFoods().add("다");

        test.getAddressHistory().add(new Address("111", "222", "333"));
        test.getAddressHistory().add(new Address("444", "555", "666"));

        em.persist(test);

        em.flush();
        em.clear();

        //지연로딩이 기본값
        CollectionTest t = em.find(CollectionTest.class, test.getId());

        t.getFavoriteFoods().remove("다");
        t.getFavoriteFoods().add("라");

        //equals 와 hashcode 필요
        t.getAddressHistory().remove(new Address("444", "555", "666"));
        t.getAddressHistory().add(new Address("777", "888", "999"));
    }
}
