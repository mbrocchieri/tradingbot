package org.tradingbot.common.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.TradingBotConfig;
import org.tradingbot.common.flag.PeriodicWaitFlag;
import org.tradingbot.common.persistence.ProviderEntity;
import org.tradingbot.stock.ProviderRepository;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

@Service
public class YahooProvider implements FinancialProvider {
    public static final int PROVIDER_ID = 1;
    public static final int EURONEXT_PARIS_MARKET_ID = 1;
    public static final int EURONEXT_AMSTERDAM_MARKET_ID = 2;
    public static final int EURONEXT_BRUSSELS_MARKET_ID = 3;
    public static final int NASDAQ_MARKET_ID = 11;
    public static final int XETRA_ID = 12;
    public static final int NMS = 13;
    public static final int CRYPTO = 20;
    private static final Logger LOG = LoggerFactory.getLogger(YahooProvider.class);
    /**
     * it seems a lot of requests to Yahoo makes 401 (not authorized) http response, waitFlag is to slow down
     */
    private final PeriodicWaitFlag waitFlag;
    @Autowired
    private final TradingBotConfig tradingBotConfig;

    public static final Map<String, Integer> marketCodeToId = Map.of("PAR", EURONEXT_PARIS_MARKET_ID, "Amsterdam", EURONEXT_AMSTERDAM_MARKET_ID, "Brussels",
            EURONEXT_BRUSSELS_MARKET_ID, "NasdaqGS", NASDAQ_MARKET_ID, "XETRA", XETRA_ID, "CCC", CRYPTO,
            "NMS", NMS);

    public YahooProvider(TradingBotConfig config, ProviderRepository providerRepository) {
        this.tradingBotConfig = config;
        waitFlag = new PeriodicWaitFlag(config::getWaitBetweenYahooRequests);
        var providerFromDatabase = providerRepository.findById(PROVIDER_ID);
        if (providerFromDatabase.isEmpty()) {
            var provider = new ProviderEntity();
            provider.setId(1);
            provider.setName("Yahoo");
            providerRepository.save(provider);
        }
        
    }

    @Override
    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval)
            throws IOException {
        waitFlag.waitFlag();
        if (interval.equals(Interval.DAILY) && from.toLocalDate().compareTo(to.toLocalDate()) == 0) {
            from = ZonedDateTime.of(from.toLocalDate(), LocalTime.MIN, UTC).minusMinutes(1);
        }
        requireNonNull(symbol);
        requireNonNull(from);
        requireNonNull(to);
        requireNonNull(interval);

        try {
            return YahooAPI.getCandles(symbol, from, to, interval);
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can not find {} from {} to {} interval {}", symbol, from, to, interval, e);
            } else {
                LOG.warn("Can not find {} from {} to {} interval {}", symbol, from, to, interval);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public StockData getStockData(String providerCode) throws IOException {
        waitFlag.waitFlag();
        return YahooAPI.getStockData(providerCode, marketCodeToId);
    }

    @Override
    public int getProviderId() {
        return 1;
    }
}
