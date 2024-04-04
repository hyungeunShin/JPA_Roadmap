package hello.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Book extends Item {
    private String author;

    private String isbn;
}
