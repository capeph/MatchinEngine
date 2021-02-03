package MatchingEngine.Messaging;

import MatchingEngine.Manager.OrderInfo;
import MatchingEngine.OrderBook.Order;

public class ResponseMessage implements Copyable<ResponseMessage> {


    public enum Type {EMPTY, ORDER_ACK, TRADE, KILL, CANCEL, ORDER_INFO, BOOK, TRADE_REPORT, ERROR};

    protected Type type = Type.EMPTY;
    protected long owner = 0;
    protected String contents = "";

    public Type getType() {
        return type;
    }

    public long getOwner() {
        return owner;
    }

    public String getContents() {
        return contents;
    }

    public static ResponseMessage getEmpty() {
        return new ResponseMessage();
    }

    @Override
    public void copy(ResponseMessage other) {
        type = other.type;
        owner = other.owner;
        contents = other.contents;
    }

    public ResponseMessage buildTrade(Long orderId, long quantity, long price, long owner) {
        type = Type.TRADE;
        this.owner = owner;
        StringBuilder sb = new StringBuilder();
        sb.append(orderId);
        sb.append(":");
        sb.append(quantity);
        sb.append("@");
        sb.append(price);
        contents = sb.toString();
        return this;
    }

    public ResponseMessage buildOrderAck(Order order) {
        type = Type.ORDER_ACK;
        owner = order.getOwnerid();
        StringBuilder sb = new StringBuilder();
        sb.append(order.getId());
        sb.append(":");
        sb.append(order.getSide().toString());
        sb.append(order.getQuantity());
        sb.append("@");
        sb.append(order.getPrice());
        contents = sb.toString();
        return this;
    }


    public ResponseMessage buildOrderInfo(OrderInfo order, long owner) {
        type = Type.ORDER_INFO;
        owner = owner;
        StringBuilder sb = new StringBuilder();
        sb.append(order.getId());
        sb.append(":");
        sb.append(order.getSide().toString());
        sb.append(order.getQuantity());
        sb.append("@");
        sb.append(order.getPrice());
        sb.append("by");
        sb.append(order.getOwnerid());
        contents = sb.toString();
        return this;
    }

    public ResponseMessage buildTradeReport(String report, long ownerId) {
        type = Type.TRADE_REPORT;
        owner = ownerId;
        contents = report;
        return this;
    }

    public ResponseMessage buildCancel(long orderId, long quantity, long ownerId) {
        type = Type.CANCEL;
        owner = ownerId;
        StringBuilder sb = new StringBuilder();
        sb.append(orderId);
        sb.append(":");
        sb.append(quantity);
        contents = sb.toString();
        return this;
    }

    public ResponseMessage buildBook(long owner, String dump) {
        type = Type.BOOK;
        this.owner = owner;
        contents = dump;
        return this;
    }

    public ResponseMessage buildError(long owner, String error) {
        type = Type.ERROR;
        this.owner = owner;
        contents = error;
        return this;
    }

    public ResponseMessage buildKill() {
        type = Type.KILL;
        contents = ("Ordered Shutdown");
        owner = 0;
        return this;
    }
}
