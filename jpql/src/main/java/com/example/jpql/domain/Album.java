package com.example.jpql.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@DiscriminatorValue("A")
public class Album extends Item {
    private String artist;
}
