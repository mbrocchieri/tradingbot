package org.tradingbot.tradingconfig;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TradingConfigStats {

    private BigDecimal lastBuyPrice;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime lastBuyDate;
    private BigDecimal percentTradePerf;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime globalStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime globalEndPrice;
    private BigDecimal percentGlobalConfigPerf;
    private BigDecimal percentNoStrategyPerf;
    private BigDecimal percentGlobalStockPerf;
    private BigDecimal startPrice;
    private BigDecimal endPrice;
    private int nbTrade;


    public BigDecimal getLastBuyPrice() {
        return lastBuyPrice;
    }

    public void setLastBuyPrice(BigDecimal lastBuyPrice) {
        this.lastBuyPrice = lastBuyPrice;
    }

    public ZonedDateTime getLastBuyDate() {
        return lastBuyDate;
    }

    public void setLastBuyDate(ZonedDateTime lastBuyDate) {
        this.lastBuyDate = lastBuyDate;
    }

    public BigDecimal getPercentTradePerf() {
        return percentTradePerf;
    }

    public void setPercentTradePerf(BigDecimal percentTradePerf) {
        this.percentTradePerf = percentTradePerf;
    }

    public ZonedDateTime getGlobalStartDate() {
        return globalStartDate;
    }

    public void setGlobalStartDate(ZonedDateTime globalStartDate) {
        this.globalStartDate = globalStartDate;
    }

    public ZonedDateTime getGlobalEndPrice() {
        return globalEndPrice;
    }

    public void setGlobalEndPrice(ZonedDateTime globalEndPrice) {
        this.globalEndPrice = globalEndPrice;
    }

    public BigDecimal getPercentGlobalConfigPerf() {
        return percentGlobalConfigPerf;
    }

    public void setPercentGlobalConfigPerf(BigDecimal percentGlobalConfigPerf) {
        this.percentGlobalConfigPerf = percentGlobalConfigPerf;
    }

    public BigDecimal getPercentNoStrategyPerf() {
        return percentNoStrategyPerf;
    }

    public void setPercentNoStrategyPerf(BigDecimal percentNoStrategyPerf) {
        this.percentNoStrategyPerf = percentNoStrategyPerf;
    }

    public BigDecimal getPercentGlobalStockPerf() {
        return percentGlobalStockPerf;
    }

    public void setPercentGlobalStockPerf(BigDecimal percentGlobalStockPerf) {
        this.percentGlobalStockPerf = percentGlobalStockPerf;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public void setNbTrade(int nbTrade) {
        this.nbTrade = nbTrade;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public int getNbTrade() {
        return nbTrade;
    }


}
