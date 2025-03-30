package org.tradingbot.common.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ChatClient implements AutoCloseable {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
    }

    protected ChatClient() {

    }

    public abstract void sendMessage(String message);

    @Override
    public void close() {
        // nothing to close
    }
}
