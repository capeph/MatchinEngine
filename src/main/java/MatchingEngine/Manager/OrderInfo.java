package MatchingEngine.Manager;

import MatchingEngine.Trading.Order;
import MatchingEngine.Trading.Side;

import java.util.ArrayList;
import java.util.List;

public class OrderInfo extends Order {

    private final List<TradeInfo> trades = new ArrayList<>();

    public OrderInfo(long id, Side side, long quantity, long price, long ownerid) {
        super(id, side, quantity, price, ownerid);
    }

    void addTrade(long quantity, long price, long oppositeOrder) {
        trades.add(new TradeInfo(quantity, price, oppositeOrder));
    }

    public List<TradeInfo> getTrades() {
        return trades;
    }

}
