package com.example.jpashop.service;

import com.example.jpashop.domain.item.Item;
import com.example.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    @Transactional
    public void saveItem(Item item) {
        repository.save(item);
    }

    @Override
    public Item findOne(Long itemId) {
        return repository.findOne(itemId);
    }

    @Override
    public List<Item> findItems() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void updateItem(Long itemId, String name, int price, int quantity) {
        Item item = repository.findOne(itemId);
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
    }
}
