package MatchingEngine.OrderBook;

import MatchingEngine.Messaging.Copyable;
import MatchingEngine.Trading.Side;

public class OrderMessage implements Copyable<OrderMessage> {

    public enum Type {EMPTY, NEW_ORDER, CANCEL, TRADE, INFO, LEVEL, BOOK, TRADE_REPORT, KILL};

    protected Type type = Type.EMPTY;
    private long id;
    private long quantity;
    private long price;
    private Side side;
    private long owner;

    public OrderMessage populate(Type type, long orderId, long clientId, Side side, long quantity, long price) {
        this.type = type;
        this.id = orderId;
        this.owner = clientId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        return this;
    }

    @Override
    public void copy(OrderMessage other) {
        type = other.type;
        this.id = other.id;
        this.owner = other.owner;
        this.side = other.side;
        this.quantity = other.quantity;
        this.price = other.price;
    }

    public Type getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public long getOwner() {
        return owner;
    }
}
