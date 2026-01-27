package com.example.jpa.BasicAndRelation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {
    @Id
    private Long id;

    private String name;

    //객체를 테이블에 맞추어 모델링(참조 대신에 외래 키를 그대로 사용)
    //@Column(name = "TEAM_ID")
    //private Long teamId;

    //회원 -> 팀 연관관계 1개(단방향)
    @ManyToOne  //Member 입장에선 N 이고 Team 입장에선 1
    @JoinColumn(name = "team_id")
    private Team team;

    public Member() {

    }

    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
