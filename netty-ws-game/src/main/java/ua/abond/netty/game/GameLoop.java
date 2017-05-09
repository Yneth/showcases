package ua.abond.netty.game;

import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Wall;
import ua.abond.netty.game.domain.WallBulletCollisionHandler;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.event.PlayerAddedMessage;
import ua.abond.netty.game.event.PlayerDisconnectedMessage;
import ua.abond.netty.game.event.PlayerShootMessage;
import ua.abond.netty.game.input.MessageQueue;
import ua.abond.netty.game.input.MessageService;
import ua.abond.netty.game.input.handler.PlayerAddedHandler;
import ua.abond.netty.game.input.handler.PlayerDisconnectedHandler;
import ua.abond.netty.game.input.handler.PlayerShootHandler;
import ua.abond.netty.game.input.service.MessageServiceImpl;
import ua.abond.netty.game.output.loop.OutputLoop;
import ua.abond.netty.game.output.loop.OutputLoopBootstrap;
import ua.abond.netty.game.physics.Transform;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.PhysicsService;
import ua.abond.netty.game.physics.collision.collider.RectCollider;
import ua.abond.netty.game.physics.collision.service.PhysicsServiceImpl;
import ua.abond.netty.game.physics.collision.spatial.quad.QuadTree;

import java.util.ArrayList;
import java.util.List;

public class GameLoop implements Runnable {
    private final ChannelMap<Player> channelMap;
    private final List<Wall> walls = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    private final PhysicsService physicsService;
    private final MessageService<Message> inputService;

    public GameLoop(ChannelMap<Player> channelMap, MessageQueue<Message> messageQueue, OutputLoopBootstrap outputLoop) {
        this.physicsService = new PhysicsServiceImpl(new QuadTree<>(-10, -10, 1020, 1020, 10, 1));
        this.inputService = new MessageServiceImpl(messageQueue);
        this.inputService.addHandler(
                PlayerAddedMessage.class, new PlayerAddedHandler(channelMap, physicsService)
        );
        this.inputService.addHandler(
                PlayerDisconnectedMessage.class, new PlayerDisconnectedHandler(channelMap, physicsService)
        );
        this.inputService.addHandler(
                PlayerShootMessage.class, new PlayerShootHandler(bullets, channelMap, physicsService)
        );

        this.channelMap = channelMap;

        WallBulletCollisionHandler handler = (w, b) -> {
            bullets.remove(b);
            physicsService.remove(b.getCollider());
        };
        createWall(new Vector2(250, 250), 5, 300, handler);
        createWall(new Vector2(250, 250), 300, 5, handler);

        createWall(new Vector2(750, 250), 5, 300, handler);
        createWall(new Vector2(750, 250), 300, 5, handler);

        createWall(new Vector2(250, 750), 5, 300, handler);
        createWall(new Vector2(250, 750), 300, 5, handler);

        createWall(new Vector2(750, 750), 5, 300, handler);
        createWall(new Vector2(750, 750), 300, 5, handler);

        outputLoop.setOutputCallback(new OutputLoop(channelMap, physicsService));
        outputLoop.start();
    }

    private void createWall(Vector2 pos, int w, int h, WallBulletCollisionHandler handler) {
        Wall wall = new Wall(pos);
        wall.setWallBulletCollisionHandler(handler);
        wall.setCollider(new RectCollider(wall, w, h));
        this.walls.add(wall);
        this.physicsService.add(wall.getCollider());
    }

    @Override
    public void run() {
        update();
    }

    private void update() {
        final float deltaTime = 0.017f;

        inputService.handle(deltaTime);
        updateBullets(deltaTime);
        updatePlayers(deltaTime);
        physicsService.update(deltaTime);
    }

    private void updateBullets(float deltaTime) {
        final float speed = 100f;

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            physicsService.remove(bullet.getCollider());
            Vector2 position = bullet.getTransform().getPosition();
            position.add(bullet.getDirection().copy().multiply(speed).multiply(deltaTime));
            float x = position.getX();
            float y = position.getY();

            if (x >= 1000 || x <= 0 || y >= 1000 || y <= 0) {
                bullets.remove(i);
            } else {
                physicsService.add(bullet.getCollider());
            }
        }
    }

    private void updatePlayers(float deltaTime) {
        final float speed = 100f;

        for (Player player : channelMap.values()) {
            Transform transform = player.getTransform();
            if (transform.getPosition().isCloseTo(player.getDirection(), 1f)) {
                continue;
            }
            physicsService.remove(player.getCollider());
            Vector2 direction = transform.getPosition().copy().minus(player.getDirection()).normalize();
            transform.setRotation(direction.copy());
            Vector2 velocity = direction.multiply(speed).multiply(deltaTime);

            transform.getPosition().add(velocity);
            physicsService.add(player.getCollider());
            player.getCameraComponent().update(deltaTime);
        }
    }
}
