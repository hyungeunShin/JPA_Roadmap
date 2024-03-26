package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("A")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends Item {
    private String artist;

    private String etc;

    public Album(String name, int price, int quantity, String artist, String etc) {
        super(name, price, quantity);
        this.artist = artist;
        this.etc = etc;
    }

    public Album(Long id, String name, int price, int quantity, String artist, String etc) {
        super(id, name, price, quantity);
        this.artist = artist;
        this.etc = etc;
    }
}
