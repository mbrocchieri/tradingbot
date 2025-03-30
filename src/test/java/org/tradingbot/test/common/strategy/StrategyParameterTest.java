package org.tradingbot.test.common.strategy;

import org.junit.jupiter.api.Test;
import org.tradingbot.common.strategy.Parameter;
import org.tradingbot.common.strategy.StrategyParameter;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class StrategyParameterTest {
    @Test
    public void test2Parameters() {
        var parameter = new StrategyParameter(
                Arrays.asList(
                        Parameter.rangeParameter("A", 1, 3),
                        Parameter.rangeParameter("B", 5, 7)));

        var it = parameter.valuesIterator();

        assertTrue(it.hasNext());
        var next = it.next();
        assertEquals(2, next.size());
        assertEquals(1, (int) next.get("A"));
        assertEquals(5, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(1, (int) next.get("A"));
        assertEquals(6, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(1, (int) next.get("A"));
        assertEquals(7, (int) next.get("B"));


        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(2, (int) next.get("A"));
        assertEquals(5, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(2, (int) next.get("A"));
        assertEquals(6, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(2, (int) next.get("A"));
        assertEquals(7, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(3, (int) next.get("A"));
        assertEquals(5, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(3, (int) next.get("A"));
        assertEquals(6, (int) next.get("B"));

        assertTrue(it.hasNext());
        next = it.next();
        assertEquals(2, next.size());
        assertEquals(3, (int) next.get("A"));
        assertEquals(7, (int) next.get("B"));

        assertFalse(it.hasNext());
    }
}
