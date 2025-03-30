package org.tradingbot.stock;

import org.tradingbot.common.persistence.ProviderCodeEntity;
import org.tradingbot.common.persistence.StockEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StockBean {
    private final int id;
    private final String name;
    private final String currency;
    private final Map<Integer, String> providerCodes = new HashMap<>();
    private final String mnemonic;
    private final MarketBean market;

    public StockBean(StockEntity stockEntity, Collection<ProviderCodeEntity> providerCodes) {
        this.id = stockEntity.getId();
        this.name = stockEntity.getName();
        this.currency = stockEntity.getCurrency();
        for (var code : providerCodes) {
            this.providerCodes.put(code.getProvider().getId(), code.getCode());
        }
        this.mnemonic = stockEntity.getMnemonic();
        this.market = new MarketBean(stockEntity.getMarketEntity());

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public Map<Integer, String> getProviderCodes() {
        return providerCodes;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public MarketBean getMarket() {
        return market;
    }

    @Override
    public String toString() {
        return "StockBean{" + "id=" + id + ", name='" + name + '\'' + ", currency='" + currency + '\'' +
                ", providerCodes=" + providerCodes + ", mnemonic='" + mnemonic + '\'' + ", market=" + market + '}';
    }
}
