package org.tradingbot.common.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tradingbot.advice.AdviceRepository;
import org.tradingbot.stock.CandleRepository;
import org.tradingbot.strategy.StrategyRepository;
import org.tradingbot.trading.TradingAdviceRepository;
import org.tradingbot.tradingconfig.TradingConfigRepository;
import org.tradingbot.tradingconfig.TradingRecordRepository;
import org.tradingbot.tradingconfig.TradingRecordSummaryRepository;

@Service
public class Repositories {

    @Autowired
    protected TradingConfigRepository tradingConfigRepository;

    @Autowired
    protected StrategyRepository strategyRepository;

    @Autowired
    protected TradingRecordRepository tradingRecordRepository;

    @Autowired
    protected AdviceRepository adviceRepository;

    @Autowired
    protected TradingAdviceRepository tradingAdviceRepository;

    @Autowired
    protected TradingRecordSummaryRepository tradingRecordSummaryRepository;

    @Autowired
    protected CandleRepository candleRepository;

    public TradingConfigRepository getTradingConfigRepository() {
        return tradingConfigRepository;
    }

    public StrategyRepository getStrategyRepository() {
        return strategyRepository;
    }

    public TradingRecordRepository getTradingRecordRepository() {
        return tradingRecordRepository;
    }

    public AdviceRepository getAdviceRepository() {
        return adviceRepository;
    }

    public TradingAdviceRepository getTradingAdviceRepository() {
        return tradingAdviceRepository;
    }

    public TradingRecordSummaryRepository getTradingRecordSummaryRepository() {
        return tradingRecordSummaryRepository;
    }

    public CandleRepository getCandleRepository() {
        return candleRepository;
    }
}
