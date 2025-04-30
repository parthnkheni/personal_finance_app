package com.example.financeapp;

public class Stock {
    private String symbol;
    private String name;
    private String type;
    private String description;
    private double price;
    private int quantity;

    public Stock(String symbol, String name, String type, String description, double price, int quantity) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
