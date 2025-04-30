package com.example.financeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private List<Stock> stockList;

    public StockAdapter(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_item, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.bind(stock);
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public void updateList(List<Stock> newStockList) {
        stockList = newStockList;
        notifyDataSetChanged();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        private final TextView stockSymbolTextView;
        private final TextView stockNameTextView;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            stockSymbolTextView = itemView.findViewById(R.id.stockSymbolTextView);
            stockNameTextView = itemView.findViewById(R.id.stockNameTextView);
        }

        public void bind(Stock stock) {
            stockSymbolTextView.setText(stock.getSymbol());
            stockNameTextView.setText(stock.getName());
        }
    }
}
