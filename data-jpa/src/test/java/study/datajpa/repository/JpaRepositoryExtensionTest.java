package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JpaRepositoryExtensionTest {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @Order(1)
    @DisplayName("사용자 정의 리포지토리 구현")
    void customRepository() {
        /*
        사용자 정의 리포지토리 구현
            - 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
            - 스프링 데이터 JPA 가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
            - 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
                - JPA 직접 사용(EntityManager)
                - 스프링 JDBC Template 사용
                - MyBatis 사용
                - 데이터베이스 커넥션 직접 사용 등등...
                - Querydsl 사용

        방법
            1. 사용자 정의 인터페이스 생성 -> MemberRepositoryCustom.class 생성
            2. 사용자 정의 인터페이스 구현 클래스 생성 -> MemberRepositoryCustomImpl.class 생성
            3. MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom

        사용자 정의 구현 클래스
            - 규칙: 리포지토리 인터페이스 이름 + Impl
            - 스프링 데이터 JPA 가 인식해서 스프링 빈으로 등록

            - Impl 대신 다른 이름으로 변경하고 싶으면?
                - XML 설정
                    <repositories base-package="study.datajpa.repository" repository-impl-postfix="Impl" />
                - JavaConfig 설정
                    @EnableJpaRepositories(basePackages = "study.datajpa.repository", repositoryImplementationPostfix = "Impl")

        참고: 실무에서는 주로 QueryDSL 이나 SpringJdbcTemplate 을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용
        참고: 항상 사용자 정의 리포지토리가 필요한 것은 아니다. 그냥 임의의 리포지토리를 만들어도 된다.
        예를들어 MemberQueryRepository 라는 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서 그냥 직접 사용해도 된다.
        물론 이 경우 스프링 데이터 JPA 와는 아무런 관계 없이 별도로 동작한다.

        사용자 정의 리포지토리 구현 최신 방식
            스프링 데이터 2.x 부터는 사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + Impl 을 적용하는 대신에 사용자 정의 인터페이스 명 + Impl 방식도 지원한다.
            예를 들어서 위 예제의 MemberRepositoryImpl 대신에 MemberRepositoryCustomImpl 같이 구현해도 된다.
            기존 방식보다 이 방식이 사용자 정의 인터페이스 이름과 구현 클래스 이름이 비슷하므로 더 직관적이다.
            추가로 여러 인터페이스를 분리해서 구현하는 것도 가능하기 때문에 새롭게 변경된 이 방식을 사용하는 것을 더 권장한다.
        */
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));
        memberRepository.save(new Member("member6", 60));

        List<Member> list = memberRepository.findMemberCustom();
        log.info("{}", list);
    }

    @Test
    @Order(2)
    @DisplayName("Auditing-순수 JPA")
    void auditing1() throws InterruptedException {
        /*
        엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶으면?
            - 등록일
            - 수정일
            - 등록자
            - 수정자

        순수 JPA
            - @PrePersist, @PostPersist
            - @PreUpdate, @PostUpdate
        */

        //순수 JPA 사용
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //@PreUpdate
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).orElse(null);
        log.info("{}", findMember.getCreatedDate());
        log.info("{}", findMember.getUpdatedDate());
    }

    @Test
    @Order(3)
    @DisplayName("Auditing-스프링 데이터 JPA")
    void auditing2() throws InterruptedException {
        /*
        설정
            - @EnableJpaAuditing 스프링 부트 설정 클래스에 적용해야함
            - @EntityListeners(AuditingEntityListener.class) 엔티티에 적용

        사용 어노테이션
            - @CreatedDate
            - @LastModifiedDate
            - @CreatedBy
            - @LastModifiedBy

        참고: 저장시점에 등록일, 등록자는 물론이고, 수정일, 수정자도 같은 데이터가 저장된다.
        데이터가 중복 저장되는 것 같지만, 이렇게 해두면 변경 컬럼만 확인해도 마지막에 업데이트한 유저를 확인 할 수 있으므로 유지보수 관점에서 편리하다.
        이렇게 하지 않으면 변경 컬럼이 null 일때 등록 컬럼을 또 찾아야 한다.
        참고로 저장시점에 저장데이터만 입력하고 싶으면 @EnableJpaAuditing(modifyOnCreate = false) 옵션을 사용하면 된다.
        */

        Team team = new Team("teamA");
        teamRepository.save(team);

        Thread.sleep(100);
        team.setName("teamB");

        em.flush();
        em.clear();

        Team findTeam = teamRepository.findById(team.getId()).orElse(null);
        log.info("{}", findTeam.getCreatedDate());
        log.info("{}", findTeam.getCreatedBy());
        log.info("{}", findTeam.getLastModifiedDate());
        log.info("{}", findTeam.getLastModifiedBy());
    }
}
