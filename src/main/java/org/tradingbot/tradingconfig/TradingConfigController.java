package org.tradingbot.tradingconfig;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.common.persistence.ConfigParameterEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.stock.CandleBean;
import org.tradingbot.stock.CandleRepository;
import org.tradingbot.stock.StockRepository;
import org.tradingbot.strategy.StrategyRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class TradingConfigController {

    @Autowired
    TradingConfigRepository tradingConfigRepository;

    @Autowired
    TradingRecordRepository tradingRecordRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StrategyRepository strategyRepository;

    @Autowired
    CandleRepository candleRepository;

    @GetMapping("/trading-configs")
    public List<TradingConfigBean> listTradingConfigs() {
        List<TradingConfigBean> l = new ArrayList<>();
        for (var configEntity : tradingConfigRepository.findAll()) {
            TradingConfigBean tcb = toTradingConfigBean(configEntity);
            l.add(tcb);
        }
        return l;
    }

    @NotNull
    private TradingConfigBean toTradingConfigBean(TradingConfigEntity configEntity) {
        return new TradingConfigBean(configEntity.getId(), configEntity.getStrategy(), configEntity.getStock(),
                configEntity.isActive(), configEntity.getStartTime(), configEntity.getInterval(),
                configEntity.getParameterEntities());
    }

    @PostMapping("/trading-configs")
    public TradingConfigBean createTradingConfig(@RequestBody TradingConfigEntity tradingConfig) throws IOException {

        var configs = tradingConfigRepository.findAllByStockAndStrategy(tradingConfig.getStock(),
                tradingConfig.getStrategy());

        for (var config : configs) {
            if (isSameParameters(tradingConfig.getParameterEntities(), config.getParameterEntities())) {
                throw new IOException("Duplicate");
            }
        }

        return toTradingConfigBean(tradingConfigRepository.save(tradingConfig));
    }

    @PostMapping("/trading-configs/page/{page}/size/{size}")
    public List<TradingConfigBean> searchTradingConfig(@PathVariable("page") int page, @PathVariable("size") int size,
                                                       @RequestBody TradingConfigSearchBean searchBean)
            throws IOException {
        Iterable<TradingConfigEntity> configs;
        var pageable = PageRequest.of(page, size);
        if (searchBean.getStockIds().isEmpty() && searchBean.getStrategyIds().isEmpty()) {
            configs = tradingConfigRepository.findAll(pageable);
        } else if (!searchBean.getStockIds().isEmpty() && !searchBean.getStrategyIds().isEmpty()) {
            var stocks = stockRepository.findAllById(searchBean.getStockIds());
            var strategies = strategyRepository.findAllById(searchBean.getStrategyIds());
            configs = tradingConfigRepository.findAllByStockInAndStrategyIn(pageable, stocks, strategies);

        } else if (searchBean.getStockIds().isEmpty()) {
            var strategies = strategyRepository.findAllById(searchBean.getStrategyIds());
            configs = tradingConfigRepository.findAllByStrategyIn(pageable, strategies);
        } else {
            var stocks = stockRepository.findAllById(searchBean.getStockIds());
            configs = tradingConfigRepository.findAllByStockIn(pageable, stocks);

        }

        List<TradingConfigBean> list = new ArrayList<>();
        for (var config : configs) {
            list.add(toTradingConfigBean(config));
        }

        return list;
    }

    @PostMapping("/trading-configs/count")
    public long countTradingConfig(@RequestBody TradingConfigSearchBean searchBean) throws IOException {
        if (searchBean.getStockIds().isEmpty() && searchBean.getStrategyIds().isEmpty()) {
            return tradingConfigRepository.count();
        } else if (!searchBean.getStockIds().isEmpty() && !searchBean.getStrategyIds().isEmpty()) {
            var stocks = stockRepository.findAllById(searchBean.getStockIds());
            var strategies = strategyRepository.findAllById(searchBean.getStrategyIds());
            return tradingConfigRepository.countByStockInAndStrategyIn(stocks, strategies);
        } else if (searchBean.getStockIds().isEmpty()) {
            var strategies = strategyRepository.findAllById(searchBean.getStrategyIds());
            return tradingConfigRepository.countByStrategyIn(strategies);
        } else {
            var stocks = stockRepository.findAllById(searchBean.getStockIds());
            return tradingConfigRepository.countByStockIn(stocks);
        }
    }

    @GetMapping("/trading-configs/{configId}")
    public TradingConfigBean getTradingConfig(@PathVariable("configId") int configId) {
        return toTradingConfigBean(tradingConfigRepository.findById(configId).orElseThrow());
    }


    @PostMapping("/trading-configs/{configId}/records")
    public TradingRecordBean getRecords(@PathVariable("configId") int configId,
                                        @RequestBody RecordDetailBean detailBean) throws IOException {
        var optionalTradingConfig = tradingConfigRepository.findById(configId);
        if (optionalTradingConfig.isEmpty()) {
            throw new IOException("trading config not found");
        }

        var tradingConfig = optionalTradingConfig.get();
        var tradingRecord = new TradingRecordBean();
        List<CandleEntity> candles;
        if (detailBean.getFrom() == null) {
            candles = candleRepository.findAllByStockIdAndIntervalOrderByStartTime(tradingConfig.getStock().getId(),
                    Interval.DAILY);
        } else {
            candles = candleRepository.findAllByStockIdAndIntervalAndStartTimeBetweenOrderByStartTime(
                    tradingConfig.getStock().getId(), Interval.DAILY, detailBean.getFrom(), detailBean.getTo());
        }
        Map<ZonedDateTime, BigDecimal> dateToCandle = new HashMap<>();
        for (var candle : candles) {
            tradingRecord.getCandles().add(new CandleBean(candle));
            dateToCandle.put(candle.getStartTime(), candle.getOpenPrice());
        }
        for (var record : tradingConfig.getTradingRecord()) {
            if (detailBean.getFrom() == null || record.getCandleStartTime().isAfter(detailBean.getFrom()) &&
                    record.getCandleStartTime().isBefore(detailBean.getTo())) {
                tradingRecord.getActions().add(new TradingActionBean(record.isBuy(), record.getCandleStartTime(),
                        dateToCandle.get(record.getCandleStartTime())));
            }
        }

        return tradingRecord;
    }

    @GetMapping("/trading-configs/{id}/stats")
    public TradingConfigStats getStats(@PathVariable("id") int id) throws IOException {
        var optionalTradingConfig = tradingConfigRepository.findById(id);
        if (optionalTradingConfig.isEmpty()) {
            throw new IOException("trading config not found");
        }
        return getStats(optionalTradingConfig.get());
    }


    public TradingConfigStats getStats(TradingConfigEntity tradingConfig) throws IOException {
        TradingConfigStats tradingConfigStats = new TradingConfigStats();
        BigDecimal startPrice = BigDecimal.valueOf(100);
        BigDecimal endPrice = startPrice;
        BigDecimal buyPrice = null;
        BigDecimal lastBuyPrice = null;
        BigDecimal lastSellPrice = null;
        Boolean isLastBuy = null;
        ZonedDateTime lastBuyDate = null;
        int nbTrade = 0;

        var tradingRecords = tradingRecordRepository.findAllByConfig(tradingConfig);

        boolean firstTrade = tradingRecords.size() == 1;
        for (var trade : tradingRecords) {
            BigDecimal price = candleRepository.findByStockIdAndIntervalAndStartTime(tradingConfig.getStock().getId(),
                    tradingConfig.getInterval(), trade.getCandleStartTime()).get().getOpenPrice();
            isLastBuy = trade.isBuy();
            if (isLastBuy) {
                buyPrice = price;
                lastBuyPrice = price;
                lastBuyDate = trade.getCandleStartTime();
            } else {
                nbTrade++;
                if (buyPrice != null) {
                    endPrice = endPrice.multiply(price.divide(buyPrice, 10, RoundingMode.CEILING));
                }
                buyPrice = null;
                lastSellPrice = price;
            }
        }
        var firstCandle =
                candleRepository.findFirstByStockIdAndIntervalAndStartTimeGreaterThan(tradingConfig.getStock().getId(),
                        tradingConfig.getInterval(), tradingConfig.getStartTime()).get();

        BigDecimal stockPerf;
        BigDecimal globalStockPerf;
        BigDecimal configPerf;
        if (isLastBuy != null && !firstTrade) {


            if (!isLastBuy) {
                tradingConfigStats.setLastBuyPrice(lastBuyPrice);
                tradingConfigStats.setLastBuyDate(lastBuyDate);
                BigDecimal tradePerf = lastSellPrice.subtract(lastBuyPrice).multiply(BigDecimal.valueOf(100))
                        .divide(lastBuyPrice, 10, RoundingMode.CEILING);
                tradingConfigStats.setPercentTradePerf(tradePerf);
            }
            tradingConfigStats.setGlobalStartDate(firstCandle.getStartTime());
            tradingConfigStats.setGlobalEndPrice(firstCandle.getStartTime());
            configPerf = endPrice.subtract(startPrice).multiply(BigDecimal.valueOf(100))
                    .divide(startPrice, 10, RoundingMode.CEILING);
            tradingConfigStats.setPercentGlobalConfigPerf(configPerf);
            if (lastSellPrice != null && firstCandle.getClosePrice().compareTo(BigDecimal.ZERO) != 0) {
                stockPerf = lastSellPrice.subtract(firstCandle.getClosePrice()).multiply(BigDecimal.valueOf(100))
                        .divide(firstCandle.getClosePrice(), 10, RoundingMode.CEILING);
                tradingConfigStats.setPercentNoStrategyPerf(stockPerf);

                CandleEntity lastCandle = candleRepository.findFirstByStockIdAndIntervalOrderByStartTimeDesc(
                        tradingConfig.getStock().getId(), tradingConfig.getInterval()).get();
                globalStockPerf = lastCandle.getClosePrice().subtract(firstCandle.getClosePrice())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(firstCandle.getClosePrice(), 10, RoundingMode.CEILING);
                tradingConfigStats.setPercentGlobalStockPerf(globalStockPerf);

            }

        }
        tradingConfigStats.setStartPrice(startPrice);
        tradingConfigStats.setEndPrice(endPrice);
        tradingConfigStats.setNbTrade(nbTrade);
        return tradingConfigStats;

    }

    private boolean isSameParameters(List<ConfigParameterEntity> parameterEntities,
                                     List<ConfigParameterEntity> parameterEntities2) {
        if (parameterEntities.size() != parameterEntities2.size()) {
            return false;
        }
        if (parameterEntities.isEmpty()) {
            return true;
        }

        Map<String, BigDecimal> map1 = new HashMap<>();
        Map<String, BigDecimal> map2 = new HashMap<>();
        parameterEntities.forEach(p -> map1.put(p.getParameter().getName(), p.getValue()));
        parameterEntities2.forEach(p -> map2.put(p.getParameter().getName(), p.getValue()));
        Set<String> keys = new HashSet<>();
        keys.addAll(map1.keySet());
        keys.addAll(map2.keySet());


        for (var key : keys) {
            if (!map1.containsKey(key) || !map2.containsKey(key)) {
                return false;
            }
            if (map1.get(key).compareTo(map2.get(key)) != 0) {
                return false;
            }
        }
        return true;

    }
}
