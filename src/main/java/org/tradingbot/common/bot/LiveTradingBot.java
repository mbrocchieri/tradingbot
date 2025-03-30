package org.tradingbot.common.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.num.DecimalNum;
import org.tradingbot.advice.AdviceRepository;
import org.tradingbot.advice.AdviceService;
import org.tradingbot.common.Interval;
import org.tradingbot.common.bot.action.NoAction;
import org.tradingbot.common.bot.action.TradingAction;
import org.tradingbot.common.flag.Flag;
import org.tradingbot.common.flag.NoWaitFlag;
import org.tradingbot.common.flag.PeriodicWaitFlag;
import org.tradingbot.common.persistence.AdviceEntity;
import org.tradingbot.common.persistence.TradingAdviceEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.ProvidersSingleton;
import org.tradingbot.common.repository.Repositories;
import org.tradingbot.common.strategy.Strategy;
import org.tradingbot.common.strategy.StrategyParameter;
import org.tradingbot.stock.RestBarSeries;
import org.tradingbot.stock.RestTradingRecord;
import org.tradingbot.stock.StockController;
import org.tradingbot.strategy.StrategyLock;
import org.tradingbot.strategy.StrategyRepository;
import org.tradingbot.trading.TradingAdviceRepository;
import org.tradingbot.tradingconfig.TradingConfigRepository;
import org.tradingbot.tradingconfig.TradingRecordRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

import static java.time.ZoneOffset.UTC;

public class LiveTradingBot {

    private static final Logger LOG = LoggerFactory.getLogger(LiveTradingBot.class);

    private final Set<Integer> stockReloadedOnce = new HashSet<>();
    private final Map<Integer, Strategy> strategyCache = new HashMap<>();
    private final Repositories repositories;
    protected TradingConfigRepository tradingConfigRepository;
    protected StrategyRepository strategyRepository;
    protected AdviceRepository adviceRepository;
    protected TradingRecordRepository tradingRecordRepository;
    protected TradingAdviceRepository tradingAdviceRepository;
    protected StockController stockController;
    protected AdviceService adviceService;
    private Flag waitDataFlag = NoWaitFlag.INSTANCE;
    private TradingAction tradingAction = NoAction.INSTANCE;
    private boolean run;

    public LiveTradingBot(Repositories repositories, StockController stockController, AdviceService adviceService) {
        tradingConfigRepository = repositories.getTradingConfigRepository();
        strategyRepository = repositories.getStrategyRepository();
        adviceRepository = repositories.getAdviceRepository();
        tradingRecordRepository = repositories.getTradingRecordRepository();
        tradingAdviceRepository = repositories.getTradingAdviceRepository();
        this.repositories = repositories;
        this.stockController = stockController;
        this.adviceService = adviceService;
    }

    public void stop() {
        run = false;
    }

    /**
     * The bot run a loop until stop is called. In the loop, it executes the
     * following steps :
     * <li>lists all available configurations</li>
     * <li>refresh candles for each stocks linked to an available configuration</li>
     * <li>execute the strategy on the stock for each configuration</li>
     */
    public void run() {
        run = true;
        loop();
    }

    private void loop() {
        while (run && !Thread.interrupted()) {
            // it is used to know if stock has already been updated in the loop
            LOG.info("Start run all config");
            var stockReload = new StockReload(getProvider(), stockController);
            runAllConfig(stockReload);
            runAdvices(stockReload);

            // to have all candles updated
            for (var stock : stockController.listStocks()) {
                try {
                    stockReload.reload(stock.getId(), ZonedDateTime.now().minusDays(1));
                } catch (IOException e) {
                    LOG.error("Error reloading stock with id {}", stock.getId(), e);
                }
            }
            LOG.info("End run all config");
            waitDataFlag.waitFlag();
        }
    }

    private void runAdvices(StockReload stockReload) {
        var advices = adviceRepository.findAllByDeletedNullAndExecutedFalse();
        for (var advice : advices) {
            final var stockId = advice.getStockEntity().getId();
            try {
                runAdvice(stockReload, advice, stockId);
            } catch (IOException e) {
                LOG.error("Error with advice {}", advice.getId(), e);
            }
        }

    }

    private void runAdvice(StockReload stockReload, AdviceEntity advice, int stockId) throws IOException {
        final var adviceDate = ZonedDateTime.of(advice.getAdviceDate(), LocalTime.MIN, UTC);
        stockReload.reload(stockId, adviceDate);
        var candles = stockController.getStockCandles(stockId, Interval.DAILY);
        if (candles != null) {
            final var limitDate = adviceDate.plusDays(8);
            for (var candle : candles) {
                if ((candle.getStartTime().isEqual(adviceDate) || candle.getStartTime().isAfter(adviceDate)) &&
                        candle.getStartTime().isBefore(limitDate)) {
                    if (advice.isBuy() && advice.getPrice().compareTo(candle.getClosePrice()) > 0) {
                        // Signal d'achat
                        TradingAdviceEntity tradingAdvice = new TradingAdviceEntity();
                        tradingAdvice.setAdvice(advice);
                        tradingAdvice.setAmount(BigDecimal.TEN);
                        tradingAdvice.setBuy(true);
                        tradingAdvice.setDate(candle.getStartTime());
                        tradingAdviceRepository.save(tradingAdvice);
                        adviceService.updateToExecuted(advice.getId());
                        tradingAction.enter("Advice", "EUR", DecimalNum.valueOf(candle.getClosePrice()));
                        break;
                    }
                    // TODO generer le signal de vente
                }
            }
            if (!advice.isExecuted() && limitDate.isBefore(ZonedDateTime.now())) {
                adviceService.updateToExecuted(advice.getId());
            }
        } else {
            LOG.error("Error with advice {} no candle", advice.getId());
        }
    }

