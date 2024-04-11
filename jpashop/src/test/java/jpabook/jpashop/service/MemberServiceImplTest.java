package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {
    @Autowired
    MemberService service;

    @Autowired
    MemberRepository repository;

    @DisplayName("회원 가입")
    @Test
    void join() {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = service.join(member);

        //then
        assertThat(member).isEqualTo(repository.findOne(saveId));
    }

    @DisplayName("중복 회원 가입")
    @Test
    void joinException() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //when
        service.join(member1);
        assertThatThrownBy(() -> service.join(member2)).isInstanceOf(IllegalStateException.class);
    }
}