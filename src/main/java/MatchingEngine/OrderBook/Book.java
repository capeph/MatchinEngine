package MatchingEngine.OrderBook;

import MatchingEngine.Logger;
import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Messaging.MailBoxImpl;
import MatchingEngine.Messaging.OrderMessage;

public class Book extends Thread {
    //TODO: set manager (from server?

    private Logger logger = new Logger("Book");

    private Depth bidSide = new Depth(Side.BUY);
    private Depth askSide = new Depth(Side.SELL);

    private MailBox<OrderMessage> mailBox = new MailBoxImpl<>(100000, OrderMessage::getEmpty);
    private MailBox<OrderMessage> toManager;
    private OrderMessage template = new OrderMessage();

    public void connect(MailBox<OrderMessage> toManager) {
        this.toManager = toManager;
    }

    private Order decode(OrderMessage msg) {
        return new Order(msg.getId(), msg.getSide(),msg.getQuantity(),msg.getPrice(),msg.getOwner());
    }

    private void accept(OrderMessage orderMsg) {
        toManager.putCopy(orderMsg);
        Order order = decode(orderMsg);
        logger.info("Received order " + order.getSide() + order.getQuantity() + "@" + order.getPrice());
        switch (order.getSide()) {
            case BUY:
                askSide.match(order, toManager);
                storeOrCancel(order, bidSide, toManager);
                break;
            case SELL:
                bidSide.match(order, toManager);
                storeOrCancel(order, askSide, toManager);
                break;
        }
    }

    private void storeOrCancel(Order order, Depth bookSide, MailBox<OrderMessage> toManager) {
        if (order.getQuantity() > 0) {
            if (order.getPrice() == 0) { // cancel remaining part of market order
                toManager.putCopy(template.buildCancel(order.getId(), order.getQuantity()));
            } else {
                bookSide.add(order, toManager);
            }
        }
    }


    public void run() {
        logger.info("starting Book");
        boolean processMessages = true;
        while(processMessages) {
            OrderMessage message = mailBox.get();
            switch (message.getType()) {
                case NEW_ORDER:
                    accept(message);
                    break;
                case KILL:
                    processMessages = false;
                    toManager.putCopy(message);  //propagate the shutdown
                    break;
                default: throw new IllegalArgumentException("Bad message type");
            }
        }
    }


    public MailBox<OrderMessage> getMailBox() {
        return mailBox;
    }
}
