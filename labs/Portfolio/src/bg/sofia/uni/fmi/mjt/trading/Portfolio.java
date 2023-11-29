package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.time.LocalDateTime;
import java.util.Arrays;

public class Portfolio implements PortfolioAPI{
    private final String owner;
    private double budget;
    private final int maxSize;
    private int currentSize;
    private final PriceChartAPI priceChart;
    private final StockPurchase[] stockPurchases;

    public Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize) {
        this.owner = owner;
        this.budget = budget;
        this.priceChart = priceChart;
        this.maxSize = maxSize;
        this.currentSize = 0;
        this.stockPurchases = new StockPurchase[maxSize];
    }

    public Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize) {
        this.owner = owner;
        this.budget = budget;
        this.priceChart = priceChart;
        this.maxSize = maxSize;
        this.currentSize = stockPurchases.length;
        this.stockPurchases = Arrays.copyOf(stockPurchases, maxSize);
    }

    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if(stockTicker == null || quantity <= 0 || stockPurchases.length == currentSize) return null;

        double currentPrice = priceChart.getCurrentPrice(stockTicker);

        if(currentPrice <= 0.0) return null;

        double updatedBudget = budget - (currentPrice * quantity);

        if(updatedBudget < 0) return null;

        this.budget = updatedBudget;
        priceChart.changeStockPrice(stockTicker, 5);

        stockPurchases[currentSize] = switch (stockTicker) {
            case "GOOG" -> new GoogleStockPurchase(quantity, LocalDateTime.now(), currentPrice);
            case "AMZ" -> new AmazonStockPurchase(quantity, LocalDateTime.now(), currentPrice);
            case "MSFT" -> new MicrosoftStockPurchase(quantity, LocalDateTime.now(), currentPrice);
            default -> null;
        };

        return stockPurchases[currentSize++];
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        return Arrays.copyOf(stockPurchases, currentSize);
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        int size = 0;

        for (int i = 0; i < currentSize; i++) {
            LocalDateTime timestamp = stockPurchases[i].getPurchaseTimestamp();
            if (timestamp.isBefore(endTimestamp) && timestamp.isAfter(startTimestamp)) {
                size++;
            }
        }

        StockPurchase[] stocks = new StockPurchase[size];

        if(size == 0) return stocks;

        int current = 0;
        for (int i = 0; i < currentSize; i++) {
            LocalDateTime timestamp = stockPurchases[i].getPurchaseTimestamp();
            if (timestamp.isBefore(endTimestamp) && timestamp.isAfter(startTimestamp)) {
                stocks[current++] = stockPurchases[i];
            }
        }

        return stocks;
    }

    @Override
    public double getNetWorth() {
        double netWorth = 0;

        double currentGooglePrice = priceChart.getCurrentPrice("GOOG");
        double currentAmazonPrice = priceChart.getCurrentPrice("AMZ");
        double currentMicrosoftPrice = priceChart.getCurrentPrice("MSFT");


        for (int i = 0; i < currentSize; i++) {
            netWorth += switch (stockPurchases[i].getStockTicker()) {
                case "GOOG" -> stockPurchases[i].getQuantity() * currentGooglePrice;
                case "AMZ" -> stockPurchases[i].getQuantity() * currentAmazonPrice;
                case "MSFT" -> stockPurchases[i].getQuantity() * currentMicrosoftPrice;
                default -> 0;
            };
        }

        return netWorth;
    }

    @Override
    public double getRemainingBudget() {
        return  (double) Math.round(this.budget*100)/100;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }
}