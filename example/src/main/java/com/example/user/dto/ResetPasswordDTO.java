package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResetPasswordDTO {
    @NotNull
    private Long id;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String checkPassword;

    public ResetPasswordDTO(Long id) {
        this.id = id;
    }
}
