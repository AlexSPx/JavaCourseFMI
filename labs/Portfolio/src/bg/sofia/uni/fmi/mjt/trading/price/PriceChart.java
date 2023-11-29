package bg.sofia.uni.fmi.mjt.trading.price;

import java.util.Arrays;

public class PriceChart implements PriceChartAPI {
    private final String[] stockTickers = {"MSFT","GOOG","AMZ"};
    private final double[] stockPrices;

    private double roundDouble(double value) {
        return (double) Math.round(value*100)/100;
    }

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice) {
        stockPrices = new double[]{roundDouble(microsoftStockPrice), roundDouble(googleStockPrice), roundDouble(amazonStockPrice)};
    }

    private int getIndex(String stockTicker){
        if(stockTicker == null) return -1;

        int idx = -1;
        int len = stockTickers.length;
        for (int i = 0; i < len; i++) {
            if(stockTickers[i].equals(stockTicker)) {
                idx = i;
                break;
            }
        }

        return idx;
    }

    @Override
    public double getCurrentPrice(String stockTicker) {
        int idx = getIndex(stockTicker);

        if(idx == -1) return 0;

        return stockPrices[idx];
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if(percentChange <= 0) return false;

        int idx = getIndex(stockTicker);
        if(idx == -1) return false;

        stockPrices[idx] += (double) Math.round((stockPrices[idx]*percentChange))/100;
        return true;
    }
}
