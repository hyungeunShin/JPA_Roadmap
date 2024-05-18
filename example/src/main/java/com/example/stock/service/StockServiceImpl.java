package com.example.stock.service;

import com.example.stock.dto.StockDetailDTO;
import com.example.stock.dto.StockListDTO;
import com.example.stock.repository.StockRepository;
import com.example.util.PaginationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository repository;

    @Override
    public List<StockListDTO> getStockList(PaginationInfo<StockListDTO> paginationInfo) {
        return repository.findAll(paginationInfo).stream().map(StockListDTO::new).toList();
    }

    @Override
    public int getStockTotalCount(PaginationInfo<StockListDTO> paginationInfo) {
        return repository.findAllTotalCount(paginationInfo).intValue();
    }

    @Override
    public List<StockDetailDTO> getChartData(String isinCode) {
        return repository.findByIsinCode(isinCode).stream().map(StockDetailDTO::new).toList();
    }
}
