package com.example.user.dto;

import com.example.user.domain.Gender;
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
}
