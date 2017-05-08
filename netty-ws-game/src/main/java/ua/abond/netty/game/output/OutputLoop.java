package ua.abond.netty.game.output;

import io.netty.buffer.ByteBufAllocator;
import lombok.Setter;
import ua.abond.netty.game.exception.VerboseRunnable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Setter
public class OutputLoop {
    private final ScheduledExecutorService eventLoopGroup;
    private final ByteBufAllocator allocator;

    private int delay = 0;
    private int period = 33;
    private OutputCallback outputCallback;

    public OutputLoop(ScheduledExecutorService executorService, ByteBufAllocator allocator) {
        this.eventLoopGroup = executorService;
        this.allocator = allocator;
    }

    public void start() {
        eventLoopGroup.scheduleAtFixedRate(
                new VerboseRunnable(() -> outputCallback.call(allocator)),
                delay, period, TimeUnit.MILLISECONDS
        );
    }
}
