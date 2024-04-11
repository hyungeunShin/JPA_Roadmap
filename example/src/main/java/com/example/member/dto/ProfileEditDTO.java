package com.example.member.dto;

import com.example.common.domain.AttachFile;
import com.example.member.domain.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class ProfileEditDTO {
    @NotNull
    private Long memNo;

    @NotBlank
    private String memId;

    @NotBlank
    private String memPw;

    @NotBlank
    private String memName;

    @NotNull
    private Gender memGender;

    @NotBlank
    private String memEmail;

    @NotBlank
    private String memPhone;

    @NotBlank
    private String memPostCode;

    @NotBlank
    private String memAddress1;

    private String memAddress2;

    private String uploadFileName;

    private MultipartFile profile;

    private AttachFile file;
}
