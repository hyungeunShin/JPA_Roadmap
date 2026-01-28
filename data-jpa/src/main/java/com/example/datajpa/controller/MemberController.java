package com.example.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import com.example.datajpa.dto.MemberDTO;
import com.example.datajpa.entity.Member;
import com.example.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @PostMapping("/members/new")
    public Long createMember(@RequestParam("username") String username, @RequestParam("age") int age) {
        Member saved = memberRepository.save(new Member(username, age));
        return saved.getId();
    }

    @GetMapping("/members/{id}/v1")
    public String findMember1(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).orElseThrow(NullPointerException::new);
        return member.getUsername();
    }

    //HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
    @GetMapping("/members/{id}/v2")
    public String findMember2(@PathVariable("id") Member member) {
        //HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
        //도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
        return member.getUsername();
    }

    /*
    - 파라미터로 Pageable 을 받을 수 있다.
    - Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성

    요청 파라미터
        - 예) /members?page=0&size=3&sort=id,desc&sort=username,desc
        - page: 현재 페이지, 0부터 시작한다.
        - size: 한 페이지에 노출할 데이터 건수
        - sort: 정렬 조건을 정의한다. 예) 정렬 컬럼,정렬 방향...(ASC(생략 가능) | DESC)
          localhost/members?page=0&size=3&sort=username,desc&sort=id

    글로벌 설정
        - spring.data.web.pageable.default-page-size=20
        - spring.data.web.pageable.max-page-size=2000

    개별 설정
        - @PageableDefault(size = 12, sort = "username", direction = Sort.Direction.DESC) Pageable pageable

    접두사
        - 페이징 정보가 둘 이상이면 접두사로 구분
        - @Qualifier 에 접두사명 추가 "{접두사명}_xxx"
        - 예제: /members?member_page=0&order_page=1

    Page 내용을 DTO 로 변환하기
        - 엔티티를 API 로 노출하면 다양한 문제가 발생한다. 그래서 엔티티를 꼭 DTO 로 변환해서 반환해야 한다.
        - Page 는 map() 을 지원해서 내부 데이터를 다른 것으로 변경할 수 있다.

    Page 를 1부터 시작하기
        - 스프링 데이터는 Page 를 0부터 시작한다.
        - 만약 1부터 시작하려면?
            1. Pageable, Page 를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리한다.
            그리고 직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘긴다.
            물론 응답값도 Page 대신에 직접 만들어서 제공해야 한다.
            2. spring.data.web.pageable.one-indexed-parameters 를 true 로 설정한다.
            그런데 이 방법은 web 에서 page 파라미터를 -1 처리 할 뿐이다.
            따라서 응답값인 Page 에 모두 0 페이지 인덱스를 사용하는 한계가 있다.

            스프링 3.3부터 @EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO) 직렬화 문제 때문에 새로 들어온 스펙
            하지만 이걸 추가하면 spring.data.web.pageable.one-indexed-parameters=true 가 동작하지 않는다.
            따라서 @Bean 으로 직접 구현
    */
    @GetMapping("/members")
    public Page<Member> list1(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members_page")
    public Page<Member> list2(@PageableDefault(size = 5, sort = "age", direction = Sort.Direction.DESC) Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members_dto")
    public Page<MemberDTO> list3(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDTO::new);
    }

    //@PostConstruct
    public void init() {
        for(int i = 0; i < 100; i++) {
            memberRepository.save(new Member("member" + i, i));
        }
    }
}
