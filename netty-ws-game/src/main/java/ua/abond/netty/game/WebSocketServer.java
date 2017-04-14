package ua.abond.netty.game;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import ua.abond.netty.game.domain.Player;
import ua.abond.netty.game.domain.Vector2;

@Slf4j
public class WebSocketServer {
    private static final String WEBSOCKET_URI = "/ws";

    private final int port;
    private final ChannelMap<Player> channelMap;

    public WebSocketServer(int port) {
        this.port = port;
        this.channelMap = new ChannelMap<>(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    public void start() {
        EventLoopGroup master = new NioEventLoopGroup(2);
        EventLoopGroup slave = new NioEventLoopGroup(4);

        ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(master, slave);

        master.scheduleAtFixedRate(() -> {
            final float deltaTime = 17.0f / 1000.0f;
            final float speed = 100.0f;
            for (Player player : channelMap.values()) {
                Vector2 direction = player.getPosition().clone().minus(player.getTarget());
                if (Math.abs(direction.getX()) < 0.001f && Math.abs(direction.getY()) < 0.001f) {
                    continue;
                }
                Vector2 velocity = direction.multiply(speed).multiply(deltaTime);

                player.getPosition().add(velocity);
            }
        }, 0, 17, TimeUnit.MILLISECONDS);

        slave.scheduleAtFixedRate(() -> {
            String userPositions = channelMap.values().stream()
                    .map(Player::getPosition)
                    .map(pos -> pos.getX() + "," + pos.getY())
                    .collect(Collectors.joining(";"));

            channelMap.writeAndFlush(new TextWebSocketFrame("0:" + userPositions));
        }, 0, 33, TimeUnit.MILLISECONDS);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_URI, null, true));
                pipeline.addLast(new WebSocketServerHandler(channelMap));
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
}
