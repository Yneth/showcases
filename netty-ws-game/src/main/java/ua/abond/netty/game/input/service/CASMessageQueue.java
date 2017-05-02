package ua.abond.netty.game.input.service;

import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.input.MessageQueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CASMessageQueue implements MessageQueue<Message> {
    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void push(Message msg) {
        queue.add(msg);
    }

    @Override
    public Message poll() {
        return queue.poll();
    }

    @Override
    public boolean hasMessages() {
        return !queue.isEmpty();
    }
}
