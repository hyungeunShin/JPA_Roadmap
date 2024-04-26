package com.example.user.service;

import com.example.user.domain.User;
import com.example.user.dto.*;
import com.example.util.ServiceResult;

import java.io.IOException;

public interface UserService {
    ServiceResult register(RegisterUserDTO dto) throws IOException, NullPointerException;

    ServiceResult idCheck(String username);

    String findId(FindIdDTO dto) throws NullPointerException;

    User findPw(FindPwDTO dto) throws NullPointerException;

//    ProfileFormDTO findMember(Long id) throws NullPointerException;
//
//    User editProfile(ProfileEditDTO dto) throws IOException, NullPointerException;
}
