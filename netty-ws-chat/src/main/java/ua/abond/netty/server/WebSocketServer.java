package ua.abond.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServer {
    private static final String LOCALHOST = "127.0.0.1";
    private static final String WEBSOCKET_URI = "/websocket";

    private final int port;
    private final ChannelGroup channelGroup;

    public WebSocketServer(int port) {
        this(port, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    public WebSocketServer(int port, ChannelGroup channelGroup) {
        this.port = port;
        this.channelGroup = channelGroup;
    }

    public void start() {
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(master, slave)
                .channel(NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_URI, null, true));
                pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_URI));
                pipeline.addLast(new WebSocketServerHandler(channelGroup));
            }
        });
        Channel channel;
        try {
            channel = bootstrap.bind(port).channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Failed to close channel.", e);
            Thread.currentThread().interrupt();
        } finally {
            slave.shutdownGracefully();
            master.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new WebSocketServer(8082).start();
    }
}
