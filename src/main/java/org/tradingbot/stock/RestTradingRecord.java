package org.tradingbot.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Trade;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.tradingbot.common.persistence.CandleId;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordEntity;
import org.tradingbot.common.persistence.TradingRecordSummaryEntity;
import org.tradingbot.common.repository.Repositories;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RestTradingRecord extends BaseTradingRecord {

    private static final Logger LOG = LoggerFactory.getLogger(RestTradingRecord.class);

    private final TradingConfigEntity tradingConfigEntity;
    private final BarSeries barSeries;

    private final Repositories repositories;

    private RestTradingRecord(Repositories repositories, TradingConfigEntity tradingConfigEntity, BarSeries barSeries,
                              Trade... trades) {
        super(trades);
        this.repositories = repositories;
        this.tradingConfigEntity = Objects.requireNonNull(tradingConfigEntity);
        this.barSeries = Objects.requireNonNull(barSeries);

    }

    private RestTradingRecord(Repositories repositories, TradingConfigEntity tradingConfigEntity, BarSeries barSeries) {
        this.repositories = repositories;
        this.tradingConfigEntity = Objects.requireNonNull(tradingConfigEntity);
        this.barSeries = Objects.requireNonNull(barSeries);

    }

    public static RestTradingRecord loadFromConfig(Repositories repositories,
                                                   List<TradingRecordEntity> tradingRecordEntities,
                                                   TradingConfigEntity tradingConfigEntity, BarSeries barSeries) {

        List<Trade> trades = new ArrayList<>();
        for (TradingRecordEntity tradingRecordEntity : tradingRecordEntities) {
            toTrade(tradingRecordEntity, barSeries).ifPresent(trades::add);
        }
        if (!trades.isEmpty()) {
            return new RestTradingRecord(repositories, tradingConfigEntity, barSeries, trades.toArray(new Trade[0]));
        } // Erreur de conception dans ta4j ?
        return new RestTradingRecord(repositories, tradingConfigEntity, barSeries);
    }

    private static Optional<Trade> toTrade(TradingRecordEntity tradingRecordEntity, BarSeries barSeries) {
        int i;
        for (i = 0; i < barSeries.getBarCount(); i++) {
            if (barSeries.getBar(i).getBeginTime().compareTo(tradingRecordEntity.getCandleStartTime()) == 0) {
                break;
            }
        }
        if (i == barSeries.getBarCount()) {
            return Optional.empty();
        }

        if (tradingRecordEntity.isBuy()) {
            return Optional.of(Trade.buyAt(i, barSeries, DecimalNum.valueOf(tradingRecordEntity.getAmount())));
        } else {
            return Optional.of(Trade.sellAt(i, barSeries, DecimalNum.valueOf(tradingRecordEntity.getAmount())));
        }
    }

    @Override
    public boolean enter(int index, Num price, Num amount) {
        boolean enter = super.enter(index, price, amount);
        if (enter) {
            persist(index, true, amount);
        }
        return enter;
    }

    @Override
    public boolean exit(int index, Num price, Num amount) {
        boolean exit = super.exit(index, price, amount);
        if (exit) {
            persist(index, false, amount);
        }
        return exit;
    }

    private void persist(int index, boolean buy, Num amount) {
        final var bar = barSeries.getBar(index);
        final var beginTime = bar.getBeginTime();
        LOG.debug("persist index = {} buy = {} begin date = {}", index, buy, beginTime);
        final var stockPrice = (BigDecimal) barSeries.getBar(index).getClosePrice().getDelegate();
        var tradingRecordEntity =
                new TradingRecordEntity(tradingConfigEntity, buy, beginTime, (BigDecimal) amount.getDelegate(),
                        stockPrice);

        if (!buy) {

            computeTradeSummary(repositories, tradingConfigEntity, stockPrice, tradingRecordEntity);
        }
        repositories.getTradingRecordRepository().save(tradingRecordEntity);
    }

    public static void computeTradeSummary(Repositories repositories, TradingConfigEntity tradingConfigEntity,
                                            BigDecimal sellPrice, TradingRecordEntity sellTradingRecord) {

        var buyTradingRecord = repositories.getTradingRecordRepository()
                .findFirstByConfigAndStartTimeOrderByDesc(tradingConfigEntity, sellTradingRecord.getCandleStartTime()).get(0);

        var buyPrice = buyTradingRecord.getStockPrice();

        final var startDate = buyTradingRecord.getCandleStartTime();
        final var endDate = sellTradingRecord.getCandleStartTime();

        var perf = sellPrice.divide(buyPrice, 10, RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(100));
        perf = perf.subtract(BigDecimal.valueOf(100));

        var tradingRecordSummary = new TradingRecordSummaryEntity(tradingConfigEntity, startDate, endDate, perf);
        repositories.getTradingRecordSummaryRepository().save(tradingRecordSummary);
    }
}
