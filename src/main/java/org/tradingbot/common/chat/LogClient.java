package org.tradingbot.common.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogClient extends ChatClient {
    private static final Logger LOG = LoggerFactory.getLogger(LogClient.class);

    @Override
    public void sendMessage(String message) {
        LOG.info(message);
    }
}
