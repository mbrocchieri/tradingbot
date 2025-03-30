package org.tradingbot.stats;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingStatisticsEntity;
import org.tradingbot.common.repository.TradingStatisticsRepository;
import org.tradingbot.common.repository.TradingViewRepository;
import org.tradingbot.strategy.ErrorResponse;
import org.tradingbot.strategy.StrategyRepository;
import org.tradingbot.tradingconfig.TradingConfigController;
import org.tradingbot.tradingconfig.TradingConfigRepository;

@RestController
public class StatisticsController {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsController.class);
    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private TradingConfigRepository tradingConfigRepository;

    @Autowired
    private TradingConfigController tradingConfigController;

    @Autowired
    private TradingViewRepository tradingViewRepository;

    @Autowired
    private TradingStatisticsRepository tradingStatisticsRepository;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> error(Throwable t) {
        LOG.warn(t.getMessage(), t);
        return ResponseEntity.internalServerError().body(new ErrorResponse(t.getMessage()));
    }

    @GetMapping("/strategies/{strategyId}/stats")
    public StatisticsBean getStrategyStatistics(@PathVariable("strategyId") int strategyId) throws IOException {
        Optional<StrategyEntity> optionalStrategyEntity = strategyRepository.findById(strategyId);
        if (optionalStrategyEntity.isEmpty()) {
            throw new IllegalStateException("strategy with id " + strategyId + " is not found");
        }
        TradingStatisticsEntity stats = tradingStatisticsRepository.findStrategyStatistics(strategyId).get(0);

        var allBuys = BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(stats.getNbTrades()));
        var allSells = stats.getPercentPerf().multiply(allBuys).divide(BigDecimal.valueOf(100), 9, RoundingMode.CEILING);
        return new StatisticsBean(allBuys, allSells, stats.getNbTrades(), stats.getPercentPerf());

    }

}
