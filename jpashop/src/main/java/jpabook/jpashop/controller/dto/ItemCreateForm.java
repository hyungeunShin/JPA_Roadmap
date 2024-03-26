package jpabook.jpashop.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemCreateForm {
    private String name;
    private int price;
    private int quantity;

    @NotEmpty(message = "상품 종류는 필수값입니다.")
    private String itemType;

    //책
    private String author;
    private String isbn;

    //앨범
    private String artist;
    private String etc;

    //영화
    private String director;
    private String actor;
}
