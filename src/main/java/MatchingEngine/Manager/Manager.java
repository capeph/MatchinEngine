package MatchingEngine.Manager;

import MatchingEngine.Logger;
import MatchingEngine.Messaging.*;
import MatchingEngine.OrderBook.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Manager extends Thread {

    private Logger logger = new Logger("Manager");

    private Map<Long, OrderInfo> orderInfo = new HashMap<>();
    private final MailBox<OrderMessage> mailBox = new MailBoxImpl<>(10000, OrderMessage::getEmpty);
    private MailBox<ResponseMessage> toResponder;
    private ResponseMessage response = new ResponseMessage();

    private SortedMap<Long, Long> bidSizes = new TreeMap<>(Side.BUY.getComparator());
    private SortedMap<Long, Long> askSizes = new TreeMap<>(Side.SELL.getComparator());

    public MailBox<OrderMessage> getMailBox() {
        return mailBox;
    }

    public void connect(MailBox<ResponseMessage> toResponder) {
        this.toResponder = toResponder;
    }

    public void run() {
        logger.info("starting Manager");
        boolean processMessages = true;
        while(processMessages) {
            OrderMessage message = mailBox.get();
            switch (message.getType()) {
                case NEW_ORDER:
                    storeNew(message);
                    break;
                case TRADE:
                    processTrade(message);
                    break;
                case CANCEL:
                    processCancel(message);
                    break;
                case INFO:
                    processInfo(message);
                    break;
                case TRADE_REPORT:
                    processTradeReport(message);
                    break;
                case LEVEL:
                    processLevel(message);
                    break;
                case BOOK:
                    processBook(message);
                case KILL:
                    toResponder.putCopy(response.buildKill());
                    processMessages = false;
                    break;
                default: throw new IllegalArgumentException("Bad message type");
            }
        }
    }

    private void storeNew(OrderMessage msg) {
        OrderInfo order = new OrderInfo(msg.getId(), msg.getSide(), msg.getQuantity(), msg.getPrice(), msg.getOwner());
        orderInfo.put(msg.getId(), order);
        logger.info("order " + order.getId() + " stored");
        toResponder.putCopy(response.buildOrderAck(order));
    }

    private void processTrade(OrderMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        long price = msg.getPrice();
        long quantity = msg.getQuantity();
        OrderInfo opposite = orderInfo.get(msg.getOpposite());
        order.addTrade(quantity, price, msg.getOpposite());
        opposite.addTrade(quantity, price, msg.getId());
        logger.info("traded " + quantity + "@" + price + " between " + order.getId() + " and " + opposite.getId());
        toResponder.putCopy(response.buildTrade(order.getId(), quantity, price, order.getOwnerid()));
        toResponder.putCopy(response.buildTrade(opposite.getId(), quantity, price, opposite.getOwnerid()));
    }

    private void processInfo(OrderMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        toResponder.putCopy(response.buildOrderInfo(order, msg.getOwner()));
    }

    private void processTradeReport(OrderMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        StringBuilder sb = new StringBuilder();
        for(OrderInfo.Trade trade: order.getTrades()) {
            OrderInfo opposite = orderInfo.get(trade.getOppositeOrder());
            sb.append(opposite.getOwnerid()).append(":").append(trade.getQuantity()).append("@").append(trade.getPrice());
        }
        toResponder.putCopy(response.buildTradeReport(sb.toString(), msg.getOwner()));
    }

    private void processBook(OrderMessage msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\nB:");
        for (Map.Entry<Long, Long>entry : bidSizes.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
        }
        sb.append("\r\nS:");
        for (Map.Entry<Long, Long>entry : askSizes.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
        }
        toResponder.putCopy(response.buildBook(msg.getOwner(), sb.toString()));
    }

    private void processLevel(OrderMessage msg) {
        Side side = msg.getSide();
        long price = msg.getPrice();
        long size = msg.getQuantity();
        SortedMap<Long, Long> sizes = side == Side.BUY ? bidSizes : askSizes;
        if (size == 0) {
            sizes.remove(price);
        } else {
            sizes.put(price, size);
        }
    }

    private void processCancel(OrderMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        long cancelled = msg.getQuantity();
        order.setQuantity(order.getQuantity() - cancelled);
        orderInfo.put(msg.getId(), order);
        logger.info("cancelled " + cancelled + " from " + order.getId() + " " + order.getQuantity() + " remaining");
        toResponder.putCopy(response.buildCancel(order.getId(), order.getQuantity(), order.getOwnerid()));
    }


}
