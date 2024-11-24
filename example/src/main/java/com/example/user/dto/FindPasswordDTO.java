package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindPasswordDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String email;
}
