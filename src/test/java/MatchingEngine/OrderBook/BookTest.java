package MatchingEngine.OrderBook;

import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Messaging.MailBoxImpl;
import MatchingEngine.Messaging.OrderMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    public void testAddOrder() {
        OrderMessage template = new OrderMessage();
        MailBox<OrderMessage> mgr = new MailBoxImpl<>(100, OrderMessage::getEmpty);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().putCopy(template.buildNewOrder(1,
                42, Side.BUY,  100, 12));
        OrderMessage out = mgr.get();
        assertEquals(OrderMessage.Type.NEW_ORDER, out.getType());
        assertEquals(1, out.getId());
        assertEquals(12, out.getPrice());
        book.getMailBox().putCopy(template.buildKill());
    }

    @Test
    public void testTradeLimitOrders() {
        OrderMessage template = new OrderMessage();
        MailBox<OrderMessage> mgr = new MailBoxImpl<>(100, OrderMessage::getEmpty);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().putCopy(template.buildNewOrder(1,
                42, Side.BUY,  100, 12));
        get(OrderMessage.Type.NEW_ORDER, mgr);

        book.getMailBox().putCopy(template.buildNewOrder(2,
                42, Side.SELL,  50, 12));
        get(OrderMessage.Type.LEVEL, mgr);
        get(OrderMessage.Type.NEW_ORDER, mgr);
        OrderMessage out = get(OrderMessage.Type.TRADE, mgr);
        assertEquals(50, out.getQuantity());
        assertEquals(2, out.getId());
        assertEquals(1, out.getOpposite());
        get(OrderMessage.Type.LEVEL, mgr);
        assertFalse(mgr.hasMessage());
        book.getMailBox().putCopy(template.buildKill());
    }

    private OrderMessage get(OrderMessage.Type type, MailBox<OrderMessage> mbox) {
        OrderMessage msg = mbox.get();
        assertEquals(type, msg.getType());
        return msg;
    }

    @Test
    public void testTradeMarketOrder() throws InterruptedException {
        OrderMessage template = new OrderMessage();
        MailBox<OrderMessage> mgr = new MailBoxImpl<>(100, OrderMessage::getEmpty);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().putCopy(template.buildNewOrder(1,
                42, Side.BUY,  50, 12));
        OrderMessage out = mgr.get();
        assertEquals(OrderMessage.Type.NEW_ORDER, out.getType());
        book.getMailBox().putCopy(template.buildNewOrder(2,
                42, Side.SELL,  100, 0));
        get(OrderMessage.Type.LEVEL, mgr);
        get(OrderMessage.Type.NEW_ORDER, mgr);
        out = get(OrderMessage.Type.TRADE, mgr);
        assertEquals(50, out.getQuantity());
        assertEquals(2, out.getId());
        assertEquals(1, out.getOpposite());
        get(OrderMessage.Type.LEVEL, mgr);
        out = get(OrderMessage.Type.CANCEL, mgr);
        assertEquals(50, out.getQuantity());
        assertFalse(mgr.hasMessage());
        book.getMailBox().putCopy(template.buildKill());
    }

}