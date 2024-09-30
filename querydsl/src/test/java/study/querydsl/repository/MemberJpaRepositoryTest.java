package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).orElseThrow(NullPointerException::new);
        assertThat(findMember).isEqualTo(member);

        List<Member> list1 = memberJpaRepository.findAll();
        assertThat(list1).containsExactly(member);

        List<Member> list2 = memberJpaRepository.findByUsername("member1");
        assertThat(list2).containsExactly(member);
    }
}