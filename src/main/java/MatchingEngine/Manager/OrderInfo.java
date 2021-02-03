package MatchingEngine.Manager;

import MatchingEngine.OrderBook.Order;
import MatchingEngine.OrderBook.Side;

import java.util.ArrayList;
import java.util.List;

public class OrderInfo extends Order {

    public class Trade {
        long quantity;
        long price;
        long oppositeOrder;

        public Trade(long quantity, long price, long oppositeOrder) {
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

    private List<Trade> trades = new ArrayList<Trade>();

    public OrderInfo(long id, Side side, long quantity, long price, long ownerid) {
        super(id, side, quantity, price, ownerid);
    }

    void addTrade(long quantity, long price, long oppositeOrder) {
        trades.add(new Trade(quantity, price, oppositeOrder));
    }

    public List<Trade> getTrades() {
        return trades;
    }

}
