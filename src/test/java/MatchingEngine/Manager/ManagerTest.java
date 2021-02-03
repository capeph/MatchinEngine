package MatchingEngine.Manager;

import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Messaging.MailBoxImpl;
import MatchingEngine.Messaging.OrderMessage;
import MatchingEngine.Messaging.ResponseMessage;
import MatchingEngine.OrderBook.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    public void testNewOrder() {
        OrderMessage template = new OrderMessage();
        MailBox<ResponseMessage> rsp = new MailBoxImpl<>(100, ResponseMessage::getEmpty);
        Manager manager = new Manager();

        manager.connect(rsp);
        manager.start();
        manager.getMailBox().putCopy(template.buildNewOrder(1,
                42, Side.BUY,  100, 12));
        ResponseMessage out = rsp.get();
        assertEquals(ResponseMessage.Type.ORDER_ACK, out.getType());
        assertEquals("1:B100@12", out.getContents());
        manager.getMailBox().putCopy(template.buildKill());
    }

    @Test
    public void testTrade() {

        OrderMessage template = new OrderMessage();
        MailBox<ResponseMessage> rsp = new MailBoxImpl<>(100, ResponseMessage::getEmpty);
        Manager manager = new Manager();

        manager.connect(rsp);
        manager.start();
        manager.getMailBox().putCopy(template.buildNewOrder(1,
                42, Side.BUY,  100, 12));
        manager.getMailBox().putCopy(template.buildNewOrder(2,
                79, Side.SELL,  50, 12));
        manager.getMailBox().putCopy(template.buildTrade(2,
                1,50, 12));
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

        manager.getMailBox().putCopy(template.buildKill());
    }


}