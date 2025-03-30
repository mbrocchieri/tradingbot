package org.tradingbot.common.flag;

import java.time.Duration;
import java.util.function.Supplier;

public class PeriodicWaitFlag implements Flag {

    private final Supplier<Duration> durationSupplier;
    private Long start;

    /**
     * @param durationSupplier duration supplier to allow properties live update
     *                         taken into account without restarting application
     */
    public PeriodicWaitFlag(Supplier<Duration> durationSupplier) {
        this.durationSupplier = durationSupplier;
    }

    @Override
    public void waitFlag() {
        if (start != null) {
            final var waitTime = durationSupplier.get().toMillis() - (System.currentTimeMillis() - start);
            if (waitTime > 0) {
                long nbWait = waitTime / 1000;
                // For is here to not wait to long when tomcat stops
                for (long i = 0; i < nbWait && !Thread.currentThread().isInterrupted(); i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Thread interrupted", e);
                    }
                }
            }
        }
        start = System.currentTimeMillis();
    }
}
