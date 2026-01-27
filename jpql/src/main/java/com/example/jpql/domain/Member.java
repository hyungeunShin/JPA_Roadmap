package com.example.jpql.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@NamedQuery(name = "Member.findByName", query = "select m from Member m where m.name = :name")
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private MemberType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
