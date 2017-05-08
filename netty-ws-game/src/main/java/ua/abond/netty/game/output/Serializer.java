package ua.abond.netty.game.output;

import io.netty.buffer.ByteBuf;

public interface Serializer<A> {

    ByteBuf serialize(A a, ByteBuf out);
}
