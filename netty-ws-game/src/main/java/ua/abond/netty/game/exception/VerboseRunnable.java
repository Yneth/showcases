package ua.abond.netty.game.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerboseRunnable implements Runnable {
    private final Runnable runnable;

    public VerboseRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("Exception during invocation of run method", e);
        }
    }
}
