package org.tradingbot.common.math;

import org.ta4j.core.num.Num;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Decimal implements Serializable {
    public static final Decimal ZERO = new Decimal(BigDecimal.ZERO);
    public static final Decimal ONE = new Decimal(BigDecimal.ONE);
    public static final Decimal TEN = new Decimal(BigDecimal.TEN);

    private final BigDecimal bigDecimal;

    public Decimal(BigDecimal bigDecimal) {
        this.bigDecimal = requireNonNull(bigDecimal);
    }

    public Decimal(String s) {
        this.bigDecimal = new BigDecimal(s);
    }

    public static Decimal valueOf(long v) {
        return new Decimal(BigDecimal.valueOf(v));
    }

    public static String getPrintablePrice(BigDecimal closePrice) {
        if (closePrice.compareTo(BigDecimal.valueOf(99999)) > 0) {
            return closePrice.toBigInteger().toString();
        } else {
            if (closePrice.compareTo(BigDecimal.ONE) > 0) {
                closePrice = closePrice.round(new MathContext(5, RoundingMode.HALF_UP));
            } else {
                closePrice = closePrice.round(new MathContext(4, RoundingMode.HALF_UP));
            }
            return closePrice.toString();
        }
    }
    public static String getPrintablePrice(Num closePrice) {
        return getPrintablePrice((BigDecimal) closePrice.getDelegate());
    }

    public boolean isGreaterThan(Decimal decimal) {
        return bigDecimal.compareTo(decimal.bigDecimal) > 0;
    }

    public boolean isGreaterThanOrEqual(Decimal decimal) {
        return bigDecimal.compareTo(decimal.bigDecimal) >= 0;
    }

    public boolean isLessThan(Decimal decimal) {
        return bigDecimal.compareTo(decimal.bigDecimal) < 0;
    }

    public boolean isLessThanOrEqual(Decimal decimal) {
        return bigDecimal.compareTo(decimal.bigDecimal) <= 0;
    }

    public boolean isBetween(Decimal decimal1, Decimal decimal2) {
        Decimal d1;
        Decimal d2;
        if (decimal2.isLessThan(decimal1)) {
            d1 = decimal2;
            d2 = decimal1;
        } else {
            d1 = decimal1;
            d2 = decimal2;
        }
        return d1.isLessThan(this) && d2.isGreaterThan(this);
    }

    public boolean isBetweenOrEqual(Decimal decimal1, Decimal decimal2) {
        Decimal d1;
        Decimal d2;
        if (decimal2.isLessThan(decimal1)) {
            d1 = decimal2;
            d2 = decimal1;
        } else {
            d1 = decimal1;
            d2 = decimal2;
        }
        return d1.isLessThanOrEqual(this) && d2.isGreaterThanOrEqual(this);
    }

    public double doubleValue() {
        return bigDecimal.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var decimal = (Decimal) o;
        return bigDecimal.compareTo(decimal.bigDecimal) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bigDecimal);
    }

    @Override
    public String toString() {
        return bigDecimal.toString();
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public static Decimal min(Decimal d1, Decimal d2) {
        if (d1.isLessThan(d2)) {
            return d1;
        }
        return d2;
    }

    public static Decimal max(Decimal d1, Decimal d2) {
        if (d1.isGreaterThan(d2)) {
            return d1;
        }
        return d2;
    }

}
