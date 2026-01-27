package com.example.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class Movie extends Item {
    private String director;

    private String actor;
}
