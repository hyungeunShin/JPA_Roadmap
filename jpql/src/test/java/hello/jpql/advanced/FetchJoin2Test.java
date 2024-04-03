package hello.jpql.advanced;

import hello.jpql.domain.Member;
import hello.jpql.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
class FetchJoin2Test {
    /*
    fetch join 과 일반 조인의 차이점
        - 일반 조인
            • JPQL 은 결과를 반환할 때 연관관계 고려X
            • 단지 SELECT 절에 지정한 엔티티만 조회할 뿐

        - fetch join
            • fetch join 을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)
            • fetch join 은 객체 그래프를 SQL 한번에 조회하는 개념

    fetch join 의 특징과 한계
        • fetch join 대상에는 별칭을 줄 수 없다.
            • 하이버네이트는 가능, 가급적 사용X
        • 둘 이상의 컬렉션은 fetch join 할 수 없다.
        • 컬렉션을 fetch join 하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
            • 일대일, 다대일 같은 단일 값 연관 필드들은 fetch join 해도 페이징 가능
            • 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)
        • 연관된 엔티티들을 SQL 한 번으로 조회 - 성능 최적화
        • 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
            • @OneToMany(fetch = FetchType.LAZY) //글로벌 로딩 전략
        • 실무에서 글로벌 로딩 전략은 모두 지연 로딩

    fetch join - 정리
        • fetch join 은 객체 그래프를 유지할 때 사용하면 효과적
        • 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면
          fetch join 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO 로 반환하는 것이 효과적
    */
    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    @Commit
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
    @Commit
    void after() {
        em.createQuery("delete from Member m").executeUpdate();
        em.createQuery("delete from Team t").executeUpdate();
        em.clear();
    }

    @Test
    @DisplayName("일반 조인")
    void test1() {
        //데이터를 team 만 가져온다.
        //select t1_0.id, t1_0.name from team t1_0 join member m1_0 on t1_0.id=m1_0.team_id
        String query = "select t from Team t join t.members m";
        List<Team> resultList = em.createQuery(query, Team.class).getResultList();
        resultList.forEach(t -> {
            //여기에서 member 에 대한 데이터를 가져온다.
            log.info("t : {}, t.members : {}", t, t.getMembers());
        });
    }

