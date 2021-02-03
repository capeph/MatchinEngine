package MatchingEngine.OrderBook;

import java.util.Comparator;

public enum Side {
    BUY((x,y) -> Long.compare(y, x), 1),
    SELL((x,y) -> Long.compare(y,x), 2);

    private final Comparator comparator;
    private int value = 0;

    Side(Comparator<Long> comparator, int intVal) {
        this.comparator = comparator;
        this.value = intVal;
    }

    public static Side fromChar(char ch) {
        switch (ch) {
            case 'b':
            case 'B': return BUY;
            case 's':
            case 'S': return SELL;
            default : throw new IllegalArgumentException("Invalid side: " + ch);
        }
    }

    public static Side fromInt(int side) {
        switch (side) {
            case 1: return BUY;
            case 2: return SELL;
            default : throw new IllegalArgumentException("Invalid side: " + side);
        }
    }

    public Comparator<Long> getComparator() {
        return comparator;
    }

    public long intValue() {
        return value;
    }

    @Override
    public String toString() {
        return value == 1 ? "B" : "S";
    }
}
