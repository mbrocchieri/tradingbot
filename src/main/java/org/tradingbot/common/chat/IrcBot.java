package org.tradingbot.common.chat;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.tradingbot.common.TradingBotConfig;

import java.io.IOException;

public class IrcBot extends PircBot {

    TradingBotConfig config;

    public IrcBot(TradingBotConfig config) throws IOException, IrcException {
        this.config = config;
        setName(config.getIrcBotName());
        setVerbose(config.isIrcBotDebug()); // for debug purpose
        connect(config.getIrcHostName());
        joinChannel(getChannel());
    }

    public String getChannel() {
        return config.getIrcBotChannel();
    }
}
