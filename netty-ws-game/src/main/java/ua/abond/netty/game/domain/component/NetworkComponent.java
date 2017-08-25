package ua.abond.netty.game.domain.component;

import io.netty.channel.Channel;

public class NetworkComponent implements Behaviour {
    private final Channel channel;

    public NetworkComponent(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void update(float deltaTime) {

    }
}
