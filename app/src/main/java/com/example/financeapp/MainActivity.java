package com.example.financeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BROWSE = 100;

    private EditText searchInput;
    private Button searchButton, saveButton, viewPortfolioButton, browseStocksButton;
    private List<Stock> portfolio = new ArrayList<>();
    private Stock latestSearchedStock = null;

    // New UI elements
    private LineChart lineChart;
    private TextView stockSymbol, stockName, currentPrice;
    private TextView openPrice, highPrice, lowPrice, previousClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        saveButton = findViewById(R.id.saveButton);
        viewPortfolioButton = findViewById(R.id.viewPortfolioButton);
        browseStocksButton = findViewById(R.id.browseStocksButton);

        lineChart = findViewById(R.id.lineChart);
        stockSymbol = findViewById(R.id.stockSymbol);
        stockName = findViewById(R.id.stockName);
        currentPrice = findViewById(R.id.currentPrice);
        openPrice = findViewById(R.id.openPrice);
        highPrice = findViewById(R.id.highPrice);
        lowPrice = findViewById(R.id.lowPrice);
        previousClose = findViewById(R.id.previousClose);

        loadPortfolio();

        searchButton.setOnClickListener(v -> {
            String symbol = searchInput.getText().toString().toUpperCase().trim();
            if (!symbol.isEmpty()) {
                fetchStockQuote(symbol);
            }
        });

        saveButton.setOnClickListener(v -> promptUserForQuantityAndSave());

        viewPortfolioButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
            startActivity(intent);
        });

        browseStocksButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BrowseStocksActivity.class);
            startActivity(intent);
        });

        fetchStockQuote("AAPL"); // Default stock shown at startup
    }

    private void fetchStockQuote(String symbol) {
        FinnhubService service = ApiClient.getRetrofitInstance().create(FinnhubService.class);
        String apiKey = BuildConfig.FINNHUB_API_KEY;

        service.getStockQuote(symbol, apiKey).enqueue(new Callback<StockQuoteResponse>() {
            @Override
            public void onResponse(Call<StockQuoteResponse> call, Response<StockQuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockQuoteResponse stockData = response.body();

                    double current = stockData.getCurrentPrice();
                    double open = stockData.getOpen();
                    double high = stockData.getHigh();
                    double low = stockData.getLow();
                    double previous = stockData.getPreviousClose();

                    stockSymbol.setText(symbol);
                    stockName.setText(symbol); // Optional: Pull real name if you want later
                    currentPrice.setText("Current Price: $" + String.format("%.2f", current));
                    if (current > previous) {
                        currentPrice.setTextColor(getResources().getColor(android.R.color.holo_green_dark)); // stock is UP
                    } else if (current < previous) {
                        currentPrice.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // stock is DOWN
                    } else {
                        currentPrice.setTextColor(getResources().getColor(android.R.color.black)); // No change
                    }


                    openPrice.setText("Open: $" + String.format("%.2f", open));
                    highPrice.setText("High: $" + String.format("%.2f", high));
                    lowPrice.setText("Low: $" + String.format("%.2f", low));
                    previousClose.setText("Previous Close: $" + String.format("%.2f", previous));

                    fetchAndDrawRealChart(symbol); // Draw real 1-day chart!

                    // Save latest stock
                    latestSearchedStock = new Stock(symbol, "", "", "", 0.0, 0); // Pass default price and quantity
                }
            }

            @Override
            public void onFailure(Call<StockQuoteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fetchAndDrawRealChart(String symbol) {
        FinnhubService service = ApiClient.getRetrofitInstance().create(FinnhubService.class);
        String apiKey = BuildConfig.FINNHUB_API_KEY;

        long currentTime = System.currentTimeMillis() / 1000;
        long oneDayAgo = currentTime - (24 * 60 * 60);

        service.getStockCandles(symbol, "5", oneDayAgo, currentTime, apiKey).enqueue(new Callback<CandleResponse>() {
            @Override
            public void onResponse(Call<CandleResponse> call, Response<CandleResponse> response) {
                if (response.isSuccessful() && response.body() != null && "ok".equals(response.body().getStatus()) && response.body().getClosePrices() != null) {
                    List<Float> closes = response.body().getClosePrices();
                    if (closes.isEmpty()) {
                        showNoChartData();
                        return;
                    }

                    List<Entry> entries = new ArrayList<>();
                    for (int i = 0; i < closes.size(); i++) {
                        entries.add(new Entry(i, closes.get(i)));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "1-Day Close Prices");
                    dataSet.setColor(getResources().getColor(R.color.purple_500));
                    dataSet.setValueTextColor(getResources().getColor(R.color.black));
                    dataSet.setLineWidth(2f);
                    dataSet.setDrawCircles(false);
                    dataSet.setDrawFilled(true);

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);

                    Description desc = new Description();
                    desc.setText("Today's Intraday Chart");
                    desc.setTextSize(12f);
                    lineChart.setDescription(desc);

                    lineChart.invalidate();
                } else {
                    showNoChartData();
                }
            }

            @Override
            public void onFailure(Call<CandleResponse> call, Throwable t) {
                showNoChartData();
                t.printStackTrace();
            }
        });
    }

    private void showNoChartData() {
        lineChart.clear();
        Description description = new Description();
        description.setText("No Chart Data Available");
        description.setTextSize(14f);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }

    private void promptUserForQuantityAndSave() {
        if (latestSearchedStock == null) {
            Toast.makeText(this, "Search and select a stock first!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter quantity of shares");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantityStr = input.getText().toString().trim();
                if (!quantityStr.isEmpty()) {
                    int quantity = Integer.parseInt(quantityStr);

                    latestSearchedStock.setQuantity(quantity);
                    portfolio.add(latestSearchedStock);

                    savePortfolio();
                    Toast.makeText(MainActivity.this, "Saved to Portfolio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void savePortfolio() {
        SharedPreferences prefs = getSharedPreferences("portfolio_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(portfolio);

        editor.putString("portfolio_list", json);
        editor.apply();
    }

    private void loadPortfolio() {
        SharedPreferences prefs = getSharedPreferences("portfolio_prefs", MODE_PRIVATE);
        String json = prefs.getString("portfolio_list", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Stock>>() {}.getType();
            portfolio = new Gson().fromJson(json, type);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_BROWSE && resultCode == RESULT_OK && data != null) {
            String selectedSymbol = data.getStringExtra("selected_symbol");
            if (selectedSymbol != null) {
                searchInput.setText(selectedSymbol);
            }
        }
    }
}
