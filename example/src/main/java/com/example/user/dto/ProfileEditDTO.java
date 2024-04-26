package com.example.user.dto;

import com.example.common.domain.AttachFile;
import com.example.user.domain.Gender;
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
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotNull
    private Gender gender;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String postCode;

    @NotBlank
    private String address1;

    private String address2;

    private String uploadFileName;

    private MultipartFile profile;

    private AttachFile file;
}
