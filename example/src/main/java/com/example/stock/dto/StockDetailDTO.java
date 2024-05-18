package com.example.stock.dto;

import com.example.stock.domain.Stock;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StockDetailDTO {
    private String baseDate;

    private String itemName;

    private int closingPrice;

    private int vs;

    private Double fluctuationRate;

    private int marketPrice;

    private int highestPrice;

    private int lowestPrice;

    public StockDetailDTO(Stock stock) {
        this.baseDate = stock.getStockPK().getBaseDate();
        this.itemName = stock.getItemName();
        this.closingPrice = stock.getClosingPrice();
        this.vs = stock.getVs();
        this.fluctuationRate = stock.getFluctuationRate();
        this.marketPrice = stock.getMarketPrice();
        this.highestPrice = stock.getHighestPrice();
        this.lowestPrice = stock.getLowestPrice();
    }
}
