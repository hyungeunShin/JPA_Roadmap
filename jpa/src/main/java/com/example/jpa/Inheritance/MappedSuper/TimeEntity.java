package com.example.jpa.Inheritance.MappedSuper;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class TimeEntity {
    @Column(name = "insert_member")
    private String registerMember;

    private LocalDateTime registerDate;

    private LocalDateTime modifiedDate;
}
