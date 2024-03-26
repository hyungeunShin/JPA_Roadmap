package jpabook.jpashop.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemEditForm {
    @NotNull(message = "아이디는 필수입니다.")
    private Long id;

    private String name;
    private int price;
    private int quantity;
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
