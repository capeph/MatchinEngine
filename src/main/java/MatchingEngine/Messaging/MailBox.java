package MatchingEngine.Messaging;

public interface MailBox<T> {

    void putCopy(T data);

    T get();

    boolean hasMessage();
}
