package com.example.member.dto;

import com.example.member.domain.Gender;
import com.example.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfileFormDTO {
    private Long memNo;

    private String memId;

    private String memPw;

    private String memName;

    private Gender memGender;

    private String memEmail;

    private String memPhone;

    private String memPostCode;

    private String memAddress1;

    private String memAddress2;

    private String uploadFileName;

    public ProfileFormDTO(Member member) {
        this.memNo = member.getId();
        this.memId = member.getMemberId();
        this.memPw = member.getMemberPassword();
        this.memName = member.getMemberName();
        this.memGender = member.getMemberGender();
        this.memEmail = member.getMemberEmail();
        this.memPhone = member.getMemberPhone();
        this.memPostCode = member.getAddress().getPostCode();
        this.memAddress1 = member.getAddress().getAddress();
        this.memAddress2 = member.getAddress().getDetailAddress();
        if(member.getProfile() != null) {
            this.uploadFileName = member.getProfile().getUploadFileName();
        }
    }
}
