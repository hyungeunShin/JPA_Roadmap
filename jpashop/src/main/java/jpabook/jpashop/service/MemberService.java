package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;

import java.util.List;

public interface MemberService {
    Long join(Member member);

    List<Member> findMembers();

    Member findOne(Long memberId);
}
