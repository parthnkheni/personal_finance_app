package com.example.financeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private final List<Stock> portfolio;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PortfolioAdapter(List<Stock> portfolio, OnItemClickListener listener) {
        this.portfolio = portfolio;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolText, quantityText, priceText, totalValueText;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            symbolText = itemView.findViewById(R.id.portfolioSymbol);
            quantityText = itemView.findViewById(R.id.portfolioQuantity);
            priceText = itemView.findViewById(R.id.portfolioPrice);
            totalValueText = itemView.findViewById(R.id.portfolioTotalValue);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public PortfolioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.portfolio_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioAdapter.ViewHolder holder, int position) {
        Stock stock = portfolio.get(position);

        holder.symbolText.setText(stock.getSymbol());
        holder.quantityText.setText("Qty: " + stock.getQuantity());

        // Fetch live price
        FinnhubService service = ApiClient.getRetrofitInstance().create(FinnhubService.class);
        String apiKey = BuildConfig.FINNHUB_API_KEY;

        service.getStockQuote(stock.getSymbol(), apiKey).enqueue(new Callback<StockQuoteResponse>() {
            @Override
            public void onResponse(Call<StockQuoteResponse> call, Response<StockQuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double currentPrice = response.body().getCurrentPrice();
                    stock.setPrice(currentPrice);
                    holder.priceText.setText("Price: $" + String.format("%.2f", currentPrice));
                    double totalValue = currentPrice * stock.getQuantity();
                    holder.totalValueText.setText("Total: $" + String.format("%.2f", totalValue));
                } else {
                    holder.priceText.setText("Price: N/A");
                    holder.totalValueText.setText("Total: N/A");
                }
            }

            @Override
            public void onFailure(Call<StockQuoteResponse> call, Throwable t) {
                holder.priceText.setText("Price: Error");
                holder.totalValueText.setText("Total: Error");
            }
        });
    }

    @Override
    public int getItemCount() {
        return portfolio.size();
    }
}
