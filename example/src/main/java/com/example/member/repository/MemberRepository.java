package com.example.member.repository;

import com.example.member.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public Optional<Member> findMemberByIdAndPw(String memId, String memPw) {
        return em.createQuery("select m from Member m left join fetch m.profile where m.memberId = :memId and m.memberPassword = :memPw", Member.class)
                 .setParameter("memId", memId)
                 .setParameter("memPw", memPw)
                 .getResultStream()
                 .findFirst();
    }

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Long findMemberById(String memId) {
        return em.createQuery("select count(m) from Member m where m.memberId = :memId", Long.class)
                 .setParameter("memId", memId)
                 .getSingleResult();
    }

    public Optional<String> findIdByNameAndPhone(String memName, String memPhone) {
        return em.createQuery("select m.memberId from Member m where m.memberName = :memName and m.memberPhone = :memPhone", String.class)
                 .setParameter("memName", memName)
                 .setParameter("memPhone", memPhone)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<String> findPwByIdAndNameAndPhone(String memId, String memName, String memPhone) {
        return em.createQuery("select m.memberPassword from Member m where m.memberId = :memId and m.memberName = :memName and m.memberPhone = :memPhone", String.class)
                 .setParameter("memId", memId)
                 .setParameter("memName", memName)
                 .setParameter("memPhone", memPhone)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<Member> findMemberByNo(Long memberNo) {
        return em.createQuery("select m from Member m left join fetch m.profile where m.id = :memberNo", Member.class)
                 .setParameter("memberNo", memberNo)
                 .getResultStream()
                 .findFirst();
    }
}
