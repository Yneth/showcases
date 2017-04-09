package ua.abond.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.CharsetUtil;

public class Server {
    private static final String WEBSOCKET_PATH = "/websocket";

    public void start() {
        EventLoopGroup slave = new NioEventLoopGroup();
        EventLoopGroup master = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap()
                .group(master, slave)
                .channel(NioServerSocketChannel.class);
        server.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
                pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
                pipeline.addLast(new WebSocketFrameHandler());
            }
        });

        Channel channel;
        try {
            channel = server.bind("localhost", 8082).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            master.shutdownGracefully();
            slave.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }

    private class WebServerChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("echo-handler", new SimpleChannelInboundHandler<ByteBuf>() {

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    System.out.println(msg.toString(CharsetUtil.UTF_8));
                    ctx.writeAndFlush(msg.retain());
                }
            });
        }
    }
}
