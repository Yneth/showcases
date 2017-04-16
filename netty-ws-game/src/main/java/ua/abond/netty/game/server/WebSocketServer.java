package ua.abond.netty.game.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.event.PlayerAddedMessage;
import ua.abond.netty.game.event.PlayerShootMessage;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.physics.collision.QuadTree;
import ua.abond.netty.game.thread.VerboseRunnable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class WebSocketServer {
    private static final String WEBSOCKET_URI = "/ws";
    private final Random random = new SecureRandom();

    private final int port;
    private QuadTree quadTree;
    private final List<Bullet> bullets;
    private final ChannelMap<Player> channelMap;
    private final ConcurrentLinkedQueue<Message> eventBus;

    public WebSocketServer(int port) {
        this.port = port;
        this.bullets = new ArrayList<>();
        this.channelMap = new ChannelMap<>(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        this.eventBus = new ConcurrentLinkedQueue<>();
    }

    public void start() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        EventLoopGroup master = new NioEventLoopGroup(2);
        EventLoopGroup slave = new NioEventLoopGroup(4);

        ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(master, slave);
        executorService.scheduleAtFixedRate(new VerboseRunnable(() -> {
            final float deltaTime = 0.017f;
            final float speed = 100.0f;
            while (!eventBus.isEmpty()) {
                Message poll = eventBus.poll();
                if (poll instanceof PlayerShootMessage) {
                    PlayerShootMessage shootMessage = (PlayerShootMessage) poll;
                    Player owner = channelMap.get(shootMessage.getChannel());
                    Vector2 rotation = owner.getRotation();
                    bullets.add(Bullet.builder()
                            .owner(owner)
                            .position(owner.getPosition().copy())
                            .direction(rotation.copy())
                            .build()
                    );
                } else if (poll instanceof PlayerAddedMessage) {
                    PlayerAddedMessage playerAddedMessage = (PlayerAddedMessage) poll;

                    Vector2 position = randomPosition();
//                    quadTree.add();
                    channelMap.put(playerAddedMessage.getChannel(), Player.builder()
                            .name(playerAddedMessage.getName())
                            .position(position)
                            .target(position)
                            .build()
                    );
                }
            }

            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                bullet.getPosition().add(bullet.getDirection().copy().multiply(speed).multiply(deltaTime));
                float x = bullet.getPosition().getX();
                float y = bullet.getPosition().getY();
                if (x > 1000 || x < 0 || y > 1000 || y < 0) {
                    bullets.remove(i);
                }
            }
            for (Player player : channelMap.values()) {
                if (player.getPosition().isCloseTo(player.getTarget(), 1f)) {
                    continue;
                }
                Vector2 direction = player.getPosition().copy().minus(player.getTarget()).normalize();
                player.setRotation(direction.copy());
                Vector2 velocity = direction.multiply(speed).multiply(deltaTime);

                player.getPosition().add(velocity);
            }
        }), 0, 17, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(new VerboseRunnable(() -> {
            String userPositions = channelMap.values().stream()
                    .map(Player::getPosition)
                    .map(pos -> pos.getX() + "," + pos.getY())
                    .collect(Collectors.joining(";"));
            String bulletPositions = bullets.stream()
                    .map(Bullet::getPosition)
                    .map(pos -> pos.getX() + "," + pos.getY())
                    .collect(Collectors.joining(";"));

            channelMap.writeAndFlush(new TextWebSocketFrame("0:" + userPositions + "|" + bulletPositions));
        }), 0, 33, TimeUnit.MILLISECONDS);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_URI, null, true));
                pipeline.addLast(new WebSocketServerHandler(channelMap, eventBus));
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(new HttpStaticFileHandler());
            }
        });
        Channel channel;
        try {
            channel = bootstrap.bind(port).channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Failed to close server channel", e);
            Thread.currentThread().interrupt();
        } finally {
            master.shutdownGracefully();
            slave.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new WebSocketServer(8082).start();
    }

    private Vector2 randomPosition() {
        return Vector2.builder().x(random.nextInt(500)).y(random.nextInt(500)).build();
    }
}
