#ifndef STOCK_API_H
#define STOCK_API_H

#include <string>
#include <vector>

struct StockSymbol {
    std::string symbol;
    std::string description;
};

std::vector<StockSymbol> fetchStockSymbols(const std::string& exchange, const std::string& token);

#endif // STOCK_API_H
