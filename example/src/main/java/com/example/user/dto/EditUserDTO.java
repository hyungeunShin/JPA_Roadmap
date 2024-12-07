package com.example.user.dto;

import com.example.user.domain.Gender;
import com.example.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EditUserDTO {
    private String username;

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

    public EditUserDTO(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.postCode = user.getAddress().getPostCode();
        this.address = user.getAddress().getAddress();
        this.detailAddress = user.getAddress().getDetailAddress();
    }
}
