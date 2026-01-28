package com.example.querydsl.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDTO;
import com.example.querydsl.entity.Member;
import com.example.querydsl.repository.MemberJpaRepository;
import com.example.querydsl.repository.MemberRepository;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;

    private final MemberRepository memberRepository;

    //http://localhost/v1/members?teamName=teamB&ageGoe=31&ageLoe=35
    @GetMapping("/v1/members")
    public List<MemberTeamDTO> searchMemberV1(MemberSearchCondition condition) {
        log.info("{}", condition);
        return memberJpaRepository.search(condition);
    }

    //http://localhost/v2/members?size=5&page=0
    @GetMapping("/v2/members")
    public Page<MemberTeamDTO> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageSimple(condition, pageable);
    }

    //http://localhost/v3/members?size=5&page=0
    @GetMapping("/v3/members")
    public Page<MemberTeamDTO> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageComplex(condition, pageable);
    }

    //http://localhost/extension?username=member1&age=1
    @GetMapping("/extension")
    public List<MemberTeamDTO> extension(@QuerydslPredicate(root = Member.class) Predicate predicate) {
        /*
        Querydsl Web Support
            - public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member>

        한계점
            - 단순한 조건만 가능
            - 조건을 커스텀하는 기능이 복잡하고 명시적이지 않음
            - 컨트롤러가 Querydsl 에 의존
            - 복잡한 실무환경에서 사용하기에는 한계가 명확

        참고: https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html
        */
        Stream<Member> stream = StreamSupport.stream(memberRepository.findAll(predicate).spliterator(), false);
        return stream.map(MemberTeamDTO::new).toList();
    }
}
