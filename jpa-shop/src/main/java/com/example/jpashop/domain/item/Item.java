package com.example.jpashop.domain.item;

import jakarta.persistence.*;
import com.example.jpashop.domain.Category;
import com.example.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int quantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    public void addStock(int quantity) {
        this.quantity += quantity;
    }

    public void decreaseStock(int quantity) {
        int restStock = this.quantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.quantity = restStock;
    }
}
