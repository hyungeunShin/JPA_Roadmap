package hello.jpql.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Movie extends Item {
    private String director;

    private String actor;
}
