package hello.jpql.advanced;

import hello.jpql.domain.Member;
import hello.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class FetchJoin1Test {
    /*
    fetch join
        • SQL 조인 종류X ==> JPQL 에서 성능 최적화를 위해 제공하는 기능
        • 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
        • join fetch 명령어 사용
        • select m from Member m join fetch m.team ==> SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID=T.ID
    */
    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void before() {
        Team team1 = new Team();
        team1.setName("팀1");
        Team team2 = new Team();
        team2.setName("팀2");
        Team team3 = new Team();
        team3.setName("팀3");

        em.persist(team1);
        em.persist(team2);
        em.persist(team3);

        Member member1 = new Member("회원1", 10);
        member1.setTeam(team1);
        Member member2 = new Member("회원2", 20);
        member2.setTeam(team1);
        Member member3 = new Member("회원3", 30);
        member3.setTeam(team2);
        Member member4 = new Member("회원4", 40);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @AfterEach
    void after() {
        em.createQuery("delete from Member m").executeUpdate();
        em.createQuery("delete from Team t").executeUpdate();
        em.clear();
    }

    @Test
    @DisplayName("fetch join 사용 X")
    void test1() {
        //member 의 team 은 지연로딩이다. 만약 회원이 100명이고 팀이 다 다르다고 한다면?
        //N + 1 문제가 생긴다.(회원을 조회하는 쿼리 하나와 각 회원의 팀을 가져오기 위한 회원 결과수 만큼의 쿼리 N)
        //이 문제는 즉시 로딩이든 지연 로딩이든 똑같이 발생하는 문제이다.
        String query = "select m from Member m";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        //1. 회원 조회 쿼리
        //2. 회원1 의 팀을 가져오는 쿼리, 회원2는 회원 1과 같은 팀이기 때문에 회원 2의 팀은 1차 캐시에서 조회
        //3. 회원3 의 팀을 가져오는 쿼리
        resultList.forEach(m -> {
            log.info("m : {}, m.team : {}", m, m.getTeam());
        });
    }

    @Test
    @DisplayName("fetch join 사용 O")
    void test2() {
        //쿼리 한번만 실행
        String query = "select m from Member m join fetch m.team";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        //1. 회원과 팀을 한번에 모두 조회
        resultList.forEach(m -> {
            log.info("m : {}, m.team : {}", m, m.getTeam());
        });
    }

    //일대다 관계, 컬렉션 fetch join
    @Test
    @DisplayName("컬렉션 fetch join")
    void test3() {
        /*
        하이버네이트 6부터 fetch join 이 하위 컬렉션을 가져올 때 동일한 상위 엔터티 참조를 필터링하기 위해 distinct 를 더 이상 사용할 필요가 없다.
        https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#distinct

        6미만에서는 distinct 를 추가한다.
        JPQL 에서 distinct 는 2가지 기능을 제공
            1. SQL 에 DISTINCT 를 추가
            2. 애플리케이션 레벨에서 엔티티 중복 제거 ==> 같은 식별자를 가진 Team 을 제거

            String query = "select distinct t from Team t join fetch t.members";
        */
        String query = "select t from Team t join fetch t.members";
        List<Team> resultList = em.createQuery(query, Team.class).getResultList();
        log.info("result.size : {}", resultList.size());
        resultList.forEach(t -> {
            /*
            하이버네이트 6 이상일 때
            Team{id=1, name='팀1'}, t.members : [Member{id=1, name='회원1', age=10}, Member{id=2, name='회원2', age=20}]
            Team{id=2, name='팀2'}, t.members : [Member{id=3, name='회원3', age=30}]

            6 미만일 때
            Team{id=1, name='팀1'}, t.members : [Member{id=1, name='회원1', age=10}, Member{id=2, name='회원2', age=20}]
            Team{id=1, name='팀1'}, t.members : [Member{id=1, name='회원1', age=10}, Member{id=2, name='회원2', age=20}]
            Team{id=2, name='팀2'}, t.members : [Member{id=3, name='회원3', age=30}]
            */
            log.info("t : {}, t.members : {}", t, t.getMembers());
        });
    }
}
