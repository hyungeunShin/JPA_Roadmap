package com.example.jpa.Inheritance.MappedSuper;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MappedSuperTest2 extends TimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
