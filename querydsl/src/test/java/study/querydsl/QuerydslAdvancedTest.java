package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDTO;
import study.querydsl.dto.QMemberDTO;
import study.querydsl.dto.UserDTO;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static study.querydsl.entity.QMember.member;

@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuerydslAdvancedTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
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
    @DisplayName("프로젝션1")
    void queryDSL1() {
        //프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
        List<String> list = queryFactory.select(member.username)
                                        .from(member)
                                        .fetch();
        log.info("{}", list);
    }

    @Test
    @Order(2)
    @DisplayName("프로젝션2 - Tuple 반환")
    void queryDSL2() {
        //프로젝션 대상이 둘 이상이면 튜플이나 DTO 로 조회
        List<Tuple> list = queryFactory.select(member.username, member.age)
                                       .from(member)
                                       .fetch();

        log.info("{}", list);
    }

    @Test
    @Order(3)
    @DisplayName("프로젝션3 - DTO 반환")
    void queryDSL3() {
        //프로젝션 대상이 둘 이상이면 튜플이나 DTO 로 조회
        /*
        순수 JPA 에서 DTO 를 조회할 때는 new 명령어를 사용해야함
        DTO 의 package 이름을 다 적어줘야해서 지저분함
        생성자 방식만 지원함

        */
        List<MemberDTO> list = em.createQuery("select new study.querydsl.dto.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                                 .getResultList();

        log.info("{}", list);
    }

    /*
    Querydsl 빈 생성(Bean population)

    Querydsl 은 결과를 DTO 반환할 때 사용 시 다음 3가지 방법 지원
        - 프로퍼티 접근
        - 필드 직접 접근
        - 생성자 사용
    */
    @Test
    @Order(4)
    @DisplayName("QueryDSL DTO 반환 - 프로퍼티 접근(Setter)")
    void queryDSL4() {
        List<MemberDTO> list = queryFactory.select(Projections.bean(MemberDTO.class, member.username, member.age))
                                           .from(member)
                                           .fetch();

        log.info("{}", list);
    }

    @Test
    @Order(5)
    @DisplayName("QueryDSL DTO 반환 - 필드 직접 접근")
    void queryDSL5() {
        List<MemberDTO> list = queryFactory.select(Projections.fields(MemberDTO.class, member.username, member.age))
                                           .from(member)
                                           .fetch();

        log.info("{}", list);
    }

    @Test
    @Order(6)
    @DisplayName("QueryDSL DTO 반환 - 별칭이 다를 때")
    void queryDSL6() {
        /*
        프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
        ExpressionUtils.as(source, alias) : 필드나, 서브 쿼리에 별칭 적용
        username.as("memberName") : 필드에 별칭 적용
        */
        QMember memberSub = new QMember("memberSub");

//        List<UserDTO> list = queryFactory.select(Projections.fields(UserDTO.class
//                                                //매핑이 안되서 null
//                                                , member.username
//                                                , member.age))
//                                         .from(member)
//                                         .fetch();

        List<UserDTO> list = queryFactory.select(Projections.fields(UserDTO.class
                                                , member.username.as("name")
                                                , ExpressionUtils.as(JPAExpressions.select(memberSub.age.max()).from(memberSub), "age")))
                                         .from(member)
                                         .fetch();



        log.info("{}", list);
    }

    @Test
    @Order(7)
    @DisplayName("프로젝션3 - DTO 반환 - 생성자 사용")
    void queryDSL7() {
        List<MemberDTO> list = queryFactory.select(Projections.constructor(MemberDTO.class, member.username, member.age))
                                           .from(member)
                                           .fetch();

        log.info("{}", list);
    }

    @Test
    @Order(8)
    @DisplayName("프로젝션3 - DTO 반환 - @QueryProjection")
    void queryDSL8() {
        /*
        이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다. -> 위의 생성자 사용은 컴파일 시점에나 오류를 알 수 있다.
        다만 DTO 에 QueryDSL 어노테이션을 유지해야 하는 점과 DTO 까지 Q 파일을 생성해야 하는 단점이 있다.

        참고: @QueryProjection 을 사용하면 해당 DTO 가 Querydsl 을 의존하게 된다.
        이런 의존이 싫으면 해당 에노테이션을 제거하고 Projection.bean(), fields(), constructor() 을 사용하면 된다.
        */
        List<MemberDTO> list = queryFactory.select(new QMemberDTO(member.username, member.age))
                                           .from(member)
                                           .fetch();

        log.info("{}", list);
    }

    @Test
    @Order(9)
    @DisplayName("distinct")
    void queryDSL9() {
        em.persist(new Member("member1", 10));
        em.flush();
        em.clear();

        List<String> list = queryFactory.select(member.username).distinct()
                                        .from(member)
                                        .fetch();

        log.info("{}", list);
    }

    @Test
    @Order(10)
    @DisplayName("동적 쿼리 - BooleanBuilder")
    void queryDSL10() {
        List<Member> list = searchMember1("member1", 10);
        log.info("{}", list);
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();

        if(usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }

        if(ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory.selectFrom(member)
                           .where(builder)
                           .fetch();
    }

    @Test
    @Order(11)
    @DisplayName("동적 쿼리 - 다중 파라미터")
    void queryDSL11() {
        /*
        where 조건에 null 값은 무시된다.
        메서드를 다른 쿼리에서도 재활용 할 수 있다.
        쿼리 자체의 가독성이 높아진다.
        */
        List<Member> list = searchMember2("member1", 10);
        log.info("{}", list);
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory.selectFrom(member)
                           .where(usernameEq(usernameCond), ageEq(ageCond))
                           .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    @Test
    @Order(12)
    @DisplayName("벌크 연산 - 수정1")
    void queryDSL12() {
        //주의: JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다.
        long count = queryFactory.update(member)
                                 .set(member.username, "비회원")
                                 .where(member.age.lt(20))
                                 .execute();

        log.info("{}", count);

        //DB 에서 데이터를 조회해와도 영속성 컨텍스트가 우선권을 가지고 있기 때문에 DB 에서 가져온 데이터를 버린다.
        //그렇기 때문에 DB 와 영속성 컨텍스트 간에 데이터가 다르다.
        List<Member> list = queryFactory.selectFrom(member).fetch();
        log.info("{}", list);
    }

    @Test
    @Order(13)
    @DisplayName("벌크 연산 - 수정2")
    void queryDSL13() {
        long count = queryFactory.update(member)
                                 .set(member.age, member.age.add(1))
                                 .execute();

        log.info("{}", count);
    }

    @Test
    @Order(14)
    @DisplayName("벌크 연산 - 삭제")
    void queryDSL14() {
        long count = queryFactory.delete(member)
                                 .where(member.age.gt(20))
                                 .execute();

        log.info("{}", count);
    }

    @Test
    @Order(15)
    @DisplayName("SQL 함수")
    void queryDSL15() {
        String s1 = queryFactory.select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})", member.username, "member", "M"))
                                .from(member)
                                .fetchFirst();

        log.info("{}", s1);

        String s2 = queryFactory.select(member.username)
                                .from(member)
                                .where(member.username.eq(Expressions.stringTemplate("function('lower', {0})", member.username)))
                                .fetchFirst();

        log.info("{}", s2);
    }
}
