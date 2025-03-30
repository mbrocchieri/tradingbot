package org.tradingbot.common.persistence;

import java.io.Serializable;
import java.util.Objects;

public class TradingViewId implements Serializable {

    private final int id;
    private final String type;

    public TradingViewId(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradingViewId that = (TradingViewId) o;
        return id == that.id && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
