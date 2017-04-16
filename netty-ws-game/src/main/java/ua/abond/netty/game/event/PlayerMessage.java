package ua.abond.netty.game.event;

import io.netty.channel.Channel;
import lombok.Getter;

public abstract class PlayerMessage implements Message {
    @Getter
    private final Channel channel;

    public PlayerMessage(Channel channel) {
        this.channel = channel;
    }
}
