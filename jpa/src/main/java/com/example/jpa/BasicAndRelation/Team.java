package com.example.jpa.BasicAndRelation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Team {
    @Id
    private Long id;

    private String name;

    //팀 -> 회원 연관관계 1개(단방향)
    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();

    public Team() {

    }

    public Team(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

