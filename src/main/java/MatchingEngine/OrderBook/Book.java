package MatchingEngine.OrderBook;

import MatchingEngine.Logger;
import MatchingEngine.Manager.ManagerMailBox;
import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Trading.Order;
import MatchingEngine.Trading.Side;

public class Book extends Thread {
    //TODO: set manager (from server?

    private Logger logger = new Logger("Book");

    private Depth bidSide = new Depth(Side.BUY);
    private Depth askSide = new Depth(Side.SELL);

    private BookMailBox mailBox = new BookMailBox(100000);
    private ManagerMailBox toManager;

    public void connect(ManagerMailBox toManager) {
        this.toManager = toManager;
    }

    private Order decode(OrderMessage msg) {
        return new Order(msg.getId(), msg.getSide(),msg.getQuantity(),msg.getPrice(),msg.getOwner());
    }

    private void accept(OrderMessage msg) {
        toManager.sendNewOrder(msg.getId(), msg.getOwner(), msg.getSide(), msg.getQuantity(), msg.getPrice());
        Order order = decode(msg);
        logger.info("Received order " + order.getId() + ":" + order.getSide() + order.getQuantity() + "@" + order.getPrice());
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

    private void storeOrCancel(Order order, Depth bookSide, ManagerMailBox toManager) {
        if (order.getQuantity() > 0) {
            if (order.getPrice() == 0) { // cancel remaining part of market order
                toManager.sendCancel(order.getId(), order.getQuantity());
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
                    toManager.sendKill();  //propagate the shutdown
                    break;
                default: throw new IllegalArgumentException("Bad message type");
            }
        }
    }


    public BookMailBox getMailBox() {
        return mailBox;
    }
}
