package MatchingEngine.Responder;

import MatchingEngine.Manager.OrderInfo;
import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Trading.Order;

import static MatchingEngine.Responder.ResponseMessage.Type.*;

public class ResponderMailBox extends MailBox<ResponseMessage> {

    private ResponseMessage template = new ResponseMessage();

    public ResponderMailBox(int size) {
        super(size, ResponseMessage::new);
    }



    public void sendTrade(Long orderId, long quantity, long price, long owner) {
        template.populate(TRADE, owner, orderId + ":" + quantity + "@" + price);
        putCopy(template);
    }

    public void sendOrderAck(Order order) {
        template.populate(ORDER_ACK, order.getOwnerid(), order.getId() + ":" + order.getSide() +
                order.getQuantity() + "@" + order.getPrice());
        putCopy(template);
    }


    public void sendOrderInfo(OrderInfo order, long owner) {
        template.populate(ORDER_INFO, owner, order.getId() + ":" + order.getSide() + order.getQuantity() + "@"
                + order.getPrice() + "by" + order.getOwnerid());
        putCopy(template);
    }

    public void sendTradeReport(String report, long ownerId) {
        template.populate(TRADE_REPORT, ownerId, report);
        putCopy(template);
    }

    public void sendCancel(long orderId, long quantity, long ownerId) {
        template.populate(CANCEL, ownerId, orderId + ":" + quantity);
        putCopy(template);
    }

    public void sendBook(long owner, String dump) {
        template.populate(BOOK, owner, dump);
        putCopy(template);
    }

    public void sendError(long owner, String error) {
        template.populate(ERROR, owner, error);
        putCopy(template);
    }



    public void sendKill() {
        template.populate(KILL, 0,"");
        putCopy(template);
    }
}
