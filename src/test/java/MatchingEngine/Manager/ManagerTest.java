package MatchingEngine.Manager;

import MatchingEngine.Messaging.MailBox;
import MatchingEngine.OrderBook.OrderMessage;
import MatchingEngine.Responder.ResponderMailBox;
import MatchingEngine.Responder.ResponseMessage;
import MatchingEngine.Trading.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    public void testNewOrder() {
        ResponderMailBox rsp = new ResponderMailBox(100);
        Manager manager = new Manager();

        manager.connect(rsp);
        manager.start();
        manager.getMailBox().sendNewOrder(1,42, Side.BUY,  100, 12);
        ResponseMessage out = rsp.get();
        assertEquals(ResponseMessage.Type.ORDER_ACK, out.getType());
        assertEquals("1:B100@12", out.getContents());
        manager.getMailBox().sendKill();
    }

    @Test
    public void testTrade() {

        OrderMessage template = new OrderMessage();
        ResponderMailBox rsp = new ResponderMailBox(100);
        Manager manager = new Manager();

        manager.connect(rsp);
        manager.start();
        manager.getMailBox().sendNewOrder(1,42, Side.BUY,  100, 12);
        manager.getMailBox().sendNewOrder(2,79, Side.SELL,  50, 12);
        manager.getMailBox().sendTrade(2,1,50, 12);
        ResponseMessage out = rsp.get();
        assertEquals(ResponseMessage.Type.ORDER_ACK, out.getType());
        assertEquals(42, out.getOwner());
        assertEquals("1:B100@12", out.getContents());
        out = rsp.get();
        assertEquals(ResponseMessage.Type.ORDER_ACK, out.getType());
        assertEquals(79, out.getOwner());
        assertEquals("2:S50@12", out.getContents());
        out = rsp.get();
        assertEquals(ResponseMessage.Type.TRADE, out.getType());
        assertEquals(79, out.getOwner());
        assertEquals("2:50@12", out.getContents());
        out = rsp.get();
        assertEquals(ResponseMessage.Type.TRADE, out.getType());
        assertEquals(42, out.getOwner());
        assertEquals("1:50@12", out.getContents());

        manager.getMailBox().sendKill();
    }


}