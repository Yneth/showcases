package ua.abond.netty.game.output;

import io.netty.buffer.ByteBufAllocator;

@FunctionalInterface
public interface OutputCallback {
    void call(ByteBufAllocator allocator);
}
