package com.example.financeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class BrowseStockAdapter extends RecyclerView.Adapter<BrowseStockAdapter.StockViewHolder> {

    private final List<Stock> stockSymbolList;
    private final OnItemClickListener listener;

    public BrowseStockAdapter(List<Stock> stockSymbolList, OnItemClickListener listener) {
        this.stockSymbolList = stockSymbolList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_stock_item, parent, false); // ✅ use correct layout
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockSymbolList.get(position);
        holder.bind(stock, listener);
    }

    @Override
    public int getItemCount() {
        return stockSymbolList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        private final TextView stockSymbolTextView;
        private final TextView stockNameTextView;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            stockSymbolTextView = itemView.findViewById(R.id.browseStockSymbol); // ✅ match XML ID
            stockNameTextView = itemView.findViewById(R.id.browseStockName);     // ✅ match XML ID
        }

        public void bind(Stock stock, OnItemClickListener listener) {
            stockSymbolTextView.setText(stock.getSymbol());
            stockNameTextView.setText(stock.getDescription());
            itemView.setOnClickListener(v -> listener.onItemClick(stock));
        }
    }
}
