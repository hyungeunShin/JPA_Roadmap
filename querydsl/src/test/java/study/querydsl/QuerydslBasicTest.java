package study.querydsl;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuerydslBasicTest {
    @PersistenceContext
    EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        /*
        JPAQueryFactory 를 필드로 제공하면 동시성 문제는 어떻게 될까?
            동시성 문제는 JPAQueryFactory 를 생성 할 때 제공하는 EntityManager(em)에 달려있다.
            스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager 에 접근해도 트랜잭션 마다 별도의 영속성 컨텍스트를 제공하기 때문에 동시성 문제는 걱정하지 않아도 된다.
        */
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    @Order(1)
    @DisplayName("1. member1 찾기 - JPQL")
    void jpql1() {
        Member member = em.createQuery("select m from Member m where m.username = :username", Member.class)
                          .setParameter("username", "member1")
                          .getSingleResult();

        assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    @Order(2)
    @DisplayName("1. member1 찾기 - QueryDSL")
    void queryDSL1() {
        //EntityManager 로 JPAQueryFactory 생성
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m");

        Member member = jpaQueryFactory.select(m)
                                       .from(m)
                                       .where(m.username.eq("member1"))
                                       .fetchOne();

        assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    @Order(3)
    @DisplayName("Q-Type 활용 2가지 방법")
    void queryDSL2() {
        //참고: 같은 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스를 사용하자, static-import 추천
        QMember member1 = QMember.member;           //기본 인스턴스 사용
        QMember member2 = new QMember("m");  //별칭 직접 지정

        Member findMember1 = queryFactory.select(member1)
                                         .from(member1)
                                         .where(member1.username.eq("member1"))
                                         .fetchOne();

        Member findMember2 = queryFactory.select(member2)
                                         .from(member2)
                                         .where(member2.username.eq("member1"))
                                         .fetchOne();

        assertThat(findMember1).isEqualTo(findMember2);
    }

    @Test
    @Order(4)
    @DisplayName("검색 조건 쿼리")
    void queryDSL3() {
        /*
        member.username.eq("member1")           //username = 'member1'
        member.username.ne("member1")           //username != 'member1'
        member.username.eq("member1").not()     //username != 'member1'
        member.username.isNotNull()             //username is not null
        member.age.in(10, 20)                   //age in (10,20)
        member.age.notIn(10, 20)                //age not in (10, 20)
        member.age.between(10,30)               //between 10, 30
        member.age.goe(30)                      //age >= 30
        member.age.gt(30)                       //age > 30
        member.age.loe(30)                      //age <= 30
        member.age.lt(30)                       //age < 30
        member.username.like("member%")         //like 검색
        member.username.contains("member")      //like ‘%member%’ 검색
        member.username.startsWith("member")    //like ‘member%’ 검색
        ...
        */
        //참고: select, from 을 selectFrom 으로 합칠 수 있음
        Member findMember = queryFactory.selectFrom(member)
                                        .where(member.username.eq("member1").and(member.age.eq(10)))
                                        .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @Order(5)
    @DisplayName("AND 조건을 파라미터로 처리")
    void queryDSL4() {
        //where() 에 파라미터로 검색조건을 추가하면 AND 조건이 추가됨
        List<Member> list = queryFactory.selectFrom(member)
                                        .where(member.username.eq("member1"), member.age.eq(10))
                                        .fetch();

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    @Order(6)
    @DisplayName("결과 조회")
    void queryDSL5() {
        /*
        - fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
        - fetchOne() : 단 건 조회
            - 결과가 없으면 : null
            - 결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
        - fetchFirst() : limit(1).fetchOne()
        - fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
        - fetchCount() : count 쿼리로 변경해서 count 수 조회
        */
        List<Member> fetch = queryFactory.selectFrom(member).fetch();
        assertThat(fetch.size()).isEqualTo(4);

        assertThatThrownBy(() -> queryFactory.selectFrom(member).fetchOne()).isInstanceOf(NonUniqueResultException.class);

        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
        log.info("{}", fetchFirst);


        //페이징에서 사용
        //fetchResults() is deprecated
        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();
        log.info("QueryResults : {}", results.getResults());

        //count 쿼리로 변경
        //fetchCount() is deprecated
        //long count = queryFactory.selectFrom(member).fetchCount();
        Long count = queryFactory.select(member.count()).from(member).fetchOne();
        log.info("{}", count);
    }

    @Test
    @Order(7)
    @DisplayName("정렬")
    void queryDSL6() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 200));
        em.persist(new Member("member6", 100));

        List<Member> list = queryFactory.selectFrom(member)
                                        .where(member.age.loe(200))
                                        .orderBy(member.age.desc(), member.username.asc().nullsLast())
                                        .fetch();

        Member member5 = list.get(0);
        Member member6 = list.get(1);
        Member memberNull = list.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    @Order(8)
    @DisplayName("페이징1")
    void queryDSL7() {
        List<Member> list = queryFactory.selectFrom(member)
                                        .orderBy(member.username.desc())
                                        .offset(1)  //0부터 시작(zero index)
                                        .limit(2)   //최대 2건 조회
                                        .fetch();

        log.info("{}", list);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    @Order(9)
    @DisplayName("페이징2")
    void queryDSL8() {
        //fetchResults() is deprecated
        QueryResults<Member> queryResults = queryFactory.selectFrom(member)
                                                        .orderBy(member.username.desc())
                                                        .offset(1)
                                                        .limit(2)
                                                        .fetchResults();    //count 쿼리가 실행되니 성능상 주의!

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    @Test
    @Order(10)
    @DisplayName("집합 함수")
    void queryDSL9() {
        List<Tuple> list = queryFactory.select(member.count(), member.age.sum(), member.age.avg(), member.age.max(), member.age.min())
                                       .from(member)
                                       .fetch();

        log.info("{}", list);
        Tuple tuple = list.get(0);

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    @Order(11)
    @DisplayName("Group By")
    void queryDSL10() {
        List<Tuple> list = queryFactory.select(team.name, member.age.avg())
                                       .from(member)
                                       .join(member.team, team)
                                       .groupBy(team.name)
                                       //.having()
                                       .fetch();

        Tuple teamA = list.get(0);
        Tuple teamB = list.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    @Order(12)
    @DisplayName("기본 조인")
    void queryDSL11() {
        /*
        join(), innerJoin() : 내부 조인(inner join)
        leftJoin() : left 외부 조인(left outer join)
        rightJoin() : right 외부 조인(right outer join)
        */
        List<Member> list = queryFactory.selectFrom(member)
                                        .join(member.team, team)
                                        .where(team.name.eq("teamA"))
                                        .fetch();

        assertThat(list).extracting("username").containsExactly("member1", "member2");
    }

    @Test
    @Order(13)
    @DisplayName("세타 조인")
    void queryDSL12() {
        //연관관계가 없는 필드로 조인
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        //from 절에 여러 엔티티를 선택해서 세타 조인
        //외부 조인 불가능
        List<Member> list = queryFactory.select(member)
                                        .from(member, team)
                                        .where(member.username.eq(team.name))
                                        .fetch();

        assertThat(list).extracting("username").containsExactly("teamA", "teamB");
    }

    @Test
    @Order(14)
    @DisplayName("조인 - ON절")
    void queryDSL13() {
        /*
        on 절을 활용해 조인 대상을 필터링 할 때 외부조인이 아니라 내부조인(inner join)을 사용하면 where 절에서 필터링 하는 것과 기능이 동일하다.
        따라서 on 절을 활용한 조인 대상 필터링을 사용할 때 내부조인 이면 where 절로 해결하고 정말 외부조인이 필요한 경우에만 이 기능을 사용하자.
        */
        List<Tuple> list = queryFactory.select(member, team)
                                       .from(member)
                                       .leftJoin(member.team, team).on(team.name.eq("teamA"))
                                       .fetch();

        list.forEach(t -> log.info("{}", t));
    }

    @Test
    @Order(15)
    @DisplayName("조인 - ON절을 활용한 연관관계 없는 엔티티 외부 조인")
    void queryDSL14() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        //하이버네이트 5.1부터 on 을 사용해서 서로 관계가 없는 필드로 외부 조인하는 기능이 추가되었다. 물론 내부 조인도 가능하다.
        //주의! 문법을 잘 봐야 한다. leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
        List<Tuple> list = queryFactory.select(member, team)
                                       .from(member)
                                       .leftJoin(team).on(member.username.eq(team.name))
                                       .fetch();

        list.forEach(t -> log.info("{}", t));
    }

    @Test
    @Order(16)
    @DisplayName("페치 조인 X")
    void queryDSL15() {
        em.flush();
        em.clear();

        //페치조인 X
        Member findMember = queryFactory.selectFrom(member)
                                        .where(member.username.eq("member1"))
                                        .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    @Order(17)
    @DisplayName("페치 조인 O")
    void queryDSL16() {
        em.flush();
        em.clear();

        //페치조인 O
        Member findMember = queryFactory.selectFrom(member)
                                        .join(member.team, team).fetchJoin()
                                        .where(member.username.eq("member1"))
                                        .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    @Test
    @Order(18)
    @DisplayName("서브 쿼리1")
    void queryDSL17() {
        QMember memberSub = new QMember("memberSub");

        List<Member> list = queryFactory.selectFrom(member)
                                        .where(member.age.eq(
                                            JPAExpressions.select(memberSub.age.max()).from(memberSub)
                                        ))
                                        .fetch();

        assertThat(list).extracting("age").containsExactly(40);
    }

    @Test
    @Order(19)
    @DisplayName("서브 쿼리2")
    void queryDSL18() {
        QMember memberSub = new QMember("memberSub");

        List<Member> list = queryFactory.selectFrom(member)
                                        .where(member.age.goe(
                                            JPAExpressions.select(memberSub.age.avg()).from(memberSub)
                                        ))
                                        .fetch();

        assertThat(list).extracting("age").containsExactly(30, 40);
    }

    @Test
    @Order(20)
    @DisplayName("서브 쿼리3")
    void queryDSL19() {
        QMember memberSub = new QMember("memberSub");

        List<Member> list = queryFactory.selectFrom(member)
                                        .where(member.age.in(
                                            JPAExpressions.select(memberSub.age)
                                                          .from(memberSub)
                                                          .where(memberSub.age.gt(10))
                                        ))
                                        .fetch();

        assertThat(list).extracting("age").containsExactly(20, 30, 40);
    }

    @Test
    @Order(21)
    @DisplayName("서브쿼리4 - 스칼라 서브쿼리")
    void queryDSL20() {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> list = queryFactory.select(member.username, JPAExpressions.select(memberSub.age.avg()).from(memberSub))
                                       .from(member)
                                       .fetch();

        list.forEach(t -> {
            log.info("{}", t.get(member.username));
            log.info("{}", t.get(JPAExpressions.select(memberSub.age.avg()).from(memberSub)));
        });
    }

    /*
    from 절의 서브쿼리 한계
        JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다.
        당연히 Querydsl 도 지원하지 않는다.
        하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다.
        Querydsl 도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다.

    from 절의 서브쿼리 해결방안
    1. 서브쿼리를 join 으로 변경한다.(가능한 상황도 있고 불가능한 상황도 있다.)
    2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
    3. nativeSQL 을 사용한다.
    */

    @Test
    @Order(22)
    @DisplayName("Case 문1")
    void queryDSL21() {
        List<String> list1 = queryFactory.select(member.age
                                                .when(10).then("열살")
                                                .when(20).then("스무살")
                                                .otherwise("기타"))
                                         .from(member)
                                         .fetch();

        log.info("{}", list1);

        List<String> list2 = queryFactory.select(new CaseBuilder()
                                                .when(member.age.between(0, 20)).then("0~20살")
                                                .when(member.age.between(21, 30)).then("21~30살")
                                                .otherwise("기타"))
                                         .from(member)
                                         .fetch();

        log.info("{}", list2);
    }

    @Test
    @Order(23)
    @DisplayName("Case 문2")
    void queryDSL22() {
        NumberExpression<Integer> orderNumber = new CaseBuilder().when(member.age.between(0, 20)).then(2)
                                                                 .when(member.age.between(21, 30)).then(1)
                                                                 .otherwise(3);

        List<Tuple> list = queryFactory.select(member.username, member.age, orderNumber)
                                       .from(member)
                                       .orderBy(orderNumber.asc())
                                       .fetch();

        list.forEach(t -> {
            String username = t.get(member.username);
            Integer age = t.get(member.age);
            Integer rank = t.get(orderNumber);

            log.info("{}, {}, {}", username, age, rank);
        });
    }

    @Test
    @Order(24)
    @DisplayName("상수, 문자 더하기")
    void queryDSL23() {
        Tuple t = queryFactory.select(member.username, Expressions.constant("A"))
                              .from(member)
                              .fetchFirst();

        log.info("{}", t);

        //member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue() 로 문자로 변환할 수 있다.
        //이 방법은 ENUM을 처리할 때도 자주 사용한다
        String s = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
                               .from(member)
                               .where(member.username.eq("member1"))
                               .fetchOne();

        log.info("{}", s);
    }
}
