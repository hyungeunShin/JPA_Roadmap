package com.example.user.dto;

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
public class EditUserDTO {
    @NotNull
    private Long id;

    private String newPassword;

    private String checkPassword;

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
    private String address;

    private String detailAddress;

    private MultipartFile profile;
}
