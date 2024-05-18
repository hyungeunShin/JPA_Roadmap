package com.example.stock.domain;

import com.example.stock.dto.APIResponseDTO;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @EmbeddedId
    private StockPK stockPK;

    private String shortCode;

    private String itemName;

    @Enumerated(EnumType.STRING)
    private MarketCategory marketCategory;

    private int closingPrice;

    private int vs;

    private Double fluctuationRate;

    private int marketPrice;

    private int highestPrice;

    private int lowestPrice;

    private Long tradingQuantity;

    private Long tradingPrice;

    private Long listingStockCount;

    private Long marketTotalAmount;

    @Builder
    public Stock(StockPK stockPK, String shortCode, String itemName, MarketCategory marketCategory, int closingPrice, int vs, Double fluctuationRate, int marketPrice, int highestPrice, int lowestPrice, Long tradingQuantity, Long tradingPrice, Long listingStockCount, Long marketTotalAmount) {
        this.stockPK = stockPK;
        this.shortCode = shortCode;
        this.itemName = itemName;
        this.marketCategory = marketCategory;
        this.closingPrice = closingPrice;
        this.vs = vs;
        this.fluctuationRate = fluctuationRate;
        this.marketPrice = marketPrice;
        this.highestPrice = highestPrice;
        this.lowestPrice = lowestPrice;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.listingStockCount = listingStockCount;
        this.marketTotalAmount = marketTotalAmount;
    }

    public static Stock fromItem(APIResponseDTO.Item item) {
        return Stock.builder()
                    .stockPK(new StockPK(item.getIsinCd(), item.getBasDt()))
                    .shortCode(item.getSrtnCd())
                    .itemName(item.getItmsNm())
                    .marketCategory(MarketCategory.valueOf(item.getMrktCtg()))
                    .closingPrice(item.getClpr())
                    .vs(item.getVs())
                    .fluctuationRate(item.getFltRt())
                    .marketPrice(item.getMkp())
                    .highestPrice(item.getHipr())
                    .lowestPrice(item.getLopr())
                    .tradingQuantity(item.getTrqu())
                    .tradingPrice(item.getTrPrc())
                    .listingStockCount(item.getLstgStCnt())
                    .marketTotalAmount(item.getMrktTotAmt())
                    .build();
    }

    public static List<Stock> fromItems(List<APIResponseDTO.Item> items) {
        return items.stream().map(Stock::fromItem).toList();
    }
}
