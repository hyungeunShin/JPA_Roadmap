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
public class RegisterUserDTO {
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

    private Boolean agree;

    private MultipartFile profile;
}
