package org.tradingbot.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tradingbot.advice.AdviserRepository;
import org.tradingbot.common.persistence.AdviserEntity;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.repository.TradingViewRepository;
import org.tradingbot.stock.StockRepository;
import org.tradingbot.strategy.ErrorResponse;
import org.tradingbot.strategy.StrategyRepository;
import org.tradingbot.tradingconfig.TradingRecordRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.tradingbot.trading.TradingSource.STRATEGY;

@RestController
public class TradingController {
    private static final Logger LOG = LoggerFactory.getLogger(TradingController.class);

    @Autowired
    private TradingViewRepository tradingViewRepository;

    @Autowired
    private TradingRecordRepository tradingRecordRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AdviserRepository adviserRepository;

    @Autowired
    private TradingAdviceRepository tradingAdviceRepository;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> error(Throwable t) {
        LOG.error(t.getMessage(), t);
        return ResponseEntity.internalServerError().body(new ErrorResponse(t.getMessage()));
    }

    @GetMapping("/tradings/page/{page}/size/{size}")
    public List<TradingBean> getTradings(@PathVariable("page") int page, @PathVariable("size") int size) {
        var list = tradingViewRepository.findAllPaginated(page * size, size);

        Set<Integer> stockIds = new HashSet<>();
        Set<Integer> strategyIds = new HashSet<>();
        for (var record : list) {
            stockIds.add(record.getStockId());
            strategyIds.add(record.getStrategyId());
        }

        Map<Integer, StockEntity> stockCache = new HashMap<>();
        for (var stockEntity : stockRepository.findAllById(stockIds)) {
            stockCache.put(stockEntity.getId(), stockEntity);
        }

        Map<Integer, StrategyEntity> strategyCache = new HashMap<>();
        for (var strategyEntity : strategyRepository.findAllById(strategyIds)) {
            strategyCache.put(strategyEntity.getId(), strategyEntity);
        }

        Map<Integer, AdviserEntity> adviserCache = new HashMap<>();
        for (var adviserEntity : adviserRepository.findAllById(strategyIds)) {
            adviserCache.put(adviserEntity.getId(), adviserEntity);
        }

        List<TradingBean> toReturn = new ArrayList<>();

        for (var record : list) {
            String stratOrAdviserName;
            if ("TR".equals(record.getType())) {
                final var strategyId = record.getStrategyId();
                final var strategyEntity = strategyCache.get(strategyId);
                if (strategyEntity == null) {
                    throw new IllegalStateException("strategy with id " + strategyId + " not found");
                }
                stratOrAdviserName = strategyEntity.getName();
            } else {
                final var adviserId = record.getStrategyId();
                final var adviserEntity = adviserCache.get(adviserId);
                if (adviserEntity == null) {
                    throw new IllegalStateException("adviser with id " + adviserId + " not found");
                }
                stratOrAdviserName = adviserEntity.getName();
            }
            final var stockEntity = stockCache.get(record.getStockId());
            if (stockEntity == null) {
                throw new IllegalStateException("stock with id " + record.getStockId() + " not found");
            }

            TradingData tradingData;
            if ("TR".equals(record.getType())) {
                tradingData = new TradingStrategyData(record.getId(), record.getStrategyId(), stratOrAdviserName);
            } else {
                tradingData = new TradingAdviceData(stratOrAdviserName);
            }
            final var tradingBean =
                    new TradingBean(record.getId(), STRATEGY, tradingData, record.isBuy(), record.getDate(),
                            BigDecimal.TEN, stockEntity.getId(), stockEntity.getName(), record.getConfigId(), record.getComputedDate());
            toReturn.add(tradingBean);
        }
        return toReturn;
    }

    @GetMapping("/tradings/count")
    public long getTradingsCount() {
        return tradingViewRepository.countTrading();
    }
}
