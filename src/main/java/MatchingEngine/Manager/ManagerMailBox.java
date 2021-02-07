package MatchingEngine.Manager;

import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Trading.Side;

import static MatchingEngine.Manager.ManagerMessage.Type.*;


public class ManagerMailBox extends MailBox<ManagerMessage> {

    private ManagerMessage template = new ManagerMessage();

    public ManagerMailBox(int size) {
        super(size, ManagerMessage::new);
    }

    public void sendNewOrder(long orderId, long clientId, Side side, long quantity, long price) {
        template.populate(NEW_ORDER, orderId, clientId, side, quantity, price);
        putCopy(template);
    }

    public void sendKill() {
        template.populate(KILL, 0,0,Side.BUY, 0, 0);  // why BUY?
        putCopy(template);
    }

    public void sendLevelUpdate(Side side, long price, long volume) {
        template.populate(LEVEL, 0, 0, side, volume, price);
        putCopy(template);
    }

    public void sendCancel(long orderId, long quantity) {
        template.populate(CANCEL, orderId, 0, Side.BUY, quantity, 0);
        putCopy(template);
    }

    public void sendTrade(long id, long opposite, long quantity, long price) {
        template.populate(TRADE, id, opposite, Side.BUY, quantity, price);
        putCopy(template);
    }

    public void sendOrderInfo(long clientId, long orderId) {
        template.populate(INFO, orderId, clientId, Side.BUY, 0, 0);
        putCopy(template);
    }

    public void sendTradeReport(long clientId, long orderId) {
        template.populate(TRADE_REPORT, orderId, clientId, Side.BUY, 0, 0);
        putCopy(template);
    }

    public void sendOrderBook(long clientId) {
        template.populate(BOOK, 0, clientId, Side.BUY, 0, 0);
        putCopy(template);
    }

}
