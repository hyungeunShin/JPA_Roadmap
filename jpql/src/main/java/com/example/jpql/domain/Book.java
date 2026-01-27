package com.example.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
public class Book extends Item {
    private String author;

    private String isbn;
}
