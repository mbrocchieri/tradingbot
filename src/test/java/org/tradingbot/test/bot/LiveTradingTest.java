package org.tradingbot.test.bot;

import static java.time.LocalDateTime.of;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.Month.JANUARY;
import static java.time.Month.JUNE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tradingbot.common.Constants.UTC;
import static org.tradingbot.common.Interval.DAILY;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.CLOSE_MARKET;
import static org.tradingbot.util.Strategies.BASIC_RSI;
import static org.tradingbot.util.Strategies.BASIC_RSI_PARAMETERS;
import static org.tradingbot.util.Strategies.RSI5050;
import static org.tradingbot.util.Strategies.RSI5050_PARAMETERS;
import static org.tradingbot.util.Utils.getFile;
import static org.tradingbot.util.Utils.load;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.ta4j.core.num.Num;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.advice.AdviceCategoryBean;
import org.tradingbot.advice.AdviceCategoryCreationBean;
import org.tradingbot.advice.AdviceCreationBean;
import org.tradingbot.advice.AdviserBean;
import org.tradingbot.advice.AdviserCreationBean;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.bot.LiveTradingBot;
import org.tradingbot.common.bot.StockReload;
import org.tradingbot.common.bot.Trader;
import org.tradingbot.common.bot.period.OpenMarketTrading;
import org.tradingbot.common.bot.action.TradingAction;
import org.tradingbot.common.bot.period.TradingPeriodEnum;
import org.tradingbot.common.math.Decimal;
import org.tradingbot.common.persistence.AdviceEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.strategy.Strategy;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.RestBarSeries;
import org.tradingbot.stock.RestTradingRecord;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.strategy.StrategyCreationBean;
import org.tradingbot.strategy.StrategyParameterCreationBean;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.util.FinancialProviderFile;
import org.tradingbot.util.FinancialProviderLive;
import org.tradingbot.util.PaperTradingFinancialProvider;
import org.tradingbot.util.Strategies;
import org.tradingbot.util.Utils;

public class LiveTradingTest extends TradingBotApplicationTests {


