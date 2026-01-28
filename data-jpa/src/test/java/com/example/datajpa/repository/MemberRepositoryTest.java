package com.example.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import com.example.datajpa.dto.MemberDTO;
import com.example.datajpa.entity.Member;
import com.example.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberRepositoryTest {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void beforeEach() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));
        memberRepository.save(new Member("member6", 60));
    }

    @Test
    @Order(1)
    @DisplayName("메소드 이름으로 쿼리 생성")
    void createQueryByMethodName() {
        /*
        - 조회: find…By ,read…By ,query…By get…By,
        - COUNT: count…By 반환타입 long
        - EXISTS: exists…By 반환타입 boolean
        - 삭제: delete…By, remove…By 반환타입 long
        - DISTINCT: findDistinct, findMemberDistinctBy
        - LIMIT: findFirst3, findFirst, findTop, findTop3
        - 참고: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
        */
        List<Member> list = memberRepository.findByUsernameAndAgeGreaterThan("member1", 10);
        log.info("{}", list);
    }

    @Test
    @Order(2)
    @DisplayName("JPA NamedQuery")
    void namedQuery() {
        /*
        - 스프링 데이터 JPA 는 선언한 "도메인 클래스 + .(점) + 메서드 이름"으로 Named 쿼리를 찾아서 실행
          만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용한다.
          필요하면 전략을 변경할 수 있지만 권장하지 않는다.
        - @Query 를 생략하고 메서드 이름만으로 Named 쿼리를 호출할 수 있다.
        - 참고: 스프링 데이터 JPA 를 사용하면 실무에서 Named Query 를 직접 등록해서 사용하는 일은 드물다. 대신
          @Query 를 사용해서 리파지토리 메소드에 쿼리를 직접 정의한다.
        */
        List<Member> list = memberRepository.jpaNamedQuery("member1");
        log.info("{}", list);
    }

    @Test
    @Order(3)
    @DisplayName("@Query")
    void useQueryAnnotation() {
        /*
        - @org.springframework.data.jpa.repository.Query 어노테이션을 사용
        - 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음
        - JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음
        */
        List<Member> list = memberRepository.queryAnnotation("member1", 10);
        log.info("{}", list);
    }

    @Test
    @Order(4)
    @DisplayName("단순히 값 하나 조회")
    void onlyOneValue() {
        //JPA 값 타입( @Embedded )도 이 방식으로 조회할 수 있다.
        List<String> list = memberRepository.findUsernameOnly();
        log.info("{}", list);
    }

    @Test
    @Order(5)
    @DisplayName("DTO로 직접 조회")
    void queryWithDTO() {
        List<MemberDTO> list = memberRepository.findMemberDTO();
        log.info("{}", list);
    }

    @Test
    @Order(6)
    @DisplayName("위치 기반 파라미터 바인딩")
    void locationBasedParameterBinding() {
        List<Member> list = memberRepository.locationBased("member1");
        log.info("{}", list);
    }

    @Test
    @Order(7)
    @DisplayName("이름 기반 파라미터 바인딩")
    void nameBasedParameterBinding() {
        //코드 가독성과 유지보수를 위해 이름 기반 파라미터 바인딩을 사용
        List<Member> list = memberRepository.nameBased("member1");
        log.info("{}", list);
    }

    @Test
    @Order(8)
    @DisplayName("컬렉션 파라미터 바인딩")
    void collectionParameterBinding() {
        List<Member> list = memberRepository.collectionBased(List.of("member1", "member2"));
        log.info("{}", list);
    }

    @Test
    @Order(9)
    @DisplayName("페이징과 정렬")
    void paging() {
        /*
        페이징과 정렬 파라미터
            - org.springframework.data.domain.Sort : 정렬 기능
            - org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
        특별한 반환 타입
            - org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
            - org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)
            - List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
        */

        /*
        두 번째 파라미터로 받은 Pageable 은 인터페이스다.
        따라서 실제 사용할 때는 해당 인터페이스를 구현한 org.springframework.data.domain.PageRequest 객체를 사용한다.

        PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를 입력한다.
        여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다.
        참고로 페이지는 0부터 시작한다.
        */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        /*
        Page<Member> page = memberRepository.findByAgeGreaterThan(10, pageRequest);
        List<Member> content = page.getContent(); //조회된 데이터
        log.info("{}", content);
        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
        */

        /*
        interface Page<T> extends Slice<T>
            - int getTotalPages(); //전체 페이지 수
            - long getTotalElements(); //전체 데이터 수
            - <U> Page<U> map(Function<? super T, ? extends U> converter); //변환기

        interface Slice<T> extends Streamable<T>
            - int getNumber(); //현재 페이지
            - int getSize(); //페이지 크기
            - int getNumberOfElements(); //현재 페이지에 나올 데이터 수
            - List<T> getContent(); //조회된 데이터
            - boolean hasContent(); //조회된 데이터 존재 여부
            - Sort getSort(); //정렬 정보
            - boolean isFirst(); //현재 페이지가 첫 페이지 인지 여부
            - boolean isLast(); //현재 페이지가 마지막 페이지 인지 여부
            - boolean hasNext(); //다음 페이지 여부
            - boolean hasPrevious(); //이전 페이지 여부
            - Pageable getPageable(); //페이지 요청 정보
            - Pageable nextPageable(); //다음 페이지 객체
            - Pageable previousPageable();//이전 페이지 객체
            - <U> Slice<U> map(Function<? super T, ? extends U> converter); //변환기
        */
        //Slice (count X) : 추가로 limit + 1을 조회한다. 그래서 다음 페이지 여부 확인
        Slice<Member> slice = memberRepository.findByAgeGreaterThan(10, pageRequest);
        log.info("{}", slice.getContent());
        log.info("{}", slice.getNumber());
        log.info("{}", slice.getSort());
        log.info("{}", slice.hasPrevious());
        log.info("{}", slice.hasNext());
        log.info("{}", slice.previousPageable());
        log.info("{}", slice.nextPageable());

        //List (count X)
        List<Member> list = memberRepository.findByAge(10, pageRequest);
        log.info("{}", list);
    }
    
    @Test
    @Order(10)
    @DisplayName("페이징 카운트 쿼리 분리")
    void pagingSeparateCount() {
        Page<Member> page = memberRepository.findMemberAllCountBy(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")));
        log.info("{}", page.getContent());
    }

    @Test
    @Order(11)
    @DisplayName("TOP N 쿼리")
    void topQuery() {
        List<Member> list = memberRepository.findTop3By(Sort.by(Sort.Direction.DESC, "age"));
        log.info("{}", list);
    }

    @Test
    @Order(12)
    @DisplayName("페이지 유지하면서 엔티티를 DTO로")
    void entityToDTOWithPaging() {
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAgeGreaterThan(10, pageRequest);
        Page<MemberDTO> dtoPage = page.map(MemberDTO::new);
        log.info("{}", dtoPage.getContent());
    }

    @Test
    @Order(13)
    @DisplayName("하이버네이트 left join 최적화")
    void leftJoinOptimization() {
        /*
        실행한 JPQL 을 보면 left join 을 사용하고 있다. => select m from Member m left join m.team t
        Member 와 Team 을 조인을 하지만 사실 이 쿼리를 Team 을 전혀 사용하지 않는다.
        select 절이나, where 절에서 사용하지 않는 다는 뜻이다.
        그렇다면 이 JPQL 은 사실상 다음과 같다. => select m from Member m

        left join 이기 때문에 왼쪽에 있는 member 자체를 다 조회한다는 뜻이 된다.
        만약 select 나 where 에 team 의 조건이 들어간다면 정상적인 join 문이 보인다.
        JPA 는 이 경우 최적화를 해서 join 없이 해당 내용만으로 SQL 을 만든다.
        여기서 만약 Member 와 Team 을 하나의 SQL 로 한번에 조회하고 싶으시다면 JPA 가 제공하는 fetch join 을 사용해야 한다.
        */
        List<Member> list = memberRepository.leftJoinOptimization();
        log.info("{}", list);
    }

    @Test
    @Order(14)
    @DisplayName("벌크성 수정 쿼리")
    void bulkUpdate() {
        /*
        벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용
            - 사용하지 않으면 다음 예외 발생
            - org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations
        벌크성 쿼리를 실행하고 나서 영속성 컨텍스트 초기화: @Modifying(clearAutomatically = true)
            - 이 옵션의 기본값은 false
            - 이 옵션 없이 회원을 findById 로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수 있다.
              만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화 하자.

        참고: 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.
            권장하는 방안
            1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
            2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다.
        */
        int resultCount = memberRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(5);
    }

    @Test
    @Order(15)
    @DisplayName("연관된 엔티티들을 SQL 한번에 조회")
    void entityGraph() {
        /*
        사실상 페치 조인(FETCH JOIN)의 간편 버전
        LEFT OUTER JOIN 사용
        */
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //페치 조인
        List<Member> list1 = memberRepository.findMemberFetchJoin();
        log.info("{}", list1);

        //엔티티 그래프
        List<Member> list2 = memberRepository.findAll();
        log.info("{}", list2);
        List<Member> list3 = memberRepository.findMemberEntityGraph();
        log.info("{}", list3);
        List<Member> list4 = memberRepository.findByUsername("member1");
        log.info("{}", list4);
        List<Member> list5 = memberRepository.findMemberNamedEntityGraph();
        log.info("{}", list5);
    }

    @Test
    @Order(16)
    @DisplayName("JPA 힌트")
    void jpaHint() {
        /*
        JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)

        @QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly", value = "true")}, forCounting = true)
        Page<Member> findByUsername(String name, Pageable pageable);
            - org.springframework.data.jpa.repository.QueryHints 어노테이션을 사용
            - forCounting : 반환 타입으로 Page 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 count 쿼리도 쿼리 힌트 적용(기본값 true)
        */
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        List<Member> list = memberRepository.findReadOnlyByUsername("member1");
        list.get(0).setUsername("member1111");
        em.flush(); //update X
    }

    @Test
    @Order(17)
    @DisplayName("Lock")
    void lock() {
        /*
        select for update 구문
        */
        List<Member> list = memberRepository.findByUsernameAndAge("member1", 10);
        log.info("{}", list);
    }
}