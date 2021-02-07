package MatchingEngine.OrderBook;

import MatchingEngine.Messaging.MailBox;
import MatchingEngine.OrderBook.OrderMessage.Type;
import MatchingEngine.Trading.Side;

import java.util.function.Supplier;

import static MatchingEngine.OrderBook.OrderMessage.Type.KILL;
import static MatchingEngine.OrderBook.OrderMessage.Type.NEW_ORDER;

public class BookMailBox extends MailBox<OrderMessage> {

    private OrderMessage template = new OrderMessage();

    public BookMailBox(int size) {
        super(size, OrderMessage::new);
    }

    public void sendNewOrder(long orderId, long clientId, Side side, long quantity, long price) {
        template.populate(NEW_ORDER, orderId, clientId, side, quantity, price);
        putCopy(template);
    }

    public void sendKill() {
        template.populate(KILL, 0,0,Side.BUY, 0, 0);  // why BUY?
    }
}
