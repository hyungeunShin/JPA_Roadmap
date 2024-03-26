package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Transactional
    @Override
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
}
