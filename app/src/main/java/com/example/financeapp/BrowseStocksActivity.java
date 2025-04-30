package com.example.financeapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.network.FinnhubAPI;
import com.example.financeapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseStocksActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView browseRecyclerView;
    private ProgressBar progressBar;

    private BrowseStockAdapter adapter;
    private List<StockSymbol> allStocks = new ArrayList<>();
    private List<Stock> displayList = new ArrayList<>();

    private Spinner securityTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_stocks);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Browse Stocks");
        }

        // Step 1: Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        browseRecyclerView = findViewById(R.id.browseRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        securityTypeSpinner = findViewById(R.id.securityTypeSpinner);

        // Step 2: Setup adapter and layout
        adapter = new BrowseStockAdapter(displayList, this::saveStockToPortfolio);
        browseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        browseRecyclerView.setAdapter(adapter);

        // Step 3: Add listeners
        setupSearchListener();
        fetchStockSymbolsFromFinnhub(); // Spinner populated inside

        // Step 4: Apply filtering when spinner is used
        securityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterStocks(searchEditText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // goes back to previous activity
        return true;
    }


    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStocks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchStockSymbolsFromFinnhub() {
        progressBar.setVisibility(View.VISIBLE);
        FinnhubAPI api = RetrofitClient.getInstance().create(FinnhubAPI.class);
        api.getAllSymbols("US", "d07hlg1r01qrslhodmj0d07hlg1r01qrslhodmjg").enqueue(new Callback<List<StockSymbol>>() {
            @Override
            public void onResponse(Call<List<StockSymbol>> call, Response<List<StockSymbol>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allStocks = response.body();
                    List<Stock> converted = new ArrayList<>();
                    for (StockSymbol stock : allStocks) {
                        converted.add(new Stock(
                                stock.getSymbol(),
                                stock.getDescription(),   // name
                                stock.getType(),          // type
                                stock.getDescription(),   // description
                                0.0,                      // price placeholder
                                0                         // quantity
                        ));
                    }
                    updateDisplayList(converted);

                    Set<String> types = new HashSet<>();
                    for (StockSymbol stock : allStocks) {
                        if (stock.getType() != null && !stock.getType().isEmpty()) {
                            types.add(stock.getType());
                        }
                    }

                    List<String> sortedTypes = new ArrayList<>(types);
                    Collections.sort(sortedTypes);
                    sortedTypes.add(0, "All");

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            BrowseStocksActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            sortedTypes
                    );
                    securityTypeSpinner.setAdapter(adapter);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<StockSymbol>> call, Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filterStocks(String query) {
        String selectedType = securityTypeSpinner.getSelectedItem().toString();
        List<Stock> filtered = new ArrayList<>();
        for (StockSymbol stock : allStocks) {
            boolean matchesQuery = stock.getSymbol().toLowerCase().contains(query.toLowerCase())
                    || stock.getDescription().toLowerCase().contains(query.toLowerCase());

            boolean matchesType = selectedType.equals("All") ||
                    (stock.getType() != null && stock.getType().equalsIgnoreCase(selectedType));

            if (matchesQuery && matchesType) {
                filtered.add(new Stock(
                        stock.getSymbol(),
                        stock.getDescription(), // name
                        stock.getType(),        // type
                        stock.getDescription(), // description (you may have a better source later)
                        0.0,                    // price placeholder
                        0                       // quantity
                ));

            }
        }

        updateDisplayList(filtered);

    }
    private void updateDisplayList(List<Stock> stocks) {
        displayList.clear();
        displayList.addAll(stocks);
        adapter.notifyDataSetChanged();
    }

    private void saveStockToPortfolio(Stock stock) {
        // TODO: Save to SharedPreferences or database
        System.out.println("Saved: " + stock.getSymbol());
    }
}
