package com.example.user.dto;

import com.example.attachfile.domain.AttachFile;
import com.example.user.domain.Address;
import com.example.user.domain.Gender;
import com.example.user.domain.Role;
import com.example.user.domain.User;
import jakarta.validation.constraints.AssertTrue;
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

    @AssertTrue
    private Boolean agree;

    @NotBlank
    private String postCode;

    @NotBlank
    private String address;

    private String detailAddress;

    private MultipartFile profile;

    public User toEntity(String password, Role role, AttachFile profile) {
        return User.builder()
                   .username(this.username)
                   .password(password)
                   .name(this.name)
                   .gender(this.gender)
                   .email(this.email)
                   .phone(this.phone)
                   .agree(this.agree)
                   .address(new Address(this.postCode, this.address, this.detailAddress))
                   .role(role)
                   .profile(profile)
                   .build();
    }
}
