package com.example.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.example.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository repository;

    @Test
    void testMember() {
        Member member = new Member("memberA");

        Member saved = repository.save(member);
        Member find = repository.find(saved.getId());

        assertThat(find.getId()).isEqualTo(member.getId());
        assertThat(find.getUsername()).isEqualTo(member.getUsername());

        assertThat(find).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        repository.save(member1);
        repository.save(member2);

        Member find1 = repository.findById(member1.getId()).orElse(null);
        Member find2 = repository.findById(member2.getId()).orElse(null);
        assertThat(find1).isEqualTo(member1);
        assertThat(find2).isEqualTo(member2);

        List<Member> all = repository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = repository.count();
        assertThat(count).isEqualTo(2);

        repository.delete(member1);
        repository.delete(member2);

        long delete = repository.count();
        assertThat(delete).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        repository.save(m1);
        repository.save(m2);

        List<Member> result = repository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void paging() {
        //given
        int age = 10;
        repository.save(new Member("member1", age));
        repository.save(new Member("member2", age));
        repository.save(new Member("member3", age));
        repository.save(new Member("member4", age));
        repository.save(new Member("member5", age));
        int offset = 0;
        int limit = 3;

        //when
        List<Member> members = repository.findByPage(age, offset, limit);
        long totalCount = repository.totalCount(age);

        //페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..
        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate() {
        //given
        repository.save(new Member("member1", 10));
        repository.save(new Member("member2", 19));
        repository.save(new Member("member3", 20));
        repository.save(new Member("member4", 21));
        repository.save(new Member("member5", 40));

        //when
        int resultCount = repository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }
}