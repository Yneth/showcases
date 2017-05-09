package ua.abond.netty.game.output.loop;

import io.netty.buffer.ByteBufAllocator;
import lombok.Setter;
import ua.abond.netty.game.exception.VerboseRunnable;
import ua.abond.netty.game.output.OutputCallback;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Setter
public class OutputLoopBootstrap {
    private final ScheduledExecutorService eventLoopGroup;
    private final ByteBufAllocator allocator;

    private int delay = 0;
    private int period = 33;
    private OutputCallback outputCallback;

    public OutputLoopBootstrap(ScheduledExecutorService executorService, ByteBufAllocator allocator) {
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
