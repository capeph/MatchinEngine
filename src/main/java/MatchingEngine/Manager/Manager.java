package MatchingEngine.Manager;

import MatchingEngine.Logger;
import MatchingEngine.Messaging.*;
import MatchingEngine.Responder.ResponderMailBox;
import MatchingEngine.Trading.Side;
import MatchingEngine.Responder.ResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Manager extends Thread {

    private Logger logger = new Logger("Manager");

    private Map<Long, OrderInfo> orderInfo = new HashMap<>();
    private final ManagerMailBox mailBox = new ManagerMailBox(10000);
    private ResponderMailBox toResponder;
    private ResponseMessage response = new ResponseMessage();

    private SortedMap<Long, Long> bidSizes = new TreeMap<>(Side.BUY.getComparator());
    private SortedMap<Long, Long> askSizes = new TreeMap<>(Side.SELL.getComparator());

    public ManagerMailBox getMailBox() {
        return mailBox;
    }

    public void connect(ResponderMailBox toResponder) {
        this.toResponder = toResponder;
    }

    public void run() {
        logger.info("starting Manager");
        boolean processMessages = true;
        while(processMessages) {
            ManagerMessage message = mailBox.get();
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
                    toResponder.sendKill();
                    processMessages = false;
                    break;
                default: throw new IllegalArgumentException("Bad message type");
            }
        }
    }

    private void storeNew(ManagerMessage msg) {
        OrderInfo order = new OrderInfo(msg.getId(), msg.getSide(), msg.getQuantity(), msg.getPrice(), msg.getOwner());
        orderInfo.put(msg.getId(), order);
        logger.info("order " + order.getId() + " stored");
        toResponder.sendOrderAck(order);
    }

    private void processTrade(ManagerMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        long price = msg.getPrice();
        long quantity = msg.getQuantity();
        OrderInfo opposite = orderInfo.get(msg.getOpposite());
        order.addTrade(quantity, price, msg.getOpposite());
        opposite.addTrade(quantity, price, msg.getId());
        logger.info("traded " + quantity + "@" + price + " between " + order.getId() + " and " + opposite.getId());
        toResponder.sendTrade(order.getId(), quantity, price, order.getOwnerid());
        toResponder.sendTrade(opposite.getId(), quantity, price, opposite.getOwnerid());
    }

    private void processInfo(ManagerMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        toResponder.sendOrderInfo(order, msg.getOwner());
    }

    private void processTradeReport(ManagerMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        StringBuilder sb = new StringBuilder();
        for(TradeInfo trade: order.getTrades()) {
            OrderInfo opposite = orderInfo.get(trade.getOppositeOrder());
            sb.append(opposite.getOwnerid()).append(":").append(trade.getQuantity()).append("@").append(trade.getPrice());
        }
        toResponder.sendTradeReport(sb.toString(), msg.getOwner());
    }

    private void processBook(ManagerMessage msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\nB:");
        for (Map.Entry<Long, Long>entry : bidSizes.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
        }
        sb.append("\r\nS:");
        for (Map.Entry<Long, Long>entry : askSizes.entrySet()) {
            sb.append(" ").append(entry.getKey()).append(":").append(entry.getValue());
        }
        toResponder.sendBook(msg.getOwner(), sb.toString());
    }

    private void processLevel(ManagerMessage msg) {
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

    private void processCancel(ManagerMessage msg) {
        OrderInfo order = orderInfo.get(msg.getId());
        long cancelled = msg.getQuantity();
        order.setQuantity(order.getQuantity() - cancelled);
        orderInfo.put(msg.getId(), order);
        logger.info("cancelled " + cancelled + " from " + order.getId() + " " + order.getQuantity() + " remaining");
        toResponder.sendCancel(order.getId(), order.getQuantity(), order.getOwnerid());
    }


}
