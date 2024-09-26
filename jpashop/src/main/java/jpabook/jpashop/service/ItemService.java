package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;

import java.util.List;

public interface ItemService {
    void saveItem(Item item);

    Item findOne(Long itemId);

    List<Item> findItems();

    void updateItem(Long itemId, String name, int price, int quantity);
}
