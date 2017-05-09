package ua.abond.netty.game.input.handler;

import io.netty.channel.ChannelId;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.component.CameraComponent;
import ua.abond.netty.game.event.PlayerAddedMessage;
import ua.abond.netty.game.input.MessageHandler;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.PhysicsService;
import ua.abond.netty.game.physics.collision.collider.CircleCollider;

import java.security.SecureRandom;
import java.util.Random;

public class PlayerAddedHandler implements MessageHandler<PlayerAddedMessage> {
    private static final Random random = new SecureRandom();

    private final ChannelMap<Player> players;
    private final PhysicsService physicsService;

    public PlayerAddedHandler(ChannelMap<Player> players, PhysicsService physicsService) {
        this.players = players;
        this.physicsService = physicsService;
    }

    @Override
    public void handle(PlayerAddedMessage msg) {
        Vector2 position = generateRandomPosition();
        Transform transform = new Transform(position);
        Player player = Player.builder()
                .name(msg.getName())
                .transform(transform)
                .direction(position)
                .bulletCollisionHandler((p, b) -> {
                    ChannelId channelId = players.find(p);
                    if (channelId == null) {
                        return;
                    }
                    players.remove(channelId);
                    physicsService.remove(p.getCollider());
                })
                .cameraComponent(new CameraComponent(transform, 100f))
                .build();
        player.setCollider(new CircleCollider(player, 20));
        physicsService.add(player.getCollider());
        players.put(msg.getChannel(), player);
    }

    private Vector2 generateRandomPosition() {
        return Vector2.builder()
                .x(random.nextInt(1000))
                .y(random.nextInt(1000))
                .build();
    }
}