    @Test
    public void testUpdate1Candle() throws IOException {
        var providerId = -2;
        var financialProvider = new PaperTradingFinancialProvider(providerId);
        financialProvider.setCandles(load(getFile("FDJ.PA.csv")));

        ZonedDateTime from = ZonedDateTime.parse("2019-01-01T00:00:00+00:00");
        ZonedDateTime to = ZonedDateTime.parse("2021-01-01T00:00:00+00:00");

        ProvidersSingletonUtils.addProvider("Paper", financialProvider, providerRepository);
        var stockEntity = (StockBean) stockController.createStock(new StockCreateBean(providerId, "A"));
        assertNotNull(stockEntity);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));
        var barSeries = new RestBarSeries(stockController, stockEntity.getId());
        new Trader(barSeries, new Strategy("rsi_30_70", BASIC_RSI, BASIC_RSI_PARAMETERS));

        var candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(2, candles.size());

        financialProvider.setCandles(Collections.singletonList(
                new Candle.Builder().date(ZonedDateTime.parse("2020-07-01T00:00:00+00:00")).open(new Decimal("27.5"))
                        .high(new Decimal("27.98")).low(new Decimal("27.059999")).close(new Decimal("27.799999"))
                        .adjClose(new Decimal("27.301195")).volume(363380L).build()));
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2,
                new RefreshBean(ZonedDateTime.parse("2000-01-01T00:00:00+00:00"), ZonedDateTime.now()));

        candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);

        assertEquals(3, candles.size());
    }

    /**
     * Compare positions day by day and on a long period, result must be the same
     */
    @Test
    public void testTradingLongPeriodAndDayByDay() throws IOException {

        final var financialProvider = new FinancialProviderFile(-2, getFile("CGG.csv"), "CGG");
        final var from = ZonedDateTime.parse("2021-01-04T00:00:00+00:00");
        var to = ZonedDateTime.parse("2021-01-31T00:00:00+00:00");

        ProvidersSingletonUtils.addProvider("Paper", financialProvider, providerRepository);
        var stockEntity = (StockBean) stockController.createStock(new StockCreateBean(-2, "CGG"));
        assertNotNull(stockEntity);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(from, to));

        List<String> exitList = new ArrayList<>();
        List<String> enterList = new ArrayList<>();

        TradingAction action = new TradingAction() {
            @Override
            public void exit(String strategyName, String symbol, Num closePrice) {
                exitList.add(strategyName + " " + symbol + " " + closePrice.toString());
            }

            @Override
            public void enter(String strategyName, String symbol, Num closePrice) {
                enterList.add(strategyName + " " + symbol + " " + closePrice.toString());
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

        final var strategy = new Strategy("rsi_50_50", RSI5050, RSI5050_PARAMETERS);
        var barSeries = new RestBarSeries(stockController, stockEntity.getId());
        var paperTrading = new Trader(barSeries, strategy, action);
        var max = ZonedDateTime.of(of(2022, JANUARY, 2, 0, 0, 0), UTC);
        for (; to.isBefore(max); to = to.plusDays(1)) {
            barSeries.refresh();
            stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(from, to));
            paperTrading.refresh();
        }

        var positions = paperTrading.getTradingRecord().getPositions();
        assertEquals(16, positions.size());
        assertEquals(30, enterList.size() + exitList.size());

        enterList.clear();
        exitList.clear();

        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(from, max));

        barSeries = new RestBarSeries(stockController, stockEntity.getId());
        paperTrading = new Trader(barSeries, strategy, action);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(from, to));
        paperTrading.refresh();

        positions = paperTrading.getTradingRecord().getPositions();

        assertEquals(16, positions.size());
        assertEquals(0, enterList.size() + exitList.size());
    }

    /**
     * Simulate several updates in one day, make sure there is no several times the
     * same message
     */
    @Test
    public void testActionNotSendInDouble() throws IOException {

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

        FinancialProvider provider =
                new FinancialProviderLive(new FinancialProviderFile(-2, Utils.getFile("CGG.csv"), "CGG"), originalTo) {
                    boolean first = true;

                    @Override
                    public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to,
                                                   Interval interval) throws IOException {
                        var candles = new ArrayList<>(super.getCandles(symbol, from, to, interval));
                        if (!first) {
                            candles.set(candles.size() - 1, daysCandles.get(0));
                        } else {
                            first = false;
                        }
                        return candles;
                    }
                };

        ProvidersSingletonUtils.addProvider("Paper", provider, providerRepository);
        var stockCreateBean = new StockCreateBean();
        stockCreateBean.setProviderId(-2);
        stockCreateBean.setCode("CGG");
        var stock = (StockBean) stockController.createStock(stockCreateBean);
        assertNotNull(stock);
        stockController.refreshCandles(stock.getId(), DAILY, -2, new RefreshBean(from, to));
        var stockEntity = stockRepository.findById(stock.getId()).orElseThrow();
        final var strategyEntity2 = getRsi5050Strategy();

        var strategyEntity = strategyController.createStrategy(strategyEntity2);
        tradingConfigRepository.deleteAll();

        var configBean = tradingConfigController.createTradingConfig(
                new TradingConfigEntity(strategyEntity, stockEntity, true,
                        ZonedDateTime.of(LocalDate.of(2020, JANUARY, 1), MIDNIGHT, UTC), DAILY));


        var configEntity = tradingConfigRepository.findById(configBean.getId()).get();
        var barSeries = new RestBarSeries(stockController, stock.getId());
        final var rsi_50_50 = new Strategy("RSI_50_50", RSI5050, RSI5050_PARAMETERS);
        var tradingRecordEntities = tradingRecordRepository.findAllByConfig(configEntity);
        new Trader(barSeries, rsi_50_50, action,
                RestTradingRecord.loadFromConfig(repositories, tradingRecordEntities, configEntity,
                        barSeries)).refresh();
        configBean = tradingConfigController.getTradingConfig(configBean.getId());

        var tradingRecords = tradingRecordRepository.findAllByConfig(configEntity);
        var startTime = configBean.getStartTime();
        tradingRecords.removeIf(ce -> ce.getCandleStartTime().isBefore(startTime));
        tradingRecordEntities = new ArrayList<>(tradingRecords);

        new Trader(barSeries, rsi_50_50, action,
                RestTradingRecord.loadFromConfig(repositories, tradingRecordEntities, configEntity,
                        barSeries)).refresh();

        assertEquals(1, enter.size());
        assertTrue(exit.isEmpty());
        assertTrue(sendStockCandlesCompareError.isEmpty());
        assertTrue(sendConfigError.isEmpty());
    }

    @NotNull
    private StrategyCreationBean getRsi5050Strategy() {
        final var strategyEntity2 = new StrategyCreationBean("RSI_50_50", RSI5050, CLOSE_MARKET);
        for (var entry : Strategies.RSI5050_PARAMETERS.entrySet()) {
            var strategyParameter = new StrategyParameterCreationBean(entry.getKey(), entry.getValue());
            strategyEntity2.getDefaultParameters().add(strategyParameter);
        }
        return strategyEntity2;
    }

    @Test
    public void testAdvice() throws IOException {
        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_TEST);
        var stockEntity = (StockBean) stockController.createStock(stock);
        assertNotNull(stockEntity);

        AdviserBean adviser = adviceController.createAdviser(new AdviserCreationBean("test"));

        AdviceCategoryBean category = adviceController.createCategory(new AdviceCategoryCreationBean("test"));
        assertNotNull(category);

        adviceController.createAdvice(
                new AdviceCreationBean(stockEntity.getId(), true, BigDecimal.ONE,
                        LocalDate.now().minus(10, ChronoUnit.DAYS), adviser.getAdviserId(), category.getCategoryId()));

        var to = ZonedDateTime.parse("2021-06-01T00:00:00+00:00");
        FinancialProvider provider =
                new FinancialProviderLive(new FinancialProviderFile(PROVIDER_TEST, Utils.getFile("CGG.csv"), "A"), to);
        ProvidersSingletonUtils.addProvider("Paper", provider, providerRepository);

        LiveTradingBot liveTradingBot = new LiveTradingBot(repositories, stockController, adviceService) {
            @Override
            public void runAllConfig(StockReload stockReload) {
                super.runAllConfig(stockReload);
                stop();
            }

            @Override
            protected FinancialProvider getProvider() {
                return provider;
            }
        };
        liveTradingBot.run();

        List<AdviceEntity> advices = adviceRepository.findAll();
        assertEquals(1, advices.size());
        assertTrue(advices.get(0).isExecuted());
    }
}
