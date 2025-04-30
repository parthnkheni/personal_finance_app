package com.example.financeapp.network;

import java.util.List;
import com.example.financeapp.StockSymbol;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FinnhubAPI {
    @GET("stock/symbol")
    Call<List<StockSymbol>> getAllSymbols(
            @Query("exchange") String exchange,
            @Query("token") String apiKey
    );
}
