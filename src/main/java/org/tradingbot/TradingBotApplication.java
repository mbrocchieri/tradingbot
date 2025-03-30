package org.tradingbot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.tradingbot.alert.WebSocketHandler;
import org.tradingbot.bot.BotComponent;
import org.tradingbot.common.persistence.MarketEntity;
import org.tradingbot.common.provider.YahooProvider;
import org.tradingbot.stock.MarketRepository;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
public class TradingBotApplication {

    @Autowired
    BotComponent botComponent;

    @Autowired
    MarketRepository marketRepository;

    @Autowired
    WebSocketHandler webSocketHandler;

    public static void main(String[] args) {
        SpringApplication.run(TradingBotApplication.class, args);
    }

    @PostConstruct
    void started() {
        // set JVM timezone as UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        botComponent.run();

        createIfNotExits(YahooProvider.EURONEXT_PARIS_MARKET_ID, "Euronext Paris", LocalTime.parse("09:00:00"), LocalTime.parse("17:30:00"), "Europe/Paris");
        createIfNotExits(YahooProvider.EURONEXT_AMSTERDAM_MARKET_ID, "Euronext Amsterdam", LocalTime.parse("09:00:00"), LocalTime.parse("17:30:00"), "Europe/Paris");
        createIfNotExits(YahooProvider.EURONEXT_BRUSSELS_MARKET_ID, "Euronext Brussels", LocalTime.parse("09:00:00"), LocalTime.parse("17:30:00"), "Europe/Paris");
        createIfNotExits(YahooProvider.NASDAQ_MARKET_ID, "Nasdaq", LocalTime.parse("15:30:00"), LocalTime.parse("22:00:00"), "Europe/Paris");
        createIfNotExits(YahooProvider.XETRA_ID, "XETRA", LocalTime.parse("09:00:00"), LocalTime.parse("17:30:00"), "Europe/Paris");
        createIfNotExits(YahooProvider.CRYPTO, "Crypto market", LocalTime.parse("00:00:00"), LocalTime.parse("00:00:00"), "Europe/Paris");
        createIfNotExits(YahooProvider.NMS, "NMS", LocalTime.parse("00:00:00"), LocalTime.parse("00:00:00"), "Europe/Paris");
    }

    private void createIfNotExits(int marketId, String name, LocalTime startTime, LocalTime endTime, String timezone) {
        if (marketRepository.findById(marketId).isEmpty()) {
            MarketEntity marketEntity = new MarketEntity(marketId, name, startTime, endTime, timezone);
            marketRepository.save(marketEntity);
        }
    }

}
