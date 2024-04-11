package com.example.member.service;

import com.example.util.ServiceResult;
import com.example.member.domain.Gender;
import com.example.member.dto.FindIdDTO;
import com.example.member.dto.FindPwDTO;
import com.example.member.dto.RegisterMemberDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberServiceImplTest {
    @Autowired
    private MemberService service;

    @Test
    @Order(1)
    @DisplayName("회원가입")
    void register() throws IOException {
        RegisterMemberDTO dto = new RegisterMemberDTO();
        dto.setMemId("id");
        dto.setMemPw("pw");
        dto.setMemName("name");
        dto.setMemGender(Gender.MALE);
        dto.setMemPhone("phone");
        dto.setMemPostCode("postcode");
        dto.setMemAddress1("address1");
        dto.setMemAddress2("address2");
        dto.setMemAgree(true);

        service.register(dto);
    }

    @Test
    @Order(2)
    @DisplayName("아이디 찾기")
    void findId() {
        FindIdDTO dto1 = new FindIdDTO();
        dto1.setMemName("name");
        dto1.setMemPhone("phone");

        FindIdDTO dto2 = new FindIdDTO();
        dto2.setMemName("wrong");
        dto2.setMemPhone("phone");

        String id1 = service.findId(dto1);

        assertThat(id1).isEqualTo("id");
        assertThatThrownBy(() -> service.findId(dto2)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @Order(3)
    @DisplayName("비밀번호 찾기")
    void findPw() {
        FindPwDTO dto1 = new FindPwDTO();
        dto1.setMemName("name");
        dto1.setMemPhone("phone");
        dto1.setMemId("id");

        FindPwDTO dto2 = new FindPwDTO();
        dto2.setMemName("name");
        dto2.setMemPhone("phone");
        dto2.setMemId("wrong");

        String pw = service.findPw(dto1);

        assertThat(pw).isEqualTo("pw");
        assertThatThrownBy(() -> service.findPw(dto2)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @Order(4)
    @DisplayName("아이디 중복 확인")
    void idCheck() {
        String duplicatedId = "id";
        String notDuplicatedId = "test";

        ServiceResult result1 = service.idCheck(duplicatedId);
        ServiceResult result2 = service.idCheck(notDuplicatedId);

        assertThat(result1).isEqualTo(ServiceResult.EXIST);
        assertThat(result2).isEqualTo(ServiceResult.NOTEXIST);
    }
}