package org.tradingbot.bot;

import org.jetbrains.annotations.NotNull;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.tradingbot.advice.AdviceService;
import org.tradingbot.alert.WebSocketHandler;
import org.tradingbot.common.provider.YahooProvider;
import org.tradingbot.common.repository.Repositories;
import org.tradingbot.common.TradingBotConfig;
import org.tradingbot.common.bot.LiveTradingBot;
import org.tradingbot.common.bot.action.SendChatAction;
import org.tradingbot.common.chat.*;
import org.tradingbot.common.flag.PeriodicWaitFlag;
import org.tradingbot.stock.StockController;

import java.io.IOException;

@Service
public class BotComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BotComponent.class);

    @Autowired
    protected Repositories repositories;

    @Autowired
    protected StockController stockController;

    @Autowired
    protected TradingBotConfig tradingBotConfig;

    @Autowired
    protected WebSocketHandler webSocketHandler;

    @Autowired
    protected AdviceService adviceService;

    // needed for initialization in singleton FIXME trouver une autre solution
    @Autowired
    protected YahooProvider yahooProvider;

    private void startBot(ChatClient chatClient) {
        LiveTradingBot bot = null;
        try {

            final var tradingAction = new SendChatAction(chatClient);
            final var waitDataFlag = new PeriodicWaitFlag(tradingBotConfig::getWaitBetweenAnalyses);
            bot = new LiveTradingBot(repositories, stockController, adviceService);
            bot.setWaitDataFlag(waitDataFlag);
            bot.setTradingAction(tradingAction);
            bot.run();
            throw new BeanInitializationException("something wrong");
        } catch (RuntimeException e) {
            LOG.error("Not expected fatal exception", e);
        } finally {
            if (bot != null) {
                bot.stop();
            }
        }
    }

    @Async
    public void run() {
        LOG.info("start bot with stocks");
        ChatClient chatClient;
        try {
            chatClient = getChatClient();
        } catch (Exception e) {
            throw new BeanInitializationException("Error initializing chat client", e);
        }
        startBot(chatClient);
    }

    @NotNull
    private ChatClient getChatClient() throws DiscordException, IOException, IrcException {
        switch (tradingBotConfig.getChatClient()) {
            case DISCORD:
                LOG.info("Send trading actions on discord channel {}", tradingBotConfig.getDiscordChannelName());
                return new DiscordClient(tradingBotConfig.getDiscordChannelName(), tradingBotConfig.getDiscordToken());
            case IRC:
                LOG.info("Send trading actions on irc");
                return new IRCClient(tradingBotConfig);
            case LOG:
                LOG.info("Put trading actions in log");
                return new LogClient();
            case WEB_SERVICE:
                LOG.info("Send trading actions to webservice");
                return new WebServerClient(webSocketHandler);
            default:
                LOG.info("Put trading actions in log (default)");
                return new LogClient();
        }
    }
}
