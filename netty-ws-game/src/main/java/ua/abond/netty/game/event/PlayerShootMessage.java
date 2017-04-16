package ua.abond.netty.game.event;

import io.netty.channel.Channel;

public class PlayerShootMessage extends PlayerMessage {

    public PlayerShootMessage(Channel channel) {
        super(channel);
    }
}
