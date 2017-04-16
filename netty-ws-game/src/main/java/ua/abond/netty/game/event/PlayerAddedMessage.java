package ua.abond.netty.game.event;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class PlayerAddedMessage extends PlayerMessage {
    private String name;

    public PlayerAddedMessage(Channel channel, String name) {
        super(channel);
        this.name = name;
    }
}
