package com.example.stock.service;

import com.example.stock.dto.StockDetailDTO;
import com.example.stock.dto.StockListDTO;
import com.example.util.PaginationInfo;

import java.util.List;

public interface StockService {
    List<StockListDTO> getStockList(PaginationInfo<StockListDTO> paginationInfo);

    int getStockTotalCount(PaginationInfo<StockListDTO> paginationInfo);

    List<StockDetailDTO> getChartData(String isinCode);
}
