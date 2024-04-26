package com.example.user.domain;

import com.example.board.domain.Board;
import com.example.common.domain.Address;
import com.example.common.domain.AttachFile;
import com.example.common.domain.TimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;

    private String pw;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private String phone;

    private Boolean agree;

    @Embedded
    private Address address;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Board> boardList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private AttachFile profile;

    @Builder
    public User(String username, String password, String name, Gender gender, String email, String phone, Boolean agree, Address address, Role role, AttachFile profile) {
        this.username = username;
        this.pw = password;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.agree = agree;
        this.address = address;
        this.role = role;
        this.profile = profile;
    }

    //    public void editProfile(ProfileEditDTO dto) {
//        this.memberId = dto.getMemId();
//        this.memberPassword = dto.getMemPw();
//        this.memberName = dto.getMemName();
//        this.memberGender = dto.getMemGender();
//        this.memberEmail = dto.getMemEmail();
//        this.memberPhone = dto.getMemPhone();
//        this.address = new Address(dto.getMemPostCode(), dto.getMemAddress1(), dto.getMemAddress2());
//        if(dto.getFile() != null) {
//            this.profile = dto.getFile();
//        }
//    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.pw;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.getDescription()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
