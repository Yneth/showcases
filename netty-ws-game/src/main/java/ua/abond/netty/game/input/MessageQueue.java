package ua.abond.netty.game.input;

public interface MessageQueue<T> {

    void push(T msg);

    T poll();

    boolean hasMessages();
}