    @Test
    @DisplayName("fetch join 에서 on 절")
    void test2() {
        /*
        fetch join + on 절에 에러가 나는 이유는 JOIN 절에 ON 필터링을 사용하면 JOIN 시점에 JOIN 대상을 필터링하기 때문에 모든 컬렉션 데이터를 가져올 수 없기 때문이다.
        이는 fetch join 이 나타난 이유인 '연관된 엔티티나 컬렉션을 한 번에 조회해준다'는 내용과 위반되기 때문에 on 조건을 주면 무조건 에러가 발생하도록 설계되었다.

        예를 들어 db에 다음과 같은 데이터가 있다.
        team1 - memberA
        team1 - memberB
        team1 - memberC

        그런데 조인 대상의 필터링을 제공해서 조회결과가 memberA, memberB만 조회하게 되면 JPA 애플리케이션은 team1 - {memberA, memberB} 라는 결과를 조회한다.
        team1에서 회원 데이터를 찾으면 memberA, memberB만 반환되어 버린다.
        이렇게 되면 JPA 입장에서 DB와 데이터 일관성이 깨지고 최악의 경우에 memberC가 DB 에서 삭제될 수도 있다.
        왜냐하면 JPA 의 엔티티 객체 그래프는 DB와 데이터 일관성을 유지해야 하기 때문이다. 생각해보면 엔티티의 값을 변경하면 DB에 반영이 되어버린다.
        정리하면 JPA 의 엔티티 데이터는 DB의 데이터와 일관성을 유지해야 합니다. 임의로 데이터를 빼고 조회해버리면 DB에 해당 데이터가 없다고 판단하는 것과 똑같다.
        그래서 엔티티를 사용할 때는 이 부분을 매우 조심해야 한다.
        */
        String query1 = "select t from Team t join fetch t.members m on m.name = :name";
        String query2 = "Select t from Team t join fetch t.members m on t.name = :name";

        Assertions.assertThatThrownBy(() -> em.createQuery(query1, Team.class).setParameter("name", "회원1").getResultList()).isInstanceOf(Exception.class);
        Assertions.assertThatThrownBy(() -> em.createQuery(query2, Team.class).setParameter("name", "팀1").getResultList()).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("fetch join 에서 where 절")
    void test3() {
        //Team 에 필터링을 거는 것은 상관이 없다. 원하는 Team 을 필터링한 후 그에 속한 모든 Member 엔티티도 함께 조회가 되기 때문이다.
        String query = "select t from Team t join fetch t.members m where t.name = :name";

        List<Team> resultList = em.createQuery(query, Team.class).setParameter("name", "팀1").getResultList();
        resultList.forEach(t -> {
            log.info("t : {}, t.members : {}", t, t.getMembers());
        });
    }

    @Test
    @DisplayName("fetch join 에서 where 절 주의점!!!!")
    void test4() {
        //fetch join 대상을 where 절에 쓰는 것은 주의해야 한다.
        //애플리케이션에서 fetch join 의 결과는 연관된 모든 엔티티가 있을것이라 가정하고 사용해야 한다.
        String query = "select t from Team t join fetch t.members m where m.name = :name";

        List<Team> resultList = em.createQuery(query, Team.class).setParameter("name", "회원1").getResultList();
        resultList.forEach(t -> {
            //t : Team{id=1, name='팀1'}, t.members : [Member{id=1, name='회원1', age=10}]
            log.info("t : {}, t.members : {}", t, t.getMembers());
        });

        //회원 1이 속한 팀1은 회원이 2명인데 한명만 조회가 된다.
        Assertions.assertThat(resultList.get(0).getMembers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("fetch join 에서 where 절2")
    void test5() {
        String query = "select m from Member m join fetch m.team t where t.name = :name";
        List<Member> resultList = em.createQuery(query, Member.class).setParameter("name", "팀1").getResultList();
        resultList.forEach(m -> {
            //m : Member{id=1, name='회원1', age=10}, m.team : Team{id=1, name='팀1'}
            //m : Member{id=2, name='회원2', age=20}, m.team : Team{id=1, name='팀1'}
            log.info("m : {}, m.team : {}", m, m.getTeam());
        });
    }

    @Test
    @DisplayName("fetch join 에서 where 절3")
    void test6() {
        String query = "select m from Member m join fetch m.team t where m.name = :name";
        List<Member> resultList = em.createQuery(query, Member.class).setParameter("name", "회원1").getResultList();
        resultList.forEach(m -> {
            //m : Member{id=1, name='회원1', age=10}, m.team : Team{id=1, name='팀1'}
            log.info("m : {}, m.team : {}", m, m.getTeam());
        });
    }

    @Test
    @DisplayName("컬렉션 fetch join 시 페이징 X")
    void test7() {
        /*
        //HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
        //String query = "select t from Team t join fetch t.members";

        List<Team> resultList = em.createQuery(query, Team.class).setFirstResult(0).setMaxResults(1).getResultList();
        resultList.forEach(t -> {
            log.info("t : {}, t.members : {}", t, t.getMembers());
        });
        */

        /*
        //대안법 -> 순서 뒤집기
        String query = "select m from Member m join fetch m.team";
        List<Member> resultList = em.createQuery(query, Member.class).setFirstResult(0).setMaxResults(1).getResultList();
        resultList.forEach(m -> {
            log.info("m : {}, m.team : {}", m, m.getTeam());
        });
        */

        //대안법2 -> Team 의 members 에 @BatchSize(size = 100) 추가
        //(team 을 가져올 때 member 는 지연 로딩상태인데 member 를 가져올 때 in 을 통해 팀 아이디를 100개를 넘긴다.)
        //spring.jpa.properties.hibernate.default_batch_fetch_size=100 전역으로 설정도 가능하다.
        String query = "select t from Team t";
        List<Team> resultList = em.createQuery(query, Team.class).setFirstResult(0).setMaxResults(2).getResultList();
        resultList.forEach(t -> {
            //쿼리문 : select m1_0.team_id, m1_0.id, m1_0.age, m1_0.name, m1_0.type from member m1_0 where m1_0.team_id in (?, ?, ?, .....?)
            log.info("t : {}, t.members : {}", t, t.getMembers());
        });
    }
}
