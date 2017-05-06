package ua.abond.netty.game.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.GameLoop;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Wall;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.exception.ApplicationStartupException;
import ua.abond.netty.game.exception.VerboseRunnable;
import ua.abond.netty.game.input.MessageQueue;
import ua.abond.netty.game.input.service.CASMessageQueue;
import ua.abond.netty.game.physics.Vector2;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketServer {
    private static final boolean SSL = System.getProperty("ssl") != null;

    private static final String WEBSOCKET_URI = "/ws";

    private final int port;
    private final List<Bullet> bullets;
    private final List<Wall> walls;
    private final ChannelMap<Player> channelMap;
    private final MessageQueue<Message> eventBus;

    public WebSocketServer(int port) {
        this.port = port;
        this.bullets = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.channelMap = new ChannelMap<>(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        this.eventBus = new CASMessageQueue();
    }

    public void start() {
        ScheduledExecutorService executorService = Platform.createLoopGroup(1);

        ByteBufAllocator allocator = new PooledByteBufAllocator(true);
        EventLoopGroup master = Platform.createLoopGroup(1);
        EventLoopGroup slave = Platform.createLoopGroup(4);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(Platform.serverSocketChannelType())
                .group(master, slave)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.ALLOCATOR, allocator)
                .childOption(ChannelOption.SO_REUSEADDR, true);

        executorService.scheduleAtFixedRate(
                new VerboseRunnable(
                        new GameLoop(channelMap, bullets, walls, eventBus)
                ), 0, 17, TimeUnit.MILLISECONDS
        );

        executorService.scheduleAtFixedRate(new VerboseRunnable(() -> {
            ByteBuf buf = allocator.directBuffer();
            buf.writeByte(0);
            buf.writeByte(1);
            buf.writeByte(channelMap.values().size());
            for (Player player : channelMap.values()) {
                Vector2 position = player.getTransform().getPosition();
                buf.writeShort((int) (position.getX() * 10));
                buf.writeShort((int) (position.getY() * 10));
            }
            buf.writeShort(bullets.size());
            for (Bullet bullet : bullets) {
                Vector2 position = bullet.getTransform().getPosition();
                buf.writeShort((int) (position.getX() * 10));
                buf.writeShort((int) (position.getY() * 10));
            }
            for (Wall wall : walls) {
                Vector2 position = wall.getTransform().getPosition();
                buf.writeShort((int) (position.getX() * 10));
                buf.writeShort((int) (position.getY() * 10));
                buf.writeShort(wall.getCollider().width() * 10);
                buf.writeShort(wall.getCollider().height() * 10);
            }
            channelMap.writeAndFlush(new BinaryWebSocketFrame(buf), ChannelMatchers.all(), true);
        }), 0, 33, TimeUnit.MILLISECONDS);

        final SslContext sslCtx = getSslContext();

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                if (sslCtx != null) {
                    pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                }
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
        int port = 8082;
        String portStr = System.getProperty("port");
        if (portStr != null) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ignore) {
            }
        }
        log.info("Starting application at {}", port);
        new WebSocketServer(port).start();
    }

    private SslContext getSslContext() {
        if (SSL) {
            SelfSignedCertificate ssc = null;
            try {
                ssc = new SelfSignedCertificate();
            } catch (CertificateException e) {
                throw new ApplicationStartupException("Failed to retrieve certificate", e);
            }
            try {
                if (ssc != null) {
                    return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                }
            } catch (SSLException e) {
                throw new ApplicationStartupException("Failed to build sslContext", e);
            }
        }
        return null;
    }
}
