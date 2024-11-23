package com.example.user.domain;

import com.example.attachfile.domain.AttachFile;
import com.example.board.domain.Board;
import com.example.util.TimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(name = "pw")
    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true)
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
    @JoinColumn(name = "file_id")
    private AttachFile profile;

    @Builder
    public User(String username, String password, String name, Gender gender, String email, String phone, Boolean agree, Address address, Role role, AttachFile profile) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.agree = agree;
        this.address = address;
        this.role = role;
        this.profile = profile;
    }

    public void editProfile(String password, String name, Gender gender, String email, String phone, Address address, AttachFile profile) {
        if(password != null) {
            this.password = password;
        }
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profile = profile;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
