package org.tradingbot.stock;

import org.apache.commons.lang3.NotImplementedException;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.tradingbot.common.Interval;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class RestBarSeries  implements BarSeries  {

    private final StockController stockController;
    private final int stockId;
    private List<Bar> barData;
    private StockBean stockBean;

    public RestBarSeries(StockController stockController, int stockId) {
        this.stockController = stockController;
        this.stockId = stockId;
    }

    private StockBean getStock() {
        if (stockBean == null) {
            stockBean = stockController.getStock(stockId);
        }
        return Objects.requireNonNull(stockBean);
    }

    @Override
    public String getName() {
        return getStock().getName();
    }

    @Override
    public Bar getBar(int i) {
        return getBarData().get(i);
    }

    @Override
    public int getBarCount() {
        return getBarData().size();
    }

    @Override
    public List<Bar> getBarData() {
        if (barData == null) {
            barData = new ArrayList<>();
            final var body = stockController.getStockCandles(stockId, Interval.DAILY);
            if (body != null) {
                for (var candle : body) {
                    barData.add(new RestBar(candle));
                }
            }
        }
        return barData;
    }

    @Override
    public int getBeginIndex() {
        if (getBarData().isEmpty()) {
            return -1;
        }
        return 0;
    }

    @Override
    public int getEndIndex() {
        return getBarCount() - 1;
    }

    @Override
    public int getMaximumBarCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMaximumBarCount(int maximumBarCount) {
        throw new NotImplementedException();
    }

    @Override
    public int getRemovedBarsCount() {
        return 0;
    }

    @Override
    public void addBar(Bar bar, boolean replace) {
        throw new NotImplementedException();
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime) {
        throw new NotImplementedException();
    }

    @Override
    public void addBar(ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume,
                       Num amount) {
        throw new NotImplementedException();
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice,
                       Num closePrice, Num volume) {
        throw new NotImplementedException();
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice,
                       Num closePrice, Num volume, Num amount) {
        throw new NotImplementedException();
    }

    @Override
    public void addTrade(Num tradeVolume, Num tradePrice) {
        throw new NotImplementedException();
    }

    @Override
    public void addPrice(Num price) {
        getLastBar().addPrice(price);
    }

    @Override
    public org.ta4j.core.BarSeries getSubSeries(int startIndex, int endIndex) {
        throw new NotImplementedException();
    }

    @Override
    public Num numOf(Number number) {
        return DecimalNum.valueOf(number);
    }

    @Override
    public Function<Number, Num> function() {
        return DecimalNum::valueOf;
    }

    public MarketHours getMarketHours() {
        var market = getStock().getMarket();
        return new MarketHours(market.getOpen(), market.getClose(), market.getTimezone());
    }

    public void refresh() {
        barData = null;
    }
}
