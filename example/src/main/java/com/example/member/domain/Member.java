package com.example.member.domain;

import com.example.board.domain.Board;
import com.example.common.domain.Address;
import com.example.common.domain.AttachFile;
import com.example.common.domain.TimeEntity;
import com.example.member.dto.ProfileEditDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    private Long id;

    private String memberId;

    private String memberPassword;

    private String memberName;

    @Enumerated(EnumType.STRING)
    private Gender memberGender;

    private String memberEmail;

    private String memberPhone;

    private Boolean memberAgree;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Board> boardList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file_no")
    private AttachFile profile;

    @Builder
    public Member(String memberId, String memberPassword, String memberName, Gender memberGender, String memberEmail, String memberPhone, Boolean memberAgree, Address address, AttachFile profile) {
        this.memberId = memberId;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberGender = memberGender;
        this.memberEmail = memberEmail;
        this.memberPhone = memberPhone;
        this.memberAgree = memberAgree;
        this.address = address;
        this.profile = profile;
    }

    public void editProfile(ProfileEditDTO dto) {
        this.memberId = dto.getMemId();
        this.memberPassword = dto.getMemPw();
        this.memberName = dto.getMemName();
        this.memberGender = dto.getMemGender();
        this.memberEmail = dto.getMemEmail();
        this.memberPhone = dto.getMemPhone();
        this.address = new Address(dto.getMemPostCode(), dto.getMemAddress1(), dto.getMemAddress2());
        if(dto.getFile() != null) {
            this.profile = dto.getFile();
        }
    }
}
