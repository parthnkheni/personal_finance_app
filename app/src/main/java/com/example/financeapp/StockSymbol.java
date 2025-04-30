package com.example.financeapp;

public class StockSymbol {
    private String symbol;
    private String description;
    private String type;

    public StockSymbol(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }
    public String getType() {
        return type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
