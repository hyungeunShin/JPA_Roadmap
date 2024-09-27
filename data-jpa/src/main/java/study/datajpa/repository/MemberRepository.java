package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDTO;
import study.datajpa.entity.Member;
import study.datajpa.projection.*;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
    //메소드 이름으로 쿼리 생성
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //JPA NamedQuery
    @Query(name = "Member.findByUsername")
    List<Member> jpaNamedQuery(@Param("username") String username);

    //@Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의
    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> queryAnnotation(@Param("username") String username, @Param("age") int age);

    //단순히 값 하나를 조회
    //JPA 값 타입(@Embedded)도 이 방식으로 조회 가능
    @Query("select m.username from Member m")
    List<String> findUsernameOnly();

    //DTO 로 직접 조회
    @Query("select new study.datajpa.dto.MemberDTO(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDTO> findMemberDTO();

    //위치 기반 파라미터 바인딩
    @Query("select m from Member m where m.username = ?1")
    List<Member> locationBased(@Param("username") String username);

    //이름 기반 파라미터 바인딩
    @Query("select m from Member m where m.username = :username")
    List<Member> nameBased(@Param("username") String username);

    //컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> collectionBased(@Param("names") List<String> names);

    //페이징
    Page<Member> findByAgeGreaterThan(int age, Pageable pageable);    //count 쿼리 사용
    //Slice<Member> findByAgeGreaterThan(int age, Pageable pageable);   //count 쿼리 사용 X
    List<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    //TOP 쿼리
    List<Member> findTop3By(Sort sort);

    @Query("select m from Member m left join m.team t")
    List<Member> leftJoinOptimization();

    //벌크성 수정 쿼리
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //JPQL 페치 조인
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(String username);

    //NamedEntityGraph
    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findMemberNamedEntityGraph();

    //JPA 힌트
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Member> findReadOnlyByUsername(String username);

    //Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findByUsernameAndAge(String username, int age);

    //인터페이스 기반 closed projection
    List<UsernameOnly> findUsernameProjectionByUsername(String username);

    //인터페이스 기반 open projection
    List<UsernameAndAgeOnly> findUsernameAndAgeProjectionByUsername(String username);

    //클래스 기반 projection
    List<UsernameOnlyDTO> findUsernameDTOByUsername(String username);

    //동적 projection
    <T> List<T> findTypeProjectionByUsername(String username, Class<T> type);

    //중첩 구조 projection
    List<NestedClosedProjection> findNestedProjectionByUsername(String username);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    List<Member> findByNativeQuery(String username);

    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName FROM member m left join team t ON m.team_id = t.team_id"
        , countQuery = "SELECT count(*) from member"
        , nativeQuery = true
    )
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
