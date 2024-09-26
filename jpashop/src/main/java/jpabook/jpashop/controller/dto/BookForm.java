package jpabook.jpashop.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BookForm {
    private Long id;
    private String name;
    private int price;
    private int quantity;
    private String author;
    private String isbn;
}
