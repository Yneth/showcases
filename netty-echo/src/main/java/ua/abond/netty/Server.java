package ua.abond.netty;

import java.net.SocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class Server {

    public void start() {
        EventLoopGroup slave = new NioEventLoopGroup();
        EventLoopGroup master = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap()
                .group(master, slave)
                .channel(NioServerSocketChannel.class);
        server.childHandler(new WebServerChannelInitializer());

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

            pipeline.addLast("echo-handler", new ChannelInboundHandler() {

                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelRegistered");
                }

                @Override
                public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelUnregistered");
                }

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelActive");
                    ctx.writeAndFlush("Hi from channel active inbound");
                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelInactive");
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    System.out.println("channelRead");
                    ctx.writeAndFlush(msg);
                }

                @Override
                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelReadComplete");
                }

                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                    System.out.println("userEventTriggered");
                }

                @Override
                public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("channelWritabilityChanged");
                }

                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("handlerAdded");
                }

                @Override
                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("handlerRemoved");
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    System.out.println("exceptionCaught");
                }
            });
            pipeline.addLast("outbound", new ChannelOutboundHandler() {
                @Override
                public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                    System.out.println("bind");
                }

                @Override
                public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                    System.out.println("connect");
                    ctx.writeAndFlush("Hi from outbound");
                }

                @Override
                public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                    System.out.println("disconnect");
                }

                @Override
                public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                    System.out.println("close");
                }

                @Override
                public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                    System.out.println("deregister");
                }

                @Override
                public void read(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("read");
                }

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    System.out.println("write");
                    ctx.writeAndFlush(msg);
                }

                @Override
                public void flush(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("flush");
                    ctx.flush();
                }

                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("handlerAdded");
                }

                @Override
                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                    System.out.println("handlerRemoved");
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    System.out.println("exceptionCaught");
                }
            });
        }
    }
}
