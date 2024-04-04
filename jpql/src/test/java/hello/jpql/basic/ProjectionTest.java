package hello.jpql.basic;

import hello.jpql.domain.Address;
import hello.jpql.domain.Member;
import hello.jpql.domain.Order;
import hello.jpql.domain.Team;
import hello.jpql.dto.ResultDto;
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
class ProjectionTest {
    /*
    프로젝션
        • SELECT 절에 조회할 대상을 지정하는 것
        • 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터 타입)
        • SELECT m FROM Member m -> 엔티티 프로젝션
        • SELECT m.team FROM Member m -> 엔티티 프로젝션
        • SELECT m.address FROM Member m -> 임베디드 타입 프로젝션
        • SELECT m.name, m.age FROM Member m -> 스칼라 타입 프로젝션
        • DISTINCT 로 중복 제거

    프로젝션 - 여러 값 조회
        • SELECT m.name, m.age FROM Member m
            1. Query 타입으로 조회
            2. Object[] 타입으로 조회
            3. new 명령어로 조회
                • 단순 값을 DTO 로 바로 조회(SELECT new jpql.ResultDto(m.name, m.age) FROM Member m)
                • 패키지 명을 포함한 전체 클래스 명 입력
                • 순서와 타입이 일치하는 생성자 필요
    */
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("엔티티 프로젝션")
    void test1() {
        Team team = new Team();
        team.setName("팀");
        em.persist(team);

        Member member = new Member("홍길동", 10);
        member.setTeam(team);
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> memberList = em.createQuery("select m from Member m", Member.class).getResultList();
        memberList.forEach(m -> {
            log.info("member 이름 : {}", m.getName());
        });

        List<Team> teamList = em.createQuery("select m.team from Member m", Team.class).getResultList();
        teamList.forEach(t -> {
            log.info("team 이름 : {}", t.getName());
        });
    }

    @Test
    @DisplayName("임베디드 타입 프로젝션")
    void test2() {
        Order order = new Order();
        order.setAddress(new Address("111", "222", "333"));
        em.persist(order);

        em.flush();
        em.clear();

        List<Address> addressList = em.createQuery("select o.address from Order o", Address.class).getResultList();
        addressList.forEach(a -> {
            log.info("address : {}", a);
        });
    }

    @Test
    @DisplayName("스칼라 타입 프로젝션")
    void test3() {
        Member member = new Member("홍길동", 10);
        em.persist(member);

        em.flush();
        em.clear();

        //1. Query 타입으로 조회
        //List resultList = em.createQuery("select m.name, m.age from Member m").getResultList();
        //Object o = resultList.get(0);
        //Object[] result = (Object[]) o;

        //2. Object[] 타입으로 조회
        //List<Object[]> resultList = em.createQuery("select m.name, m.age from Member m").getResultList();
        //Object[] result = resultList.get(0);

        //log.info("name : {}", result[0]);
        //log.info("age : {}", result[1]);

        //3. new 명령어로 조회
        List<ResultDto> resultList = em.createQuery("select new hello.jpql.dto.ResultDto(m.name, m.age) from Member m", ResultDto.class).getResultList();
        ResultDto dto = resultList.get(0);
        log.info("result : {}", dto);
    }
}
