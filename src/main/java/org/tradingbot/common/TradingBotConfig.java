package org.tradingbot.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.tradingbot.common.chat.ChatEnum;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class TradingBotConfig {
    @Value("${yahoo.write.candles.folder:#{null}}")
    String yahooWriteFolder;

    @Value("${compare.dabatabase.to.providerdata:true}")
    boolean compareDatabaseToProvider;

    @Value("${chatclient:LOG}")
    ChatEnum sendDataTo;

    @Value("${discord.channel:bot}")
    String discordChannel;

    @Value("${discord.token:#{null}}")
    String discordToken;

    @Value("${tradingbot.yahoo.waitbetweenrequest:1000}")
    long yahooWaitBetweenRequests;

    @Value("${tradingbot.waitbetweenanalyses:5}")
    long waitBetweenAnalyses;

    @Value("${irc.botname:#{null}}")
    String ircBotName;

    @Value("${irc.host.url:#{null}}")
    String ircHostUrl;

    @Value("${irc.channel:#{null}}")
    String ircChannel;

    @Value("${irc.debug:false}")
    boolean ircDebug;

    public Optional<String> getWriteFolder() {
        return Optional.ofNullable(yahooWriteFolder);
    }

    public boolean isCompareDatabaseToProvider() {
        return compareDatabaseToProvider;
    }

    public ChatEnum getChatClient() {
        return sendDataTo;
    }

    public String getDiscordChannelName() {
        return discordChannel;
    }

    public String getDiscordToken() {
        return Objects.requireNonNull(discordToken);
    }

    public Duration getWaitBetweenYahooRequests() {
        return Duration.ofMillis(yahooWaitBetweenRequests);
    }


    public String getIrcBotName() {
        return Objects.requireNonNull(ircBotName);
    }

    public String getIrcHostName() {
        return Objects.requireNonNull(ircHostUrl);
    }

    public String getIrcBotChannel() {
        return Objects.requireNonNull(ircChannel);
    }

    public boolean isIrcBotDebug() {
        return ircDebug;

    }

    /**
     * @return 5 minutes if property is not set
     */
    public Duration getWaitBetweenAnalyses() {
        return Duration.ofMinutes(waitBetweenAnalyses);
    }
}
