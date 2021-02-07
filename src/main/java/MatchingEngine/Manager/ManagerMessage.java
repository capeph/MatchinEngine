package MatchingEngine.Manager;

import MatchingEngine.Messaging.Copyable;
import MatchingEngine.Trading.Side;

public class ManagerMessage implements Copyable<ManagerMessage> {


    public enum Type {EMPTY, NEW_ORDER, CANCEL, TRADE, INFO, LEVEL, BOOK, TRADE_REPORT, KILL};

    protected Type type = Type.EMPTY;
    private long id;
    private long quantity;
    private long price;
    private Side side;
    private long altId;

    public ManagerMessage populate(Type type, long orderId, long clientId, Side side, long quantity, long price) {
        this.type = type;
        this.id = orderId;
        this.altId = clientId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        return this;
    }

    @Override
    public void copy(ManagerMessage other) {
        type = other.type;
        this.id = other.id;
        this.altId = other.altId;
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

    public long getAltId() {
        return altId;
    }
    public long getOwner() {
        return altId;
    }

    public long getOpposite() {
        return altId;
    }



    //
//
//    public OrderMessage buildNewOrder(long orderId, long clientId, Side side, long quantity, long price) {
//        type = Type.NEW_ORDER;
//        fields[ID] = orderId;
//        fields[QUANTITY] = quantity;
//        fields[SIDE] = side.intValue();
//        fields[PRICE] = price;
//        fields[OWNER] = clientId;
//        return this;
//    }
//
//    public OrderMessage buildTrade(long orderId, long opposite, long quantity, long price) {
//        type = Type.TRADE;
//        fields[ID] = orderId;
//        fields[OPPOSITE_ID] = opposite;
//        fields[QUANTITY] = quantity;
//        fields[PRICE] = price;
//        return this;
//    }
//
//    public OrderMessage buildCancel(long orderId, long quantity) {
//        type = Type.CANCEL;
//        fields[ID] = orderId;
//        fields[QUANTITY] = quantity;
//        return this;
//    }
//
//    // anyone can look at all orders...
//    public OrderMessage buildOrderInfo(long clientId, long orderId) {
//        type = Type.INFO;
//        fields[ID] = orderId;
//        fields[OWNER] = clientId;
//        return this;
//    }
//
//
//    public OrderMessage buildTradeReport(long clientId, long orderId) {
//        type = type.TRADE_REPORT;
//        fields[ID] = orderId;
//        fields[OWNER] = clientId;
//        return this;
//    }
//
//
//    public OrderMessage buildOrderBook(long clientId) {
//        type = Type.BOOK;
//        fields[OWNER] = clientId;
//        return this;
//    }
//
//    public OrderMessage buildLevelUpdate(Side side, long price, long volume) {
//        type = Type.LEVEL;
//        fields[PRICE] = price;
//        fields[QUANTITY] = volume;
//        fields[SIDE] = side.intValue();
//        return this;
//    }
//
//    public OrderMessage buildKill() {
//        type = Type.KILL;
//        return this;
//    }
//
//    public long getId() {
//        return fields[ID];
//    }
//
//    public Side getSide() {
//        assert(Type.NEW_ORDER == type);
//        return Side.fromInt((int) fields[SIDE]);
//    }
//
//    public long getOwner() {
//        assert(Type.NEW_ORDER == type);
//        return fields[OWNER];
//    }
//
//    public long getQuantity() {
//        return fields[QUANTITY];
//    }
//
//    public long getPrice() {
//        return fields[PRICE];
//    }
//
//
//    public long getOpposite() {
//        assert(type == Type.TRADE);
//        return fields[OPPOSITE_ID];
//    }

}
