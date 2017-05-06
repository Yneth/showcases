package ua.abond.netty.game.input;

public interface MessageHandler<M> {
    void handle(M msg);
}
