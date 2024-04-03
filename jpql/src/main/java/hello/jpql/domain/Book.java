package hello.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Book extends Item {
    private String author;

    private String isbn;
}
