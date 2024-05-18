package com.example.stock.domain;

public enum MarketCategory {
    KOSPI("코스피"), KOSDAQ("코스닥"), KONEX("코넥스");

    private final String description;

    MarketCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
