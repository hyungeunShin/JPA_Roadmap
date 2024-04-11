package com.example.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDTO {
    @NotEmpty
    private String memId;

    @NotEmpty
    private String memPw;
}
