package com.example.user.service;

import com.example.user.domain.User;
import com.example.user.dto.*;

import java.io.IOException;

public interface UserService {
    void register(RegisterUserDTO dto) throws IOException;

    boolean duplicatedId(String username);

    boolean duplicatedEmail(Long id, String email);

    String findId(FindIdDTO dto);

    User findPassword(FindPasswordDTO dto);

    void changePassword(ResetPasswordDTO dto);

    EditUserViewDTO findUser(Long id);

    User edit(EditUserDTO dto) throws IOException;
}
