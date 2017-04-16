package ua.abond.netty.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.HealthPack;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.physics.Vector2;

public class GameLoop implements Runnable {
    private final Random random = new SecureRandom();

    private final ChannelGroup channel;
    private final Map<ChannelId, Player> players;

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<HealthPack> healthPacks = new ArrayList<>(5);

    public GameLoop(ChannelGroup channel, Map<ChannelId, Player> players) {
        this.channel = channel;
        this.players = players;
    }

    @Override
    public void run() {
        update();
    }

    private void update() {
        if (healthPacks.size() < 5) {
            healthPacks.add(HealthPack.builder().position(generateRandomPosition()).build());
        }
    }

    private Vector2 generateRandomPosition() {
        return Vector2.builder()
                .x(random.nextInt(100))
                .y(random.nextInt(100))
                .build();
    }
}
