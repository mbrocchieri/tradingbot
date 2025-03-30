package org.tradingbot.test.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.tradingbot.common.bot.RunningFlag;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RunningFlagTest {
    /**
     * Test the file is created and deleted
     * Test the loop
     */
    @Test
    @Timeout(4000)
    public void testNominal() throws IOException {
        final var t = new Thread(() -> {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        AtomicInteger i = new AtomicInteger();
        final var f = new File(System.getProperty("java.io.tmpdir") + File.separator + "tradingBotTest");
        assertFalse(f.exists());
        try (RunningFlag runningFlag = new RunningFlag(f, t, () -> 100L) {
            @Override
            protected void await() {
                i.incrementAndGet();
                super.await();
            }
        }) {
            assertTrue(f.exists());
            runningFlag.run();
        }
        assertFalse(f.exists());
        assertTrue(i.get() == 2 || i.get() == 3);
    }
}
