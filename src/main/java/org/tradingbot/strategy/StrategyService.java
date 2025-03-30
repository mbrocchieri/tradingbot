package org.tradingbot.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordEntity;
import org.tradingbot.stock.StockRepository;
import org.tradingbot.tradingconfig.TradingConfigRepository;
import org.tradingbot.tradingconfig.TradingConfigService;
import org.tradingbot.tradingconfig.TradingRecordRepository;
import org.tradingbot.tradingconfig.TradingRecordSummaryRepository;

import java.util.Collections;
import java.util.List;

@Service
public class StrategyService {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TradingConfigService tradingConfigService;

    @Autowired
    private TradingConfigRepository tradingConfigRepository;

    @Autowired
    private TradingRecordRepository tradingRecordRepository;

    @Autowired
    private TradingRecordSummaryRepository tradingRecordSummaryRepository;


    @Transactional
    public StrategyEntity save(StrategyEntity strategyEntity) {
        var strategyEntitySaved = strategyRepository.save(strategyEntity);

        for (var stock : stockRepository.findAll()) {
            tradingConfigService.create(strategyEntitySaved, stock);
        }


        return strategyEntitySaved;
    }

    @Transactional
    public void enableAlerts(int strategyId, boolean enable) {
        strategyRepository.enableAlerts(strategyId, enable);
    }

    @Transactional
    public void delete(int strategyId) {
        StrategyLock.INSTANCE.lock(strategyId);
        try {
            StrategyEntity strategy = strategyRepository.getById(strategyId);

            // suppression des configs
            List<TradingConfigEntity> tradingConfigs =
                    tradingConfigRepository.findAllByStrategyIn(Pageable.unpaged(), Collections.singletonList(strategy));
            for (var tradingConfig : tradingConfigs) {

                var tradingSummary = tradingRecordSummaryRepository.findAllByConfig(tradingConfig);

                for (var summary : tradingSummary) {
                    tradingRecordSummaryRepository.delete(summary);
                }

                List<TradingRecordEntity> tradingRecords = tradingRecordRepository.findAllByConfig(tradingConfig);
                for (var record : tradingRecords) {
                    tradingRecordRepository.delete(record);
                }

                tradingConfigRepository.delete(tradingConfig);
            }

            // suppression de la strategy
            strategyRepository.delete(strategy);
        } finally {
            StrategyLock.INSTANCE.unlock(strategyId);
        }
    }
}
