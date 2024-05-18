package com.example.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindPwDTO {
    @NotEmpty
    private String username;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String name;
}
