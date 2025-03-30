package org.tradingbot.common.bot;

import org.tradingbot.common.Interval;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.stock.RefreshBean;
import org.tradingbot.stock.StockController;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class StockReload {

    private final FinancialProvider provider;
    private final StockController stockController;
    private final Set<Integer> updatedStocks = new HashSet<>();

    public StockReload(FinancialProvider provider, StockController stockController) {
        this.provider = provider;
        this.stockController = stockController;
    }

    void reload(int stockId, ZonedDateTime date) throws IOException {
        if (!updatedStocks.contains(stockId)) {
            stockController.refreshCandles(stockId, Interval.DAILY, provider.getProviderId(),
                    new RefreshBean(date, ZonedDateTime.now()));
            updatedStocks.add(stockId);
        }
    }
}
