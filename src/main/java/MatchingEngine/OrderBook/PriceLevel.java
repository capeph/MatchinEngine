package MatchingEngine.OrderBook;

import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Messaging.OrderMessage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PriceLevel {

    private final Map<Long, Order> orderQueue = new LinkedHashMap<>();
    private long volume = 0;
    private OrderMessage template = new OrderMessage();

    void add(Order order, MailBox<OrderMessage> toManager) {
        orderQueue.put(order.getId(), order);
        volume += order.getQuantity();
        toManager.putCopy(template.buildLevelUpdate(order.getSide(), order.getPrice(), volume));
    }

    Order remove(long id) {
        Order removed = orderQueue.remove(id);
        if (removed != null) {
            volume -= removed.getQuantity();
        }
        return removed;
    }

    boolean amendQty(long id, long newQuantity) {
        Order amend = orderQueue.get(id);
        if (amend == null || newQuantity == 0) {
            return false;
        }
        long delta = newQuantity - amend.getQuantity();
        amend.setQuantity(newQuantity);
        volume += delta;
        return true;
    }

    long getVolume() {
        return volume;
    }


    public long match(Order order, MailBox manager) {
        Iterator<Map.Entry<Long, Order>> it = orderQueue.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Order> entry = it.next();
            long id = entry.getKey();
            Order opposite = entry.getValue();
            if (opposite.getQuantity() > order.getQuantity()) { // partial trade
                manager.putCopy(template.buildTrade(order.getId(), opposite.getId(),
                        order.getQuantity(), opposite.getPrice()));
                opposite.setQuantity(opposite.getQuantity() - order.getQuantity());
                volume -= order.getQuantity();
                order.setQuantity(0);
            } else {
                manager.putCopy(template.buildTrade(order.getId(), opposite.getId(),
                        opposite.getQuantity(), opposite.getPrice()));
                it.remove();
                volume -= opposite.getQuantity();
                order.setQuantity(order.getQuantity() - opposite.getQuantity());
            }
        }
        return volume;
    }

}
