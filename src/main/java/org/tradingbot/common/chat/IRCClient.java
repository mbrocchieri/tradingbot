package org.tradingbot.common.chat;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.IrcException;
import org.tradingbot.common.TradingBotConfig;

import java.io.IOException;

public class IRCClient extends ChatClient {
    private final IrcBot bot;

    public IRCClient(TradingBotConfig config) throws IOException, IrcException {
        super();
        this.bot = new IrcBot(config);
    }

    @Override
    public void sendMessage(String message) {
        for (String s : StringUtils.split(message, '\n'))
        bot.sendMessage(bot.getChannel(), s);
    }

    @Override
    public void close() {
        super.close();
        bot.disconnect();
    }
}
