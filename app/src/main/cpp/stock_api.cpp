#include "stock_api.h"
#include <iostream>

// This is a placeholder - real API fetching will come next
std::vector<StockSymbol> fetchStockSymbols(const std::string& exchange, const std::string& token) {
    std::vector<StockSymbol> stockList;

    // Hardcoded dummy data for now
    stockList.push_back({"AAPL", "Apple Inc."});
    stockList.push_back({"GOOGL", "Alphabet Inc."});
    stockList.push_back({"AMZN", "Amazon.com Inc."});

    std::cout << "Fetched " << stockList.size() << " stock symbols.\n";

    return stockList;
}
