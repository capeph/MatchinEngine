package MatchingEngine.OrderBook;

import MatchingEngine.Manager.ManagerMailBox;
import MatchingEngine.Manager.ManagerMessage;
import MatchingEngine.Trading.Side;
import org.junit.jupiter.api.Test;

import static MatchingEngine.Manager.ManagerMessage.Type.*;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    public void testAddOrder() {
        OrderMessage template = new OrderMessage();
        ManagerMailBox mgr = new ManagerMailBox(100);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().sendNewOrder(1,42, Side.BUY,  100, 12);
        ManagerMessage out = mgr.get();
        assertEquals(NEW_ORDER, out.getType());
        assertEquals(1, out.getId());
        assertEquals(12, out.getPrice());
        book.getMailBox().sendKill();
    }

    @Test
    public void testTradeLimitOrders() {
        OrderMessage template = new OrderMessage();
        ManagerMailBox mgr = new ManagerMailBox(100);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().sendNewOrder(1,42, Side.BUY,  100, 12);
        get(NEW_ORDER, mgr);

        book.getMailBox().sendNewOrder(2,42, Side.SELL,  50, 12);
        get(LEVEL, mgr);
        get(NEW_ORDER, mgr);
        ManagerMessage out = get(TRADE, mgr);
        assertEquals(50, out.getQuantity());
        assertEquals(2, out.getId());
        assertEquals(1, out.getOpposite());
        get(LEVEL, mgr);
        assertFalse(mgr.hasMessage());
        book.getMailBox().sendKill();
    }

    @Test
    public void testTradeDepth() {
        ManagerMailBox mgr = new ManagerMailBox(100);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().sendNewOrder(1,42, Side.BUY,  50, 12);
        book.getMailBox().sendNewOrder(2,42, Side.BUY,  50, 11);

        book.getMailBox().sendNewOrder(3,42, Side.SELL,  100, 11);
        ManagerMessage out = get(NEW_ORDER, mgr);
        out = get(LEVEL, mgr);
        assertEquals(12, out.getPrice());
        get(NEW_ORDER, mgr);
        out = get(LEVEL, mgr);
        assertEquals(11, out.getPrice());
        get(NEW_ORDER, mgr);
        out = get(TRADE, mgr);
        assertEquals(50, out.getQuantity());
        assertEquals(3, out.getId());
        assertEquals(1, out.getOpposite());
        assertEquals(12, out.getPrice());
        get(LEVEL, mgr);
        out = get(TRADE, mgr);
        assertEquals(50, out.getQuantity());
        assertEquals(3, out.getId());
        assertEquals(2, out.getOpposite());
        assertEquals(11, out.getPrice());
        get(LEVEL, mgr);
        assertFalse(mgr.hasMessage());
        book.getMailBox().sendKill();
    }


    private ManagerMessage get(ManagerMessage.Type type, ManagerMailBox mbox) {
        ManagerMessage msg = mbox.get();
        assertEquals(type, msg.getType());
        return msg;
    }

    @Test
    public void testTradeMarketOrder() throws InterruptedException {
        ManagerMailBox mgr = new ManagerMailBox(100);
        Book book = new Book();
        book.connect(mgr);
        book.start();
        book.getMailBox().sendNewOrder(1,42, Side.BUY,  50, 12);
        ManagerMessage out = mgr.get();
        assertEquals(NEW_ORDER, out.getType());
        book.getMailBox().sendNewOrder(2,42, Side.SELL,  100, 0);
        get(LEVEL, mgr);
        get(NEW_ORDER, mgr);
        out = get(TRADE, mgr);
        assertEquals(50, out.getQuantity());
        assertEquals(2, out.getId());
        assertEquals(1, out.getOpposite());
        get(LEVEL, mgr);
        out = get(CANCEL, mgr);
        assertEquals(50, out.getQuantity());
        assertFalse(mgr.hasMessage());
        book.getMailBox().sendKill();
    }

}