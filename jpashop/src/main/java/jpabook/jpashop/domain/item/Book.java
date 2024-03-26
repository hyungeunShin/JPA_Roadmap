package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("B")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends Item {
    private String author;

    private String isbn;

    public Book(String name, int price, int quantity) {
        super(name, price, quantity);
    }

    public Book(String name, int price, int quantity, String author, String isbn) {
        super(name, price, quantity);
        this.author = author;
        this.isbn = isbn;
    }

    public Book(Long id, String name, int price, int quantity, String author, String isbn) {
        super(id, name, price, quantity);
        this.author = author;
        this.isbn = isbn;
    }
}
