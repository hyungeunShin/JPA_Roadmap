package hello.jpa.Inheritance;

import hello.jpa.Inheritance.MappedSuper.MappedSuperTest1;
import hello.jpa.Inheritance.MappedSuper.MappedSuperTest2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Commit
class InheritanceTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("상속")
    void inherit() {
        Movie movie = new Movie();
        movie.setName("영화");
        movie.setDirector("감독");
        movie.setActor("배우");
        movie.setPrice(10000);
        em.persist(movie);

        Album album = new Album();
        album.setName("앨범");
        album.setArtist("작곡가");
        album.setPrice(10000);
        em.persist(album);

        em.flush();
        em.clear();

        em.find(Movie.class, movie.getId());
    }

    @Test
    @DisplayName("MappedSuperClass")
    void mappedSuper() {
        MappedSuperTest1 test = new MappedSuperTest1();
        test.setName("테스트1");
        test.setRegisterDate(LocalDateTime.now());

        MappedSuperTest2 test2 = new MappedSuperTest2();
        test2.setName("테스트2");
        test2.setRegisterDate(LocalDateTime.now());

        em.persist(test);
        em.persist(test2);
    }
}
