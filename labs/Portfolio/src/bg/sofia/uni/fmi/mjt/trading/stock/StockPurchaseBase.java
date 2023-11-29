package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class StockPurchaseBase implements StockPurchase {
    private final String ticker;
    private final int quantity;
    private final double purchasePricePerUnit;
    private final LocalDateTime purchaseTimestamp;

    public StockPurchaseBase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit, String ticker){
        this.ticker = ticker;
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        this.purchasePricePerUnit = purchasePricePerUnit;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return purchasePricePerUnit;
    }

    @Override
    public double getTotalPurchasePrice() {
        return purchasePricePerUnit * quantity;
    }

    @Override
    public String getStockTicker() {
        return ticker;
    }
}
