package com.example.financeapp;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PortfolioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PortfolioAdapter portfolioAdapter;
    private List<Stock> portfolio = new ArrayList<>();
    private TextView totalPortfolioValue;

    private Stock recentlyDeletedStock;
    private int recentlyDeletedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Portfolio");
        }

        recyclerView = findViewById(R.id.recyclerView);
        totalPortfolioValue = findViewById(R.id.totalPortfolioValue);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPortfolio();

        portfolioAdapter = new PortfolioAdapter(portfolio, position -> showQuantityDialog(position));
        recyclerView.setAdapter(portfolioAdapter);

        setupSwipeToDelete();
        updateTotalPortfolioValue();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void showQuantityDialog(int position) {
        Stock stock = portfolio.get(position);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter quantity");
        input.setText(String.valueOf(stock.getQuantity()));

        new AlertDialog.Builder(this)
                .setTitle("Update Quantity for " + stock.getSymbol())
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String entered = input.getText().toString().trim();
                    if (!entered.isEmpty()) {
                        int newQuantity = Integer.parseInt(entered);
                        stock.setQuantity(newQuantity);
                        portfolioAdapter.notifyItemChanged(position);
                        savePortfolio();
                        updateTotalPortfolioValue();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                recentlyDeletedPosition = viewHolder.getAdapterPosition();
                recentlyDeletedStock = portfolio.get(recentlyDeletedPosition);

                portfolio.remove(recentlyDeletedPosition);
                portfolioAdapter.notifyItemRemoved(recentlyDeletedPosition);
                savePortfolio();
                updateTotalPortfolioValue();

                showUndoSnackbar();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (dX < 0) { // Only draw on left swipe
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    RectF background = new RectF(
                            viewHolder.itemView.getRight() + dX,
                            viewHolder.itemView.getTop(),
                            viewHolder.itemView.getRight(),
                            viewHolder.itemView.getBottom()
                    );
                    c.drawRect(background, paint);

                    Drawable icon = ContextCompat.getDrawable(PortfolioActivity.this, R.drawable.ic_delete);
                    if (icon != null) {
                        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconTop = viewHolder.itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconRight = viewHolder.itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - icon.getIntrinsicWidth();

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showUndoSnackbar() {
        Snackbar.make(recyclerView, "Stock removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    portfolio.add(recentlyDeletedPosition, recentlyDeletedStock);
                    portfolioAdapter.notifyItemInserted(recentlyDeletedPosition);
                    savePortfolio();
                    updateTotalPortfolioValue();
                })
                .show();
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

    private void updateTotalPortfolioValue() {
        double totalValue = 0;
        for (Stock stock : portfolio) {
            totalValue += stock.getPrice() * stock.getQuantity();
        }
        totalPortfolioValue.setText("Total Portfolio Value: $" + String.format("%.2f", totalValue));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotalPortfolioValue();
    }
}
