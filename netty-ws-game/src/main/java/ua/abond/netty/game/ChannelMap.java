package ua.abond.netty.game;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import lombok.experimental.Delegate;

public class ChannelMap<E> implements ChannelGroup {
    @Delegate
    private final ChannelGroup channels;
    private final ConcurrentMap<ChannelId, E> userData;

    public ChannelMap(ChannelGroup channels) {
        this(channels, new ConcurrentSkipListMap<>());
    }

    public ChannelMap(ChannelGroup channels, ConcurrentMap<ChannelId, E> data) {
        this.channels = channels;
        this.userData = data;
    }

    public E get(Channel channel) {
        return userData.get(channel.id());
    }

    public void put(Channel channel, E data) {
        channels.add(channel);
        userData.put(channel.id(), data);
    }

    public void remove(Channel channel) {
        channels.remove(channel);
        userData.remove(channel.id());
    }
}
