package com.example.jpql.basic;

import com.example.jpql.domain.*;
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
class TypeExpressionTest {
    /*
    JPQL 타입 표현
        • 문자: ‘HELLO’, ‘She’’s’
        • 숫자: 10L(Long), 10D(Double), 10F(Float)
        • Boolean: TRUE, FALSE
        • ENUM: com.example.jpql.domain.MemberType.Admin (패키지명 포함)
        • 엔티티 타입: TYPE(i) = Book (상속 관계에서 사용)
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("타입 표현")
    void test1() {
        Member member1 = new Member("홍길동", 10);
        member1.setType(MemberType.ADMIN);

        Member member2 = new Member("박길동", 20);
        member2.setType(MemberType.USER);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        String query = "select m.name, 'HELLO', TRUE from Member m where m.type = com.example.jpql.domain.MemberType.USER";
        List<Object[]> resultList = em.createQuery(query).getResultList();

        resultList.forEach(o -> {
            log.info("object : {}", o[0]);
            log.info("object : {}", o[1]);
            log.info("object : {}", o[2]);
        });
    }

    @Test
    @DisplayName("부모 자식 간 엔티티 타입 표현")
    void test2() {
        Book book = new Book();
        book.setName("책");
        book.setPrice(1000);
        book.setAuthor("작가");

        Album album = new Album();
        album.setPrice(2000);
        album.setName("앨범");
        album.setArtist("작곡가");
        
        em.persist(book);
        em.persist(album);

        em.flush();
        em.clear();

        String query = "select i from Item i where type(i) = Book";
        List<Item> resultList = em.createQuery(query, Item.class).getResultList();

        resultList.forEach(i -> log.info("item : {}", i));
    }

    @Test
    @DisplayName("타입 변환")
    void test3() {
        Book book = new Book();
        book.setName("책");
        book.setPrice(1000);
        book.setAuthor("작가");

        Movie movie = new Movie();
        movie.setName("영화");
        movie.setActor("배우");
        movie.setPrice(10000);

        em.persist(book);
        em.persist(movie);

        em.flush();
        em.clear();

        List<Item> result = em.createQuery("select i from Item i", Item.class).getResultList();
        for(Item item : result) {
            printInfo(item);
        }
    }

    private void printInfo(Item item) {
        if(item instanceof Book b) {
            log.info("b.author : {}", b.getAuthor());
        } else if(item instanceof Album a) {
            log.info("a.artist {}", a.getArtist());
        } else if(item instanceof Movie m) {
            log.info("m.actor : {}", m.getActor());
        }
    }
}
