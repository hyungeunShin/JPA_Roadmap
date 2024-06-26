package com.example.user.service;

import com.example.user.domain.Address;
import com.example.attachfile.domain.AttachFile;
import com.example.user.domain.Role;
import com.example.user.domain.User;
import com.example.user.dto.*;
import com.example.user.repository.UserRepository;
import com.example.util.FileStore;
import com.example.util.ServiceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private final FileStore fileStore;

    private final PasswordEncoder pe;

    @Override
    @Transactional
    public ServiceResult register(RegisterUserDTO dto) throws IOException, NullPointerException {
        ServiceResult result = null;

        AttachFile profile = saveProfile(dto.getProfile());

        User user = User.builder()
                        .username(dto.getUsername())
                        .password(pe.encode(dto.getPassword()))
                        .name(dto.getName())
                        .gender(dto.getGender())
                        .email(dto.getEmail())
                        .phone(dto.getPhone())
                        .address(new Address(dto.getPostCode(), dto.getAddress1(), dto.getAddress2()))
                        .role(Role.USER)
                        .profile(profile)
                        .build();

        Long status = repository.save(user);

        if(status > 0) {
            result = ServiceResult.CREATED;
        } else {
            result = ServiceResult.FAILED;
        }

        return result;
    }

    @Override
    public ServiceResult idCheck(String username) {
        ServiceResult result = null;

        Optional<User> user = repository.findByUsername(username);

        if(user.isPresent()) {
            result = ServiceResult.EXIST;
        } else {
            result = ServiceResult.NOTEXIST;
        }

        return result;
    }

    @Override
    public String findId(FindIdDTO dto) throws NullPointerException {
        return repository.findByNameAndPhone(dto.getName(), dto.getPhone()).orElseThrow(NullPointerException::new).getUsername();
    }

    @Override
    public User findPw(FindPwDTO dto) throws NullPointerException {
        return repository.findByUsernameAndNameAndPhone(dto.getUsername(), dto.getName(), dto.getPhone()).orElseThrow(NullPointerException::new);
    }

    @Override
    @Transactional
    public void changePassword(ResetPwDTO dto) throws NullPointerException {
        User user = repository.findById(dto.getId()).orElseThrow(NullPointerException::new);
        user.changePassword(pe.encode(dto.getNewPw()));
    }

    @Override
    public ProfileFormDTO findUser(Long id) throws NullPointerException {
        User user = repository.findById(id).orElseThrow(NullPointerException::new);
        return new ProfileFormDTO(user);
    }

    @Override
    @Transactional
    public User editProfile(ProfileEditDTO dto) throws IOException, NullPointerException {
        User user = repository.findById(dto.getId()).orElseThrow(NullPointerException::new);

        AttachFile profile = saveProfile(dto.getProfile());
        dto.setFile(profile);

        if(StringUtils.hasText(dto.getPassword())) {
            dto.setPassword(pe.encode(dto.getPassword()));
        }

        user.editProfile(dto);

        return user;
    }

    private AttachFile saveProfile(MultipartFile multipartFile) throws IOException, NullPointerException {
        AttachFile profile = null;
        if(!multipartFile.isEmpty()) {
            String ext = fileStore.extractExt(Objects.requireNonNull(multipartFile.getOriginalFilename(), "이미지 이름이 존재하지 않습니다."));
            String uploadFileName = fileStore.uploadFile(multipartFile);
            String uploadPath = fileStore.getFullPath(uploadFileName);

            profile = new AttachFile(multipartFile, uploadFileName, uploadPath, ext);
        }
        return profile;
    }
}
