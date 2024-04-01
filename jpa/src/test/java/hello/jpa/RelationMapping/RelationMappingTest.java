package hello.jpa.RelationMapping;

import hello.jpa.RelationMapping.OneToMany.Member3;
import hello.jpa.RelationMapping.OneToMany.Team2;
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
class RelationMappingTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("1 : N")
    void oneToMany() {
        Member3 test = new Member3();
        test.setName("홍길동");
        em.persist(test);

        Team2 team = new Team2();
        team.setName("팀");
        //update OneToMany set team_id=? where id=?
        team.getMembers().add(test);
        em.persist(team);
    }
}
