package org.tradingbot.test.common.math;

import org.junit.jupiter.api.Test;
import org.tradingbot.common.math.Decimal;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.tradingbot.common.math.Decimal.*;

public class DecimalTest {

    @Test
    public void isGreaterThan() {
        assertTrue(ONE.isGreaterThan(ZERO));
        assertFalse(ZERO.isGreaterThan(ONE));
        assertFalse(ONE.isGreaterThan(ONE));
    }

    @Test
    public void isGreaterThanOrEqual() {
        assertTrue(ONE.isGreaterThanOrEqual(ZERO));
        assertFalse(ZERO.isGreaterThanOrEqual(ONE));
        assertTrue(ONE.isGreaterThanOrEqual(ONE));
    }

    @Test
    public void isLessThan() {
        assertTrue(ZERO.isLessThan(ONE));
        assertFalse(ONE.isLessThan(ZERO));
        assertFalse(ZERO.isLessThan(ZERO));
    }

    @Test
    public void isLessThanOrEqual() {
        assertTrue(ZERO.isLessThanOrEqual(ONE));
        assertFalse(ONE.isLessThanOrEqual(ZERO));
        assertTrue(ZERO.isLessThanOrEqual(ZERO));
    }

    @Test
    public void isBetween() {
        assertTrue(ONE.isBetween(ZERO, TEN));
        assertTrue(ONE.isBetween(TEN, ZERO));
        assertFalse(ZERO.isBetween(TEN, ZERO));
    }

    @Test
    public void isBetweenOrEqual() {
        assertTrue(ONE.isBetweenOrEqual(ZERO, TEN));
        assertTrue(ONE.isBetweenOrEqual(TEN, ZERO));
        assertTrue(ZERO.isBetweenOrEqual(TEN, ZERO));
        assertFalse(TEN.isBetweenOrEqual(ZERO, ONE));
    }

    @Test
    public void stringConstructor() {
        assertEquals(new Decimal("1"), ONE);
    }

    @Test
    public void testValueOf() {
        assertEquals(Decimal.valueOf(1), ONE);
    }

    @Test
    public void testDoubleValue() {
        assertEquals(1.1, new Decimal("1.1").doubleValue(), 0.0);
    }

    @Test
    public void testBigDecimal() {
        assertEquals(BigDecimal.ONE, ONE.getBigDecimal());
    }

    @Test
    public void testToString() {
        assertEquals("1.1", new Decimal("1.1").toString());
    }

    @Test
    public void testGetPrintablePrice() {
        assertEquals("0.1235", Decimal.getPrintablePrice(new BigDecimal("0.123456789")));
        assertEquals("1.2346", Decimal.getPrintablePrice(new BigDecimal("1.23456789")));
        assertEquals("12.346", Decimal.getPrintablePrice(new BigDecimal("12.3456789")));
        assertEquals("123.46", Decimal.getPrintablePrice(new BigDecimal("123.456789")));
        assertEquals("1234.6", Decimal.getPrintablePrice(new BigDecimal("1234.56789")));
        assertEquals("12346", Decimal.getPrintablePrice(new BigDecimal("12345.6789")));
        assertEquals("123456", Decimal.getPrintablePrice(new BigDecimal("123456.789")));
        assertEquals("1234567", Decimal.getPrintablePrice(new BigDecimal("1234567.89")));
        assertEquals("12345678", Decimal.getPrintablePrice(new BigDecimal("12345678.9")));
        assertEquals("123456789", Decimal.getPrintablePrice(new BigDecimal("123456789")));

        assertEquals("0.1", Decimal.getPrintablePrice(new BigDecimal("0.1")));
        assertEquals("0.12", Decimal.getPrintablePrice(new BigDecimal("0.12")));
        assertEquals("0.123", Decimal.getPrintablePrice(new BigDecimal("0.123")));
        assertEquals("0.1234", Decimal.getPrintablePrice(new BigDecimal("0.1234")));
        assertEquals("0.1235", Decimal.getPrintablePrice(new BigDecimal("0.12345")));
        assertEquals("0.1234", Decimal.getPrintablePrice(new BigDecimal("0.12341")));

        assertEquals("1.0", Decimal.getPrintablePrice(new BigDecimal("1.0")));
        assertEquals("1.2", Decimal.getPrintablePrice(new BigDecimal("1.2")));
        assertEquals("1.23", Decimal.getPrintablePrice(new BigDecimal("1.23")));
        assertEquals("1.234", Decimal.getPrintablePrice(new BigDecimal("1.234")));
        assertEquals("1.2345", Decimal.getPrintablePrice(new BigDecimal("1.2345")));
    }

    @Test
    public void testHashCode() {
        assertEquals(62, ONE.hashCode());
        assertEquals(341, TEN.hashCode());
    }
}