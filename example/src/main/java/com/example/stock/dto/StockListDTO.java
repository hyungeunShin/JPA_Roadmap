package com.example.stock.dto;

import com.example.stock.domain.Stock;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StockListDTO {
    private String isinCode;

    private String itemName;

    private int closingPrice;

    private int vs;

    private Double fluctuationRate;

    private Long tradingQuantity;

    public StockListDTO(Stock stock) {
        this.isinCode = stock.getStockPK().getIsinCode();
        this.itemName = stock.getItemName();
        this.closingPrice = stock.getClosingPrice();
        this.vs = stock.getVs();
        this.fluctuationRate = stock.getFluctuationRate();
        this.tradingQuantity = stock.getTradingQuantity();
    }
}
