package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository repository;

    @Transactional  //readOnly = false 로
    @Override
    public Long join(Member member) {
        validateDuplicateMember(member);
        repository.save(member);
        return member.getId();
    }

    @Override
    public List<Member> findMembers() {
        return repository.findAll();
    }

    @Override
    public Member findOne(Long memberId) {
        return repository.findOne(memberId);
    }

    private void validateDuplicateMember(Member member) {
        List<Member> list = repository.findByName(member.getName());
        if(!list.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
