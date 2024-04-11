package com.example.member.service;

import com.example.common.domain.Address;
import com.example.common.domain.AttachFile;
import com.example.member.dto.*;
import com.example.util.ServiceResult;
import com.example.member.domain.Member;
import com.example.member.repository.MemberRepository;
import com.example.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository repository;

    private final FileStore fileStore;

    @Override
    public Member login(LoginDTO dto) throws NullPointerException {
        return repository.findMemberByIdAndPw(dto.getMemId(), dto.getMemPw()).orElseThrow(NullPointerException::new);
    }

    @Override
    @Transactional
    public ServiceResult register(RegisterMemberDTO dto) throws IOException, NullPointerException {
        ServiceResult result = null;

        AttachFile profile = saveProfile(dto.getProfile());

        Member member = Member.builder()
                              .memberId(dto.getMemId())
                              .memberPassword(dto.getMemPw())
                              .memberName(dto.getMemName())
                              .memberGender(dto.getMemGender())
                              .memberEmail(dto.getMemEmail())
                              .memberPhone(dto.getMemPhone())
                              .address(new Address(dto.getMemPostCode(), dto.getMemAddress1(), dto.getMemAddress2()))
                              .profile(profile)
                              .build();

        Long status = repository.save(member);

        if(status > 0) {
            result = ServiceResult.CREATED;
        } else {
            result = ServiceResult.FAILED;
        }

        return result;
    }

    @Override
    public ServiceResult idCheck(String memId) {
        ServiceResult result = null;

        Long cnt = repository.findMemberById(memId);

        if(cnt > 0) {
            result = ServiceResult.EXIST;
        } else {
            result = ServiceResult.NOTEXIST;
        }

        return result;
    }

    @Override
    public String findId(FindIdDTO dto) throws NullPointerException {
        return repository.findIdByNameAndPhone(dto.getMemName(), dto.getMemPhone()).orElseThrow(NullPointerException::new);
    }

    @Override
    public String findPw(FindPwDTO dto) throws NullPointerException {
        return repository.findPwByIdAndNameAndPhone(dto.getMemId(), dto.getMemName(), dto.getMemPhone()).orElseThrow(NullPointerException::new);
    }

    @Override
    public ProfileFormDTO findMember(Long id) throws NullPointerException {
        Member member = repository.findMemberByNo(id).orElseThrow(NullPointerException::new);
        return new ProfileFormDTO(member);
    }

    @Override
    @Transactional
    public Member editProfile(ProfileEditDTO dto) throws IOException, NullPointerException {
        Member member = repository.findMemberByNo(dto.getMemNo()).orElseThrow(NullPointerException::new);

        AttachFile profile = saveProfile(dto.getProfile());
        dto.setFile(profile);

        member.editProfile(dto);

        return member;
    }

    private AttachFile saveProfile(MultipartFile multipartFile) throws IOException {
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
