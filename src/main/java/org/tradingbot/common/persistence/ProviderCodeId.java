package org.tradingbot.common.persistence;

import java.io.Serializable;
import java.util.Objects;

public class ProviderCodeId implements Serializable {
    private int stock;
    private int provider;

    public ProviderCodeId() {
    }

    public ProviderCodeId(int stockId, int providerId) {
        this.stock = stockId;
        this.provider = providerId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderCodeId that = (ProviderCodeId) o;
        return stock == that.stock && provider == that.provider;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stock, provider);
    }

    @Override
    public String toString() {
        return "ProviderCodeId [provider=" + provider + ", stock=" + stock + "]";
    }

    
}
