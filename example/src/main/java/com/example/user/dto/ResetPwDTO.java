package com.example.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResetPwDTO {
    @NotNull
    private Long id;

    @NotEmpty
    private String newPassword;

    @NotEmpty
    private String checkPassword;

    public ResetPwDTO(Long id) {
        this.id = id;
    }
}
