package com.example.user.service;

import com.example.attachfile.domain.AttachFile;
import com.example.user.domain.Address;
import com.example.user.domain.Role;
import com.example.user.domain.User;
import com.example.user.dto.*;
import com.example.user.repository.UserJpaRepository;
import com.example.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
//    private final UserRepository repository;

    private final UserJpaRepository repository;

    private final FileStore fileStore;

    private final PasswordEncoder pe;

    @Override
    @Transactional
    public void register(RegisterUserDTO dto) throws IOException {
        AttachFile profile = saveProfile(dto.getProfile());
        User user = dto.toEntity(pe.encode(dto.getPassword()), Role.USER, profile);
        repository.save(user);
    }

    @Override
    public boolean duplicatedId(String username) {
        return repository.findByUsername(username).isPresent();
    }

    @Override
    public boolean duplicatedEmail(Long id, String email) {
        if(id == null) {
            return repository.findByEmail(email).isPresent();
        } else {
            return repository.findByIdNotAndEmail(id, email).isPresent();
        }
    }

    @Override
    public String findId(FindIdDTO dto) {
        return repository.findByNameAndEmail(dto.getName(), dto.getEmail()).orElseThrow(NullPointerException::new).getUsername();
    }

    @Override
    public User findPassword(FindPasswordDTO dto) {
        return repository.findByUsernameAndNameAndEmail(dto.getUsername(), dto.getName(), dto.getEmail()).orElseThrow(NullPointerException::new);
    }

    @Override
    @Transactional
    public void changePassword(ResetPasswordDTO dto) {
        User user = repository.findById(dto.getId()).orElseThrow(NullPointerException::new);
        user.changePassword(pe.encode(dto.getNewPassword()));
    }

    @Override
    public EditUserViewDTO findUser(Long id) {
        return new EditUserViewDTO(repository.findById(id).orElseThrow(NullPointerException::new));
    }

    @Override
    @Transactional
    public User edit(EditUserDTO dto) throws IOException {
        User user = repository.findById(dto.getId()).orElseThrow(NullPointerException::new);

        AttachFile profile = saveProfile(dto.getProfile());

        user.editProfile(
            StringUtils.hasText(dto.getNewPassword()) ? pe.encode(dto.getNewPassword()) : null
            , dto.getName(), dto.getGender(), dto.getEmail(), dto.getPhone()
            , new Address(dto.getPostCode(), dto.getAddress(), dto.getDetailAddress())
            , profile
        );

        return user;
    }

    private AttachFile saveProfile(MultipartFile multipartFile) throws IOException {
        if(multipartFile != null && !multipartFile.isEmpty()) {
            String originalFilename = multipartFile.getOriginalFilename();

            String uploadFileName = fileStore.uploadFile(multipartFile);
            String ext = fileStore.extractExt(originalFilename);
            String uploadFilePath = fileStore.getFullPath(uploadFileName);


            return AttachFile.builder()
                             .originalFileName(originalFilename)
                             .uploadFileName(uploadFileName)
                             .uploadFilePath(uploadFilePath)
                             .fileSize(multipartFile.getSize())
                             .fileFancySize(FileUtils.byteCountToDisplaySize(multipartFile.getSize()))
                             .fileExt(ext)
                             .build();
        }

        return null;
    }
}
