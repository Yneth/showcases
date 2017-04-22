package ua.abond.netty.game.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
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
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import ua.abond.netty.game.ChannelMap;
import ua.abond.netty.game.GameLoop;
import ua.abond.netty.game.domain.Bullet;
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.event.Message;
import ua.abond.netty.game.exception.ApplicationStartupException;
import ua.abond.netty.game.physics.Vector2;
import ua.abond.netty.game.thread.VerboseRunnable;

import javax.net.ssl.SSLException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class WebSocketServer {
    static final boolean SSL = System.getProperty("ssl") != null;

    private static final String WEBSOCKET_URI = "/ws";
    private final Random random = new SecureRandom();

    private final int port;
    private final List<Bullet> bullets;
    private final ChannelMap<Player> channelMap;
    private final ConcurrentLinkedQueue<Message> eventBus;
    private final Queue<Message> outgoingMessages = new ArrayDeque<>();

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
        executorService.scheduleAtFixedRate(
                new VerboseRunnable(
                        new GameLoop(bullets, channelMap, eventBus, outgoingMessages)
                ), 0, 17, TimeUnit.MILLISECONDS
        );

        executorService.scheduleAtFixedRate(new VerboseRunnable(() -> {
            while (!outgoingMessages.isEmpty()) {
                Message poll = outgoingMessages.poll();
                channelMap.writeAndFlush(new BinaryWebSocketFrame(Unpooled.directBuffer(1).setBytes()));
            }
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
