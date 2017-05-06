package ua.abond.netty.game.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Platform {

    public static Class<? extends ServerSocketChannel> serverSocketChannelType() {
        return Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static EventLoopGroup createLoopGroup() {
        return Epoll.isAvailable() ?
                new EpollEventLoopGroup() :
                new NioEventLoopGroup();
    }

    public static EventLoopGroup createLoopGroup(int threadCount) {
        return Epoll.isAvailable() ?
                new EpollEventLoopGroup(threadCount) :
                new NioEventLoopGroup(threadCount);
    }
}
