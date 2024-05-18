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
    private String newPw;

    @NotEmpty
    private String newPw2;

    public ResetPwDTO(Long id) {
        this.id = id;
    }
}
