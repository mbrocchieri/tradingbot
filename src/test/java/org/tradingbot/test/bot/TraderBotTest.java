package org.tradingbot.test.bot;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.ta4j.core.num.Num;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.bot.LiveTradingBot;
import org.tradingbot.common.bot.StockReload;
import org.tradingbot.common.bot.action.TradingAction;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordEntity;
import org.tradingbot.common.persistence.TradingRecordSummaryEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.YahooProvider;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.util.FinancialProviderFile;
import org.tradingbot.util.FinancialProviderLive;
import org.tradingbot.util.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.Month.JANUARY;
import static java.time.Month.JUNE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tradingbot.common.Constants.UTC;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.CLOSE_MARKET;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.OPEN_MARKET;
import static org.tradingbot.util.Utils.getFile;

public class TraderBotTest extends TradingBotApplicationTests {

    @Test
    public void testEmptyDatabase() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        LiveTradingBot bot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            public void runAllConfig(StockReload stockReload) {
                super.runAllConfig(stockReload);
                latch.countDown();
            }
        };// Lock nothing for test
        Thread t = new Thread(bot::run);
        t.start();
        latch.await();
        bot.stop();
    }

    /**
     * test there is no runtime exception on a case close to what happen in prod
     */
    @Disabled("YahooProvider deprecated")
    @Test
    public void testNominal() throws InterruptedException, IOException {

        var from = ZonedDateTime.of(LocalDateTime.of(2020, JANUARY, 1, 0, 0, 0), UTC);
        var to = ZonedDateTime.of(LocalDateTime.of(2021, JANUARY, 1, 0, 0, 0), UTC);

        var providerId = YahooProvider.PROVIDER_ID;
        var stockEntity = (StockBean) stockController.createStock(new StockCreateBean(providerId, "GOOG"));
        stockController.refreshCandles(stockEntity.getId(), Interval.DAILY, providerId, new RefreshBean(from, to));

        CountDownLatch latch = new CountDownLatch(5);
        LiveTradingBot bot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            public void runAllConfig(StockReload stockReload) {
                super.runAllConfig(stockReload);
                latch.countDown();
            }
        };
        Thread t = new Thread(bot::run);
        t.start();
        assertTrue(latch.await(10, TimeUnit.MINUTES));
        bot.stop();
    }

    /**
     * test there is no runtime exception on a case close to what happen in prod
     */
    @Test
    public void testNominalDaily() throws IOException {
        var providerId = -2;

        var from = ZonedDateTime.now().minusYears(3);
        var to = ZonedDateTime.now();
        var lastDate = ZonedDateTime.of(LocalDateTime.of(2021, JANUARY, 18, 0, 0, 0), UTC);
        FinancialProvider provider = new FinancialProviderLive(
                new FinancialProviderFile(providerId, Utils.getFile("AMUN.csv"), "AMUN"), lastDate);
        ProvidersSingletonUtils.addProvider("test", provider, providerRepository);
        var stockEntity = stockController.createStock(new StockCreateBean(providerId, "AMUN"));
        stockController.refreshCandles(stockEntity.getId(), Interval.DAILY, providerId, new RefreshBean(from, to));

        StrategyEntity strategyEntity = getStrategyEntity();
        var stock = stockRepository.findById(stockEntity.getId()).orElseThrow();
        TradingConfigEntity configEntity = tradingConfigRepository.findAllByStockAndStrategy(stock, strategyEntity)
                .iterator().next();

        List<String> enterList = new ArrayList<>();
        List<String> exitList = new ArrayList<>();

        TradingAction tradingAction = new TradingAction() {
            @Override
            public void exit(String strategyName, String symbol, Num closePrice) {
                exitList.add(strategyName + "_" + symbol + "_" + closePrice.toString());
            }

            @Override
            public void enter(String strategyName, String symbol, Num closePrice) {
                enterList.add(strategyName + "_" + symbol + "_" + closePrice.toString());
            }

            @Override
            public void sendStockCandlesCompareError(String symbol) {
                throw new IllegalStateException();
            }

            @Override
            public void sendConfigError(int id) {
                throw new IllegalStateException();
            }
        };

        LiveTradingBot bot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            public void setTradingAction(TradingAction tradingAction) {
                super.setTradingAction(tradingAction);
            }

            @Override
            protected FinancialProvider getProvider() {
                return provider;
            }
        };
        bot.setTradingAction(tradingAction);
        bot.runAllConfig(new StockReload(provider, stockController));
        List<TradingRecordEntity> allTrading = tradingRecordRepository.findAllByConfig(configEntity);
        assertEquals(0, enterList.size());
        assertEquals(1, exitList.size());
        assertEquals(44, allTrading.size());

        enterList.clear();
        exitList.clear();
        bot.runAllConfig(new StockReload(provider, stockController));
        allTrading = tradingRecordRepository.findAllByConfig(configEntity);
        assertEquals(0, enterList.size());
        assertEquals(0, exitList.size());
        assertEquals(44, allTrading.size());

        configEntity.setStartTime(ZonedDateTime.now().minusMonths(2));
        tradingConfigRepository.save(configEntity);

        enterList.clear();
        exitList.clear();
        bot.runAllConfig(new StockReload(provider, stockController));
        allTrading = tradingRecordRepository.findAllByConfig(configEntity);
        assertEquals(0, enterList.size());
        assertEquals(0, exitList.size());
        assertEquals(44, allTrading.size());
        bot.stop();
    }

    /**
     * Simulate several updates in one day, make sure there is no several times the
     * same message
     */
    @Test
    @Timeout(10_000)
    public void testActionNotSendInDouble() throws IOException, InterruptedException {

        var providerId = -2;
        List<String> exit = new ArrayList<>();
        List<String> enter = new ArrayList<>();
        List<String> sendStockCandlesCompareError = new ArrayList<>();
        List<Integer> sendConfigError = new ArrayList<>();

        TradingAction action = new TradingAction() {
            @Override
            public void exit(String strategyName, String symbol, Num closePrice) {
                exit.add(strategyName + " " + symbol + " " + closePrice.toString());
            }

            @Override
            public void enter(String strategyName, String symbol, Num closePrice) {
                enter.add(strategyName + " " + symbol + " " + closePrice.toString());
            }

            @Override
            public void sendStockCandlesCompareError(String symbol) {
                sendStockCandlesCompareError.add(symbol);
            }

            @Override
            public void sendConfigError(int id) {
                sendConfigError.add(id);
            }
        };

        var from = ZonedDateTime.of(LocalDate.now().minus(1, ChronoUnit.MONTHS), MIDNIGHT, UTC);
        var originalTo = ZonedDateTime.of(LocalDate.of(2021, JUNE, 1), MIDNIGHT, UTC);
        var to = ZonedDateTime.of(LocalDate.now(), MIDNIGHT, UTC);
        List<Candle> daysCandles = new ArrayList<>();
        daysCandles.add(new Candle.Builder().open(new BigDecimal("1.0156")).high(new BigDecimal("1.0640"))
                .close(new BigDecimal("1.0450")).adjClose(new BigDecimal("0.6154")).low(new BigDecimal("1.0090"))
                .volume(45000L).date(ZonedDateTime.of(LocalDate.now(), MIDNIGHT, UTC)).build());

        FinancialProvider provider = new FinancialProviderLive(
                new FinancialProviderFile(providerId, Utils.getFile("CGG.csv"), "CGG"), originalTo) {
            boolean first = true;

            @Override
            public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to,
                    Interval interval) throws IOException {
                var candles = super.getCandles(symbol, from, to, interval);
                if (!first) {
                    if (!candles.isEmpty()) {
                        candles.set(candles.size() - 1, daysCandles.get(0));
                    }
                } else {
                    first = false;
                }
                return candles;
            }
        };

        ProvidersSingletonUtils.addProvider("test", provider, providerRepository);
        var stockEntity = (StockBean) stockController.createStock(new StockCreateBean(providerId, "CGG"));
        stockController.refreshCandles(stockEntity.getId(), Interval.DAILY, providerId, new RefreshBean(from, to));

        StrategyEntity strategyEntity = getStrategyEntity();
        tradingConfigRepository.deleteAll();
        var stock = stockRepository.findById(stockEntity.getId()).orElseThrow();
        TradingConfigEntity configEntity = new TradingConfigEntity(strategyEntity, stock, true,
                ZonedDateTime.of(LocalDate.now().minus(30, ChronoUnit.DAYS), MIDNIGHT, UTC), Interval.DAILY);
        tradingConfigController.createTradingConfig(configEntity);

        CountDownLatch latch = new CountDownLatch(10);
        LiveTradingBot bot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            protected FinancialProvider getProvider() {
                return provider;
            }

            @Override
            public void runAllConfig(StockReload stockReload) {
                super.runAllConfig(stockReload);
                latch.countDown();
            }
        };
        bot.setTradingAction(action);
        Thread t = new Thread(bot::run);
        t.start();
        latch.await();
        bot.stop();

        assertEquals(1, enter.size());
        assertTrue(exit.isEmpty());
        assertTrue(sendStockCandlesCompareError.isEmpty());
        assertTrue(sendConfigError.isEmpty());
    }

    // TODO fix test
    // @Test
    public void testTradingPeriodOnPastCandles() throws InterruptedException, IOException {

        final var providerId = -2;
        final var financialProvider = new FinancialProviderFile(providerId, getFile("CGG.csv"), "CGG");
        ProvidersSingletonUtils.addProvider("test", financialProvider, providerRepository);
        final var from = ZonedDateTime.parse("2021-01-04T00:00:00+00:00");
        var to = ZonedDateTime.parse("2021-01-31T00:00:00+00:00");

        var stockEntity = (StockBean) stockController.createStock(new StockCreateBean(providerId, "CGG"));
        stockController.refreshCandles(stockEntity.getId(), Interval.DAILY, providerId, new RefreshBean(from, to));
        getStrategyEntity("open", OPEN_MARKET);
        getStrategyEntity("close", CLOSE_MARKET);

        CountDownLatch latch = new CountDownLatch(1);
        LiveTradingBot bot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            public void runAllConfig(StockReload stockReload) {
                super.runAllConfig(stockReload);
                latch.countDown();
            }

            @Override
            protected FinancialProvider getProvider() {
                return financialProvider;
            }
        };
        Thread t = new Thread(bot::run);
        t.start();
        assertTrue(latch.await(10, TimeUnit.MINUTES));
        bot.stop();

        var allConfigs = tradingConfigRepository.findAll();
        assertEquals(2, allConfigs.size());
        var config1 = allConfigs.get(0);
        var config2 = allConfigs.get(1);

        List<TradingRecordSummaryEntity> records1 = new ArrayList<>(
                tradingRecordSummaryRepository.findAllByConfig(config1));
        List<TradingRecordSummaryEntity> records2 = new ArrayList<>(
                tradingRecordSummaryRepository.findAllByConfig(config2));

        assertEquals(16, records1.size());
        assertEquals(16, records2.size());

        records1.sort(Comparator.comparing(TradingRecordSummaryEntity::getBuyCandleStartTime));
        records2.sort(Comparator.comparing(TradingRecordSummaryEntity::getBuyCandleStartTime));

        assertEqualsBd(new BigDecimal("-0.57"), records1.get(0).getPercentPerformance());
        assertEqualsBd(new BigDecimal("-0.57"), records2.get(0).getPercentPerformance());

    }
}