    public void runAllConfig(StockReload stockReload) {
        Map<Integer, Integer> configIdsToStrategyIds = new HashMap<>();
        for (var config : tradingConfigRepository.findAllByActiveTrue()) {
            configIdsToStrategyIds.put(config.getId(), config.getStrategy().getId());
        }
        for (var entry : configIdsToStrategyIds.entrySet()) {
            var strategyId = entry.getValue();
            var configId = entry.getKey();
            StrategyLock.INSTANCE.lock(strategyId);
            try {
                var optionalConfig = tradingConfigRepository.findById(entry.getKey());
                // if absent means the config has been deleted
                if (optionalConfig.isPresent()) {
                    LOG.debug("Run trading for config {}", configId);
                    var strategy = getStrategy(strategyId);
                    // if absent means the strategy has been deleted
                    strategy.ifPresent(value -> runConfig(stockReload, optionalConfig.get(), value));
                }
            } finally {
                StrategyLock.INSTANCE.unlock(strategyId);
            }
        }
    }

    /**
     * @param stockReload it is used to know if stock has already been updated in
     *                    the loop. Contains stock ids
     * @param config      trading configuration to load
     * @param strategy    strategy to run
     */
    private void runConfig(StockReload stockReload, TradingConfigEntity config, Strategy strategy) {

        // load all past trading buy and sell from database
        List<TradingRecordEntity> tradingRecordEntities = getTradingRecordEntities(config, config.getStartTime());

        ZonedDateTime startTime;
        if (!tradingRecordEntities.isEmpty()) {
            startTime = tradingRecordEntities.get(0).getCandleStartTime();
        } else {
            startTime = config.getStartTime();
        }

        try {
            // get last price of the stock
            if (!stockReloadedOnce.contains(config.getStock().getId())) {
                stockController.updateLastCandle(config.getStock().getId(), Interval.DAILY,
                        getProvider().getProviderId());
                stockReloadedOnce.add(config.getStock().getId());
            }

            stockReload.reload(config.getStock().getId(), startTime);

            // execute strategy in the stock
            var barSeries = new RestBarSeries(stockController, config.getStock().getId());
            var tradingRecord =
                    RestTradingRecord.loadFromConfig(repositories, tradingRecordEntities, config, barSeries);

            var parameters = StrategyParameter.toMap(config.getParameterEntities());
            Trader paperTrading = new Trader(barSeries, strategy, tradingAction, tradingRecord, parameters);
            paperTrading.refresh();
        } catch (IOException | IllegalStateException | IllegalArgumentException | SecurityException e) {
            LOG.error("Error with config {}", config.getId(), e);
        }
    }

    /**
     * Load trading records linked to the trading configuration
     *
     * @param config trading configuration
     * @return Trading record. The first element of the list is always an opening
     * trade
     */
    private List<TradingRecordEntity> getTradingRecordEntities(TradingConfigEntity config, ZonedDateTime startTime) {
        var tradingRecordEntities =
                tradingRecordRepository.findAllByConfigIdAfterOrderByCandleStartTime(config, startTime);

        // if the first trade is not an opening trading, we have to load the opening
        // trade before
        if (!tradingRecordEntities.isEmpty() && !tradingRecordEntities.get(0).isBuy()) {
            var trade = tradingRecordRepository.findFirstByConfigAndStartTimeOrderByDesc(config, startTime).get(0);
            tradingRecordEntities.add(0, trade);
        }
        return tradingRecordEntities;
    }

    protected FinancialProvider getProvider() {
        return ProvidersSingleton.INSTANCE.getProvider(1);// FIXME
    }

    private Optional<Strategy> getStrategy(int strategyId) {

        var strategy = strategyCache.get(strategyId);
        if (strategy == null) {
            var optionalStrategyEntity = strategyRepository.findById(strategyId);

            if (optionalStrategyEntity.isPresent()) {
                strategy = new Strategy(optionalStrategyEntity.get());
                strategyCache.put(strategyId, strategy);
            } else {
                // strategy can be deleted
                LOG.warn("Strategy with id {} is absent", strategyId);

                return Optional.empty();
            }
        }
        return Optional.of(strategy);

    }

    public void setWaitDataFlag(PeriodicWaitFlag waitDataFlag) {
        this.waitDataFlag = waitDataFlag;
    }

    public void setTradingAction(TradingAction tradingAction) {
        this.tradingAction = tradingAction;
    }
}
