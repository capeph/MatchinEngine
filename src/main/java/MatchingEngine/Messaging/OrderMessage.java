package MatchingEngine.Messaging;

import MatchingEngine.OrderBook.Side;

public class OrderMessage implements Copyable<OrderMessage> {

    public enum Type {EMPTY, NEW_ORDER, CANCEL, TRADE, INFO, LEVEL, BOOK, KILL};

    protected Type type = Type.EMPTY;
    protected long[] fields = new long[5];

    // idexes for fields
    private static final int ID = 0;
    private static final int QUANTITY = 1;
    private static final int PRICE = 2;
    private static final int SIDE = 3;
    private static final int OWNER = 4;
    private static final int OPPOSITE_ID = 3;



    public Type getType() {
        return type;
    }

    public static OrderMessage getEmpty() {
        return new OrderMessage();
    }

    @Override
    public void copy(OrderMessage other) {
        type = other.type;
        System.arraycopy(other.fields, 0, fields, 0, fields.length);
    }

    public OrderMessage buildNewOrder(long orderId, long clientId, Side side, long quantity, long price) {
        type = Type.NEW_ORDER;
        fields[ID] = orderId;
        fields[QUANTITY] = quantity;
        fields[SIDE] = side.intValue();
        fields[PRICE] = price;
        fields[OWNER] = clientId;
        return this;
    }

    public OrderMessage buildTrade(long orderId, long opposite, long quantity, long price) {
        type = Type.TRADE;
        fields[ID] = orderId;
        fields[OPPOSITE_ID] = opposite;
        fields[QUANTITY] = quantity;
        fields[PRICE] = price;
        return this;
    }

    public OrderMessage buildCancel(long orderId, long quantity) {
        type = Type.CANCEL;
        fields[ID] = orderId;
        fields[QUANTITY] = quantity;
        return this;
    }

    // anyone can look at all orders...
    public OrderMessage buildOrderInfo(long clientId, long orderId) {
        type = Type.INFO;
        fields[ID] = orderId;
        fields[OWNER] = clientId;
        return this;
    }

    public OrderMessage buildOrderBook(long clientId) {
        type = Type.BOOK;
        fields[OWNER] = clientId;
        return this;
    }

    public OrderMessage buildLevelUpdate(Side side, long price, long volume) {
        type = Type.LEVEL;
        fields[PRICE] = price;
        fields[QUANTITY] = volume;
        fields[SIDE] = side.intValue();
        return this;
    }

    public OrderMessage buildKill() {
        type = Type.KILL;
        return this;
    }

    public long getId() {
        return fields[ID];
    }

    public Side getSide() {
        assert(Type.NEW_ORDER == type);
        return Side.fromInt((int) fields[SIDE]);
    }

    public long getOwner() {
        assert(Type.NEW_ORDER == type);
        return fields[OWNER];
    }

    public long getQuantity() {
        return fields[QUANTITY];
    }

    public long getPrice() {
        return fields[PRICE];
    }


    public long getOpposite() {
        assert(type == Type.TRADE);
        return fields[OPPOSITE_ID];
    }



}
