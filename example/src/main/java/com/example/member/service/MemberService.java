package com.example.member.service;

import com.example.member.domain.Member;
import com.example.member.dto.*;
import com.example.util.ServiceResult;

import java.io.IOException;

public interface MemberService {
    Member login(LoginDTO dto) throws NullPointerException;

    ServiceResult register(RegisterMemberDTO dto) throws IOException, NullPointerException;

    ServiceResult idCheck(String memId);

    String findId(FindIdDTO dto) throws NullPointerException;

    String findPw(FindPwDTO dto) throws NullPointerException;

    ProfileFormDTO findMember(Long id) throws NullPointerException;

    Member editProfile(ProfileEditDTO dto) throws IOException, NullPointerException;
}
