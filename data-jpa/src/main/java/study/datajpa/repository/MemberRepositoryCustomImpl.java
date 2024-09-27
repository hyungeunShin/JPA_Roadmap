package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.entity.Member;

import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    @PersistenceContext
    EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
