package com.example.config.batch;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class StockItemWriter implements ItemWriter<List<Stock>> {
    private final StockJpaRepository repository;

    @Override
    public void write(Chunk<? extends List<Stock>> chunks) throws Exception {
        List<? extends List<Stock>> items = chunks.getItems();
        List<Stock> list = items.stream().flatMap(Collection::stream).toList();

        repository.saveAll(list);

        log.info("StockItemWriter Insert 리스트 : {}", list.size());
    }
}
