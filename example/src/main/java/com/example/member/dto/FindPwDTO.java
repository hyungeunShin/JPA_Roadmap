package com.example.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindPwDTO {
    private String memId;

    private String memPhone;

    private String memName;
}
