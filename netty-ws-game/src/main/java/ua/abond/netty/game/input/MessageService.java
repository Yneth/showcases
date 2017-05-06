package ua.abond.netty.game.input;

public interface MessageService<T> {

    void handle(float deltaTime);

    void addHandler(Class<? extends T> type, MessageHandler<? extends T> handler);
}
