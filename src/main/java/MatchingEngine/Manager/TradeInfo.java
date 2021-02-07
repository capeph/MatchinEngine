package MatchingEngine.Manager;

public class TradeInfo {
    long quantity;
    long price;
    long oppositeOrder;

    public TradeInfo(long quantity, long price, long oppositeOrder) {
        this.quantity = quantity;
        this.price = price;
        this.oppositeOrder = oppositeOrder;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getPrice() {
        return price;
    }

    public long getOppositeOrder() {
        return oppositeOrder;
    }
}