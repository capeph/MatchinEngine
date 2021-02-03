package MatchingEngine.Messaging;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;


public class MailBoxImpl<T extends Copyable<T>> implements MailBox<T> {

    private class Container {
        T data;
        AtomicLong count;

        public Container(T data, long initCount) {
            this.data = data;
            count = new AtomicLong(initCount);
        }
    }

    private int size;
    private ArrayList<Container> buffer;
    private AtomicLong writeIdx = new AtomicLong(0);
    private int readIdx = 0;

    // require a Supplier that initializes data fields
    public MailBoxImpl(int size, Supplier<T> initMethod)  {
        this.size = size;
        buffer = new ArrayList<Container>(size);
        for(int i = 0; i < size; i++) {
            Container container = new Container(initMethod.get(), -1);
            buffer.add(container);
        }
    }

    // get and increase writeIdx as myIdx
    // copy data into container
    // swap container count with myIdx
    public void putCopy(T data) {
        long idx = writeIdx.getAndIncrement();
        Container dest = buffer.get((int) (idx % size));
        dest.data.copy(data);  // copy the value to the buffer
        dest.count.set(idx);
    }

    // single consumer, no need to use atomics for readIndex
    // wait for the count to signal that the value has been updated
    public T get() {
        Container next = buffer.get(readIdx % size);
        while (next.count.get() < readIdx) { }   // busy wait for data
        readIdx++;
        return next.data;
    }

    @Override
    public boolean hasMessage() {
        Container next = buffer.get(readIdx % size);
        return next.count.get() == readIdx;
    }

}
