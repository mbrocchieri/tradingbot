package org.tradingbot;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tradingbot.advice.AdviceController;
import org.tradingbot.advice.AdviceRepository;
import org.tradingbot.advice.AdviceService;
import org.tradingbot.advice.AdviserRepository;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.bot.period.TradingPeriodEnum;
import org.tradingbot.common.persistence.ProviderEntity;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;
import org.tradingbot.common.repository.Repositories;
import org.tradingbot.stats.StatisticsController;
import org.tradingbot.stock.*;
import org.tradingbot.strategy.StrategyController;
import org.tradingbot.strategy.StrategyCreationBean;
import org.tradingbot.strategy.StrategyParameterCreationBean;
import org.tradingbot.strategy.StrategyRepository;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.trading.TradingAdviceRepository;
import org.tradingbot.trading.TradingController;
import org.tradingbot.tradingconfig.TradingConfigController;
import org.tradingbot.tradingconfig.TradingConfigRepository;
import org.tradingbot.tradingconfig.TradingRecordRepository;
import org.tradingbot.tradingconfig.TradingRecordSummaryRepository;
import org.tradingbot.util.FinancialProviderFile;
import org.tradingbot.util.FinancialProviderMock;
import org.tradingbot.util.Strategies;
import org.tradingbot.util.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.CLOSE_MARKET;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.OPEN_MARKET;
import static org.tradingbot.util.Strategies.RSI5050;

@SpringBootTest(properties ="spring.jpa.hibernate.ddl-auto=create-drop")
public abstract class TradingBotApplicationTests {
    protected static int PROVIDER_TEST = -1;
    protected static int PROVIDER_FAIL = -2;

    // Controllers
    @Autowired
    protected AdviceController adviceController;

    @Autowired
    protected StockController stockController;

    @Autowired
    protected StatisticsController statisticsController;

    @Autowired
    protected StrategyController strategyController;

    @Autowired
    protected TradingConfigController tradingConfigController;

    // Repositories
    @Autowired
    protected StockRepository stockRepository;

    @Autowired
    protected StrategyRepository strategyRepository;

    @Autowired
    protected TradingConfigRepository tradingConfigRepository;

    @Autowired
    protected ProviderRepository providerRepository;

    @Autowired
    protected StockMetadataRepository stockMetadataRepository;

    @Autowired
    protected CandleRepository candleRepository;

    @Autowired
    protected TradingRecordRepository tradingRecordRepository;

    @Autowired
    protected TradingRecordSummaryRepository tradingRecordSummaryRepository;

    @Autowired
    protected TradingAdviceRepository tradingAdviceRepository;

    @Autowired
    protected AdviceRepository adviceRepository;

    @Autowired
    protected TradingController tradingController;

    @Autowired
    private AdviserRepository adviserRepository;

    @Autowired
    protected ProviderCodeRepository providerCodeRepository;

    @Autowired
    protected Repositories repositories;

    @Autowired
    protected AdviceService adviceService;

    @BeforeEach
    public void beforeEach() {
        cleanDatabase();
    }

    protected void cleanDatabase() {
        tradingAdviceRepository.deleteAll();
        adviceRepository.deleteAll();
        adviserRepository.deleteAll();
        tradingRecordSummaryRepository.deleteAll();
        tradingRecordRepository.deleteAll();
        tradingConfigRepository.deleteAll();
        strategyRepository.deleteAll();
        providerCodeRepository.deleteAll();
        stockRepository.deleteAll();
        providerRepository.deleteAll();

        ProviderEntity yahoo = new ProviderEntity();
        yahoo.setId(1);
        yahoo.setName("Yahoo");
        providerRepository.save(yahoo);
        var providerId = -1;
        ProvidersSingletonUtils.addProvider("providerTest", new FinancialProviderMock(providerId),
                providerRepository);

        var failProvider = new FinancialProvider() {
            @Override
            public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval)
                    throws IOException {
                throw new IOException("Unknown");
            }

            @Override
            public StockData getStockData(String providerCode) throws IOException {
                throw new IOException("Missing data for symbol " + providerCode + " for provider providerFail");
            }

            @Override
            public int getProviderId() {
                return PROVIDER_FAIL;
            }
        };

        ProvidersSingletonUtils.addProvider("providerFail", failProvider, providerRepository);

    }

    protected StrategyEntity getStrategyEntity() {
        return getStrategyEntity("RSI_50_50", OPEN_MARKET);
    }

    @NotNull
    protected StrategyEntity getStrategyEntity(String name, TradingPeriodEnum tradingPeriodEnum) {
        var strategyEntity = new StrategyCreationBean(name, RSI5050, tradingPeriodEnum);
        var parameters = Strategies.RSI5050_PARAMETERS;
        for (var entry : parameters.entrySet()) {
            var parameter = new StrategyParameterCreationBean(entry.getKey(), entry.getValue());
            strategyEntity.getDefaultParameters().add(parameter);
        }
        return Objects.requireNonNull(strategyController.createStrategy(strategyEntity));
    }

    protected int createProvider(File candlesData, String stockName) throws IOException {
        var providerId = -2;
        FinancialProviderFile financialProviderFile = new FinancialProviderFile(providerId, candlesData, stockName);
        ProvidersSingletonUtils.addProvider("test", financialProviderFile, providerRepository);
        return providerId;
    }

    protected StockEntity createStock(int providerId, String stockName) throws IOException {
        var stockBean = (StockBean) stockController.createStock(new StockCreateBean(providerId, stockName));
        stockController.refreshCandles(stockBean.getId(), Interval.DAILY,
                providerId, new RefreshBean(ZonedDateTime.now().minusYears(5), ZonedDateTime.now()));
        return stockRepository.findById(stockBean.getId()).orElseThrow();
    }

    protected StrategyEntity createStrategy(File strategyFile) throws IOException {
        String xml = Utils.readFile(strategyFile.toPath());
        return strategyController.createStrategy(new StrategyCreationBean("RSI", xml, CLOSE_MARKET));
    }

    protected static void assertEqualsBd(BigDecimal expected, BigDecimal current) {
        assertEquals(0, expected.compareTo(current), current.toString());
    }

    protected static void assertEqualsBd(long l, BigDecimal current) {
        assertEqualsBd(BigDecimal.valueOf(l), current);
    }

    protected static void assertEqualsBd(String s, BigDecimal current) {
        assertEqualsBd(new BigDecimal(s), current);
    }

}
