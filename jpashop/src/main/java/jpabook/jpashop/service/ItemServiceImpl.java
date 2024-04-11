package jpabook.jpashop.service;

import jpabook.jpashop.controller.dto.ItemEditForm;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
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

    @Transactional
    @Override
    public void updateItem(ItemEditForm form) {
        Item item = repository.findOne(form.getId());
        item.setName(form.getName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        if(item instanceof Book book) {
            book.setAuthor(form.getAuthor());
            book.setIsbn(form.getIsbn());
        } else if(item instanceof Album album) {
            album.setArtist(form.getArtist());
            album.setEtc(form.getEtc());
        } else if(item instanceof Movie movie) {
            movie.setDirector(form.getDirector());
            movie.setActor(form.getActor());
        }
    }
}
