package com.example.financeapp;

import com.google.gson.annotations.SerializedName;

public class StockQuoteResponse {

    @SerializedName("c")
    private double currentPrice;

    @SerializedName("h")
    private double high;

    @SerializedName("l")
    private double low;

    @SerializedName("o")
    private double open;

    @SerializedName("pc")
    private double previousClose;

    // Getters
    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getOpen() {
        return open;
    }

    public double getPreviousClose() {
        return previousClose;
    }
}
