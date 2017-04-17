package ua.abond.netty.game.event;

import io.netty.channel.Channel;

public class PlayerDisconnectedMessage extends PlayerMessage {
    public PlayerDisconnectedMessage(Channel channel) {
        super(channel);
    }
}
