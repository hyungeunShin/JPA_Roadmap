package hello.jpql.advanced;

import hello.jpql.domain.Album;
import hello.jpql.domain.Book;
import hello.jpql.domain.Item;
import hello.jpql.domain.Movie;
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
class PolymorphicQueryTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("TYPE 사용")
    void test1() {
        Book book = new Book();
        book.setName("책");
        book.setPrice(10000);
        book.setAuthor("작가");
        book.setIsbn("111111");

        Movie movie = new Movie();
        movie.setName("영화");
        movie.setPrice(20000);
        movie.setActor("배우");
        movie.setDirector("감독");

        Album album = new Album();
        album.setName("앨범");
        album.setPrice(30000);
        album.setArtist("아티스트");

        em.persist(book);
        em.persist(movie);
        em.persist(album);

        em.flush();
        em.clear();

        //select i1_0.id, i1_0.dtype, i1_0.name, i1_0.price, i1_0.artist, ... from item i1_0 where i1_0.dtype in ('Book', 'Movie')
        String query = "select i from Item i where type(i) in (Book, Movie)";
        List<Item> resultList = em.createQuery(query, Item.class).getResultList();
        resultList.forEach(i -> {
            log.info("i : {}", i);
        });
    }

    @Test
    @DisplayName("TREAT 사용")
    void test2() {
        Book book1 = new Book();
        book1.setName("책1");
        book1.setPrice(10000);
        book1.setAuthor("작가1");
        book1.setIsbn("111111");

        Book book2 = new Book();
        book2.setName("책2");
        book2.setPrice(10000);
        book2.setAuthor("작가2");
        book2.setIsbn("222222");

        em.persist(book1);
        em.persist(book2);

        em.flush();
        em.clear();

        String query = "select i from Item i where treat(i as Book).name = '책1'";
        List<Item> resultList = em.createQuery(query, Item.class).getResultList();
        resultList.forEach(i -> {
            log.info("i : {}", i);
        });
    }
}
