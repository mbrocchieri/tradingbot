package org.tradingbot.tradingconfig;

import java.util.ArrayList;
import java.util.List;

public class TradingConfigSearchBean {

    private List<Integer> strategyIds = new ArrayList<>();
    private List<Integer> stockIds = new ArrayList<>();

    public TradingConfigSearchBean() {
    }

    public List<Integer> getStrategyIds() {
        return strategyIds;
    }

    public void setStrategyIds(List<Integer> strategyIds) {
        this.strategyIds = strategyIds;
    }

    public List<Integer> getStockIds() {
        return stockIds;
    }

    public void setStockIds(List<Integer> stockIds) {
        this.stockIds = stockIds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((stockIds == null) ? 0 : stockIds.hashCode());
        result = prime * result + ((strategyIds == null) ? 0 : strategyIds.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TradingConfigSearchBean other = (TradingConfigSearchBean) obj;
        if (stockIds == null) {
            if (other.stockIds != null)
                return false;
        } else if (!stockIds.equals(other.stockIds))
            return false;
        if (strategyIds == null) {
            if (other.strategyIds != null)
                return false;
        } else if (!strategyIds.equals(other.strategyIds))
            return false;
        return true;
    }

}
