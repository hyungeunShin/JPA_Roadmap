package hello.jpql.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@DiscriminatorValue("A")
@ToString
public class Album extends Item {
    private String artist;
}
