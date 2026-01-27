package com.example.jpa.Type;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@Commit
@Transactional
@SpringBootTest
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

        em.flush();

        CollectionTest findT = em.find(CollectionTest.class, test.getId());
        Assertions.assertThat(findT.getFavoriteFoods()).contains("가", "나", "라");
        Assertions.assertThat(findT.getAddressHistory()).containsExactly(new Address("111", "222", "333"), new Address("777", "888", "999"));
    }
}
