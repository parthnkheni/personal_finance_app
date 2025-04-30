package com.example.financeapp;

import java.util.List;

public class CandleResponse {
    private List<Float> c; // close prices
    private List<Long> t; // timestamps
    private String s; // status ("ok" or "no_data")

    public List<Float> getClosePrices() {
        return c;
    }

    public List<Long> getTimestamps() {
        return t;
    }

    public String getStatus() {
        return s;
    }
}
