package org.tradingbot.common.provider;

import static java.util.Objects.requireNonNull;

public class StockData {
    private final String currency;
    private final String name;
    private final String mnemonic;
    private final int marketId;

    public StockData(String currency, String name, String mnemonic, int marketId) {
        this.currency = requireNonNull(currency);
        this.name = requireNonNull(name);
        this.mnemonic = requireNonNull(mnemonic);
        this.marketId = marketId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public int getMarketId() {
        return marketId;
    }
}
