package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends Item {
    private String director;

    private String actor;

    public Movie(String name, int price, int quantity, String director, String actor) {
        super(name, price, quantity);
        this.director = director;
        this.actor = actor;
    }

    public Movie(Long id, String name, int price, int quantity, String director, String actor) {
        super(id, name, price, quantity);
        this.director = director;
        this.actor = actor;
    }
}
