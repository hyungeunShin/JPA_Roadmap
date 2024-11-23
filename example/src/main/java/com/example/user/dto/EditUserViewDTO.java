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
public class EditUserViewDTO {
    private Long id;

    private String name;

    private Gender gender;

    private String email;

    private String phone;

    private String postCode;

    private String address;

    private String detailAddress;

    public EditUserViewDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.postCode = user.getAddress().getPostCode();
        this.address = user.getAddress().getAddress();
        this.detailAddress = user.getAddress().getDetailAddress();
    }
}
