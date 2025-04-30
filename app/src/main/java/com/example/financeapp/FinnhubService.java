package com.example.financeapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FinnhubService {

    // Existing quote fetcher (search by symbol)
    @GET("quote")
    Call<StockQuoteResponse> getStockQuote(
            @Query("symbol") String symbol,
            @Query("token") String apiKey
    );
    @GET("stock/candle")
    Call<CandleResponse> getStockCandles(
            @Query("symbol") String symbol,
            @Query("resolution") String resolution,
            @Query("from") long from,
            @Query("to") long to,
            @Query("token") String token
    );


    // NEW: Fetch all US stock symbols
    @GET("stock/symbol")
    Call<List<StockSymbolResponse>> getAllUSStocks(
            @Query("exchange") String exchange,
            @Query("token") String apiKey
    );
}
