package com.example.jpashop.service;

import com.example.jpashop.domain.Member;

import java.util.List;

public interface MemberService {
    Long join(Member member);

    List<Member> findMembers();

    Member findOne(Long memberId);

    void update(Long id, String name);
}
