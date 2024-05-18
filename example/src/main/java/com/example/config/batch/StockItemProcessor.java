package com.example.config.batch;

import com.example.stock.domain.Stock;
import com.example.stock.dto.APIResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

@Slf4j
public class StockItemProcessor implements ItemProcessor<APIResponseDTO, List<Stock>> {
    @Override
    public List<Stock> process(APIResponseDTO item) throws Exception {
        log.info("ApiItemProcessor.process 시작 : {}", item.getResponse().getBody().getItems().getItem().size());

        List<Stock> list = Stock.fromItems(item.getResponse().getBody().getItems().getItem());

        log.info("ApiItemProcessor.process 끝 : {}", list.size());

        return list;
    }
}
