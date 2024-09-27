package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.projection.NestedClosedProjection;
import study.datajpa.projection.UsernameAndAgeOnly;
import study.datajpa.projection.UsernameOnly;
import study.datajpa.projection.UsernameOnlyDTO;
import study.datajpa.spec.MemberSpec;

import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
class JpaRepositoryRestFnTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void beforeEach() {
        Team team = new Team("teamA");
        em.persist(team);

        Member m1 = new Member("m1", 10, team);
        Member m2 = new Member("m2", 10, team);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
    }

    /*
    Specifications
        - 스프링 데이터 JPA 는 JPA Criteria 를 활용해서 이 개념을 사용할 수 있도록 지원

    MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member>

    실무에서는 JPA Criteria 를 거의 안쓴다! 대신에 QueryDSL 을 사용하자.
    */
    @Test
    @DisplayName("Specifications")
    void spec() {
        //Specification 을 구현하면 명세들을 조립할 수 있음. where() , and() , or() , not() 제공
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> list = memberRepository.findAll(spec);
        log.info("{}", list);
    }

    /*
    Query By Example
        - Probe: 필드에 데이터가 있는 실제 도메인 객체
        - ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
        - Example: Probe 와 ExampleMatcher 로 구성, 쿼리를 생성하는데 사용

    장점
        - 동적 쿼리를 편리하게 처리
        - 도메인 객체를 그대로 사용
        - 데이터 저장소를 RDB 에서 NOSQL 로 변경해도 코드 변경이 없게 추상화 되어 있음
        - 스프링 데이터 JPA JpaRepository 인터페이스에 이미 포함

    단점
        - 조인은 가능하지만 내부 조인(INNER JOIN)만 가능함 외부 조인(LEFT JOIN) 안됨
        - 다음과 같은 중첩 제약조건 안됨
          firstname = ?0 or (firstname = ?1 and lastname = ?2)
        - 매칭 조건이 매우 단순함
        - 문자는 starts/contains/ends/regex
        - 다른 속성은 정확한 매칭(=) 만 지원
    */
    @Test
    @DisplayName("Query By Example")
    void queryByExample() {
        //Probe 생성
        Member probe = new Member("m1");
        probe.setTeam(new Team("teamA"));

        //ExampleMatcher 생성, age 프로퍼티는 무시
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> example = Example.of(probe, matcher);
        List<Member> list = memberRepository.findAll(example);

        log.info("{}", list);
    }

    //https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html
    @Test
    @DisplayName("Projection")
    void projection() {
        //select m1_0.username from member m1_0 where m1_0.username=?
        //인터페이스 기반 Closed Projections
        //프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA 가 제공
        List<UsernameOnly> list1 = memberRepository.findUsernameProjectionByUsername("m1");
        log.info("{}", list1);

        //인터페이스 기반 Open Projections
        List<UsernameAndAgeOnly> list2 = memberRepository.findUsernameAndAgeProjectionByUsername("m1");
        log.info("{}", list2);

        //클래스 기반 Projection
        //생성자의 파라미터 이름으로 매칭
        List<UsernameOnlyDTO> list3 = memberRepository.findUsernameDTOByUsername("m1");
        log.info("{}", list3);

        //동적 Projections
        List<UsernameOnlyDTO> list4 = memberRepository.findTypeProjectionByUsername("m1", UsernameOnlyDTO.class);
        log.info("{}", list4);

        //중첩 구조 처리
        /*
        주의
            - 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
            - 프로젝션 대상이 root 가 아니면
                - LEFT OUTER JOIN 처리
                - 모든 필드를 SELECT 해서 엔티티로 조회한 다음에 계산

        정리
            - 프로젝션 대상이 root 엔티티면 유용하다.
            - 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다.
            - 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
            - 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL 을 사용하자
        */
        List<NestedClosedProjection> list5 = memberRepository.findNestedProjectionByUsername("m1");
        log.info("{}", list5);
    }

    /*
    네이티브 쿼리
        - 가급적 네이티브 쿼리는 사용하지 않는게 좋음, 정말 어쩔 수 없을 때 사용

    스프링 데이터 JPA 기반 네이티브 쿼리
        - 페이징 지원
        - 반환 타입
            - Object[]
            - Tuple
            - DTO(스프링 데이터 인터페이스 Projections 지원)
        - 제약
            - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
            - JPQL 처럼 애플리케이션 로딩 시점에 문법 확인 불가
            - 동적 쿼리 불가
    */
    @Test
    @DisplayName("Native Query")
    void nativeQuery() {
        /*
        JPA 네이티브 SQL 지원
            - JPQL 은 위치 기반 파리미터를 1부터 시작하지만 네이티브 SQL 은 0부터 시작
            - 네이티브 SQL 을 엔티티가 아닌 DTO 로 변환은 하려면
                - DTO 대신 JPA TUPLE 조회
                - DTO 대신 MAP 조회
                - @SqlResultSetMapping 복잡
                - Hibernate ResultTransformer 를 사용해야함 복잡
                - https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpa-and-hibernate/

        참고: 네이티브 SQL 을 DTO 로 조회할 때는 JdbcTemplate or myBatis 권장
        */
        List<Member> list1 = memberRepository.findByNativeQuery("m1");
        log.info("{}", list1);
    }
}
