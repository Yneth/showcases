package ua.abond.netty.game;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import ua.abond.netty.game.domain.Player;

import java.util.Map;

public class GameLoop implements Runnable {
    private static final float FRAME_RATE = 1f / 30f;

    private final ChannelGroup channel;
    private final Map<ChannelId, Player> players;

    public GameLoop(ChannelGroup channel, Map<ChannelId, Player> players) {
        this.channel = channel;
        this.players = players;
    }

    @Override
    public void run() {

    }

    private void fixedUpdate(float delta) {

    }

    private void update(float delta) {

    }
}
