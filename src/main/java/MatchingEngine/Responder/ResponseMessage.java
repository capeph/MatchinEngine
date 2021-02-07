package MatchingEngine.Responder;

import MatchingEngine.Manager.OrderInfo;
import MatchingEngine.Messaging.Copyable;
import MatchingEngine.Trading.Order;

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

    public void populate(Type type, long owner, String message) {
        this.type = type;
        this.owner = owner;
        this.contents = message;
    }
}
