package MatchingEngine.Trading;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testGetters() {
        Order order = new Order(5, Side.SELL, 42, 17, 7);
        assertEquals(5, order.getId());
        assertEquals(Side.SELL, order.getSide());
        assertEquals(42, order.getQuantity());
        assertEquals(17, order.getPrice());
    }

    @Test
    void acceptsPrice() {
        Order ask = new Order(5, Side.SELL, 42, 17, 7);
        assertTrue(ask.acceptsPrice(17));
        assertTrue(ask.acceptsPrice(18));
        assertFalse(ask.acceptsPrice(16));
    }
}