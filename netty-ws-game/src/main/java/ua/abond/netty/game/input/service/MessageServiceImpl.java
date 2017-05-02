package ua.abond.netty.game.input.service;

import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.input.MessageHandler;
import ua.abond.netty.game.input.MessageQueue;
import ua.abond.netty.game.input.MessageService;

import java.util.HashMap;
import java.util.Map;

public class MessageServiceImpl implements MessageService<Message> {
    private final Map<Class<? extends Message>, MessageHandler> handlers;
    private final MessageQueue<Message> queue;

    public MessageServiceImpl(MessageQueue<Message> queue) {
        this.queue = queue;
        this.handlers = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(float deltaTime) {
        while (queue.hasMessages()) {
            Message msg = queue.poll();
            handlers.get(msg.getClass()).handle(msg);
        }
    }

    @Override
    public void addHandler(Class<? extends Message> type, MessageHandler<? extends Message> handler) {
        handlers.put(type, handler);
    }
}
