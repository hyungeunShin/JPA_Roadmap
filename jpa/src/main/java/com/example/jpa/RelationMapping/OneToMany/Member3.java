package com.example.jpa.RelationMapping.OneToMany;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member3 {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    //oneToMany 에서 양방향 연결을 하고 싶다면
    @ManyToOne
    @JoinColumn(name = "team_id", insertable = false, updatable = false)    //읽기 전용으로 만든다.
    //공식적으로 존재하는게 아닌 편법을 통해 생성
    private Team2 team;
}
