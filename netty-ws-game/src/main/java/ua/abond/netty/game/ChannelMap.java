package ua.abond.netty.game;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import lombok.experimental.Delegate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class ChannelMap<E> implements ChannelGroup {
    @Delegate
    private final ChannelGroup channels;
    private final ConcurrentMap<ChannelId, E> userData;

    public ChannelMap(ChannelGroup channels) {
        this(channels, new ConcurrentHashMapV8<>());
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

    public void remove(ChannelId channelId) {
        channels.remove(channels.find(channelId));
        userData.remove(channelId);
    }

    public ChannelId find(E e) {
        for (Map.Entry<ChannelId, E> channelIdEEntry : userData.entrySet()) {
            if (channelIdEEntry.getValue().equals(e)) {
                return channelIdEEntry.getKey();
            }
        }
        return null;
    }

    public Collection<E> values() {
        return userData.values();
    }
}
