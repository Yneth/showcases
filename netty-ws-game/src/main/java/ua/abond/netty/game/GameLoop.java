package ua.abond.netty.game;

import io.netty.channel.ChannelId;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.event.PlayerAddedMessage;
import ua.abond.netty.game.event.PlayerDisconnectedMessage;
import ua.abond.netty.game.event.PlayerShootMessage;
import ua.abond.netty.game.physics.Collider;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.QuadNode;
import ua.abond.netty.game.physics.collision.QuadTree;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameLoop implements Runnable {
    private final Random random = new SecureRandom();
    private final List<Bullet> bullets;
    private final ChannelMap<Player> channelMap;
    private final ConcurrentLinkedQueue<Message> eventBus;
    private QuadTree<Collider> quadTree = new QuadTree<>(-10, -10, 1020, 1020, 10, 1);

    public GameLoop(List<Bullet> bullets, ChannelMap<Player> channelMap, ConcurrentLinkedQueue<Message> eventBus) {
        this.bullets = bullets;
        this.channelMap = channelMap;
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        update();
    }

    private void update() {
        final float deltaTime = 0.017f;

        handleUserInput();
        updateBullets(deltaTime);
        updatePlayers(deltaTime);
        handleCollisions();
    }

    private void handleUserInput() {
        while (!eventBus.isEmpty()) {
            Message poll = eventBus.poll();
            if (poll instanceof PlayerShootMessage) {
                PlayerShootMessage shootMessage = (PlayerShootMessage) poll;
                Player owner = channelMap.get(shootMessage.getChannel());
                if (owner == null) {
                    continue;
                }
                Vector2 rotation = owner.getRotation();
                Bullet bullet = Bullet.builder()
                        .owner(owner)
                        .position(owner.getPosition().copy())
                        .direction(rotation.copy())
                        .build();
                bullets.add(bullet);
                quadTree.add(Bullet.toQuadNode(bullet));
            } else if (poll instanceof PlayerAddedMessage) {
                PlayerAddedMessage playerAddedMessage = (PlayerAddedMessage) poll;

                Vector2 position = generateRandomPosition();
                Player player = Player.builder()
                        .name(playerAddedMessage.getName())
                        .position(position)
                        .rotation(Vector2.ONE)
                        .target(position)
                        .bulletCollisionHandler((p, b) -> {
                            ChannelId channelId = channelMap.find(p);
                            if (channelId == null) {
                                return;
                            }
                            channelMap.remove(channelId);
                            quadTree.remove(Player.toQuadNode(p));
                        })
                        .build();
                quadTree.add(Player.toQuadNode(player));
                channelMap.put(playerAddedMessage.getChannel(), player);
            } else if (poll instanceof PlayerDisconnectedMessage) {
                PlayerDisconnectedMessage msg = (PlayerDisconnectedMessage) poll;
                Player player = channelMap.get(msg.getChannel());
                if (player != null) {
                    channelMap.remove(msg.getChannel());
                    quadTree.remove(Player.toQuadNode(player));
                }
            }
        }
    }

    private void updateBullets(float deltaTime) {
        final float speed = 100f;

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            QuadNode<Collider> old = Bullet.toQuadNode(bullet);

            bullet.getPosition().add(bullet.getDirection().copy().multiply(speed).multiply(deltaTime));
            float x = bullet.getPosition().getX();
            float y = bullet.getPosition().getY();

            QuadNode<Collider> updated = Bullet.toQuadNode(bullet);
            if (x >= 1000 || x <= 0 || y >= 1000 || y <= 0) {
                bullets.remove(i);
                quadTree.remove(old);
            }
            quadTree.update(old, updated);
        }
    }

    private void updatePlayers(float deltaTime) {
        final float speed = 100f;

        for (Player player : channelMap.values()) {
            if (player.getPosition().isCloseTo(player.getTarget(), 1f)) {
                continue;
            }
            QuadNode<Collider> old = Player.toQuadNode(player);
            Vector2 direction = player.getPosition().copy().minus(player.getTarget()).normalize();
            player.setRotation(direction.copy());
            Vector2 velocity = direction.multiply(speed).multiply(deltaTime);

            player.getPosition().add(velocity);
            quadTree.update(old, Player.toQuadNode(player));
        }
    }

    private void handleCollisions() {
        ArrayList<QuadNode<Collider>> nodes = new ArrayList<>(20);

        for (Player player : channelMap.values()) {
            QuadNode<Collider> node = Player.toQuadNode(player);
            quadTree.query(node.getRect(), nodes);

            for (QuadNode<Collider> other : nodes) {
                Collider object = other.getElement();
                if (!object.equals(player) && player.collides(object)) {
                    object.onCollision(player);
                    player.onCollision(object);
                }
            }
            nodes.clear();
        }
    }

    private Vector2 generateRandomPosition() {
        return Vector2.builder()
                .x(random.nextInt(100))
                .y(random.nextInt(100))
                .build();
    }
}
