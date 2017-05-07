package ua.abond.netty.game.input.handler;

import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.PlayerShootMessage;
import ua.abond.netty.game.input.MessageHandler;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.PhysicsService;
import ua.abond.netty.game.physics.collision.collider.CircleCollider;

import java.util.List;

public class PlayerShootHandler implements MessageHandler<PlayerShootMessage> {
    private final List<Bullet> bullets;
    private final ChannelMap<Player> players;
    private final PhysicsService physicsService;

    public PlayerShootHandler(List<Bullet> bullets, ChannelMap<Player> players, PhysicsService physicsService) {
        this.bullets = bullets;
        this.players = players;
        this.physicsService = physicsService;
    }

    @Override
    public void handle(PlayerShootMessage msg) {
        Player owner = players.get(msg.getChannel());
        if (owner == null) {
            return;
        }
        Transform ownerTransform = owner.getTransform();
        Vector2 rotation = ownerTransform.getRotation();
        Vector2 position = ownerTransform.getPosition().copy()
                .add(rotation.copy().multiply(owner.getCollider().height() * 0.5f));
        Bullet bullet = Bullet.builder()
                .owner(owner)
                .transform(new Transform(position))
                .direction(rotation.copy())
                .build();
        bullet.setCollider(new CircleCollider(bullet, 5));

        bullets.add(bullet);
        physicsService.add(bullet.getCollider());
    }
}
