package org.tradingbot.common.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Supplier;

public class RunningFlag implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(RunningFlag.class);
    private final File f;
    private final Thread t;
    private final Supplier<Long> waitTimeSupplier;

    /**
     * @param f                file used as flag
     * @param t                the thread that must run when flag file is deleted
     * @param waitTimeSupplier time between 2 tests in ms. It is supplier for properties to be taken into account
     *                         without
     *                         restarting tradingbot
     * @throws IOException if IO exceptions
     */
    public RunningFlag(File f, Thread t, Supplier<Long> waitTimeSupplier) throws IOException {
        this.waitTimeSupplier = waitTimeSupplier;
        this.f = f;
        if (!f.createNewFile()) {
            throw new IOException("Can not create file " + f.getAbsolutePath());
        }
        // add shutdown hook, to make sure that if the process is killed, flag file is deleted
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        LOG.info("running flag is file : {}", f.getAbsolutePath());
        this.t = Objects.requireNonNull(t);
    }

    /**
     * Starts thread
     */
    public void run() {
        t.start();
        while (t.isAlive() && f.exists()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread alive : {}, file exists : {}", t.isAlive(), f.exists());
            }
            await();
        }
        if (t.isAlive()) {
            t.interrupt();
        }
    }

    protected void await() {
        try {
            Thread.sleep(waitTimeSupplier.get());
        } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
        }
    }

    @Override
    public void close() {
        if (f.exists()) {
            try {
                Files.delete(f.toPath());
            } catch (IOException e) {
                LOG.error("Error deleting file {}", f.getAbsolutePath(), e);
            }
        }
    }
}
