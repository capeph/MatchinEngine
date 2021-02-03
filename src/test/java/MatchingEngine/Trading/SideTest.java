package MatchingEngine.Trading;


import MatchingEngine.OrderBook.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SideTest {

    @Test
    public void testValidSide() {
        assertEquals(Side.BUY, Side.fromChar('b'));
        assertEquals(Side.BUY, Side.fromChar('B'));
        assertEquals(Side.SELL, Side.fromChar('s'));
        assertEquals(Side.SELL, Side.fromChar('S'));
        assertEquals(Side.BUY, Side.fromInt(1));
        assertEquals(Side.SELL, Side.fromInt(2));
    }


    @Test
    public void testInvalidCharToSide() {
        try {
            Side side = Side.fromChar('z');
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }


    @Test
    public void testInvalidIntToSide() {
        try {
            Side side = Side.fromInt(0);
            fail();
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }
}