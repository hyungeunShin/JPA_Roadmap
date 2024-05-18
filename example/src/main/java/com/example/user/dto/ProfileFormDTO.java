package com.example.user.dto;

import com.example.user.domain.Gender;
import com.example.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfileFormDTO {
    private Long id;

    private String username;

    private String password;

    private String name;

    private Gender gender;

    private String email;

    private String phone;

    private String postCode;

    private String address1;

    private String address2;

    private String uploadFileName;

    public ProfileFormDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.postCode = user.getAddress().getPostCode();
        this.address1 = user.getAddress().getAddress();
        this.address2 = user.getAddress().getDetailAddress();
        if(user.getProfile() != null) {
            this.uploadFileName = user.getProfile().getUploadFileName();
        }
    }
}
