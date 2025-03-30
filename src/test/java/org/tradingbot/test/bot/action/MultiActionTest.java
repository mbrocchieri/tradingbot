package org.tradingbot.test.bot.action;

import org.junit.jupiter.api.Test;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.tradingbot.common.bot.action.MultiAction;
import org.tradingbot.common.bot.action.NoAction;
import org.tradingbot.common.bot.action.TradingAction;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class MultiActionTest {

    @Test
    public void testNominal() {
        AtomicBoolean enter1 = new AtomicBoolean();
        AtomicBoolean enter2 = new AtomicBoolean();
        AtomicBoolean exit1 = new AtomicBoolean();
        AtomicBoolean exit2 = new AtomicBoolean();

        TradingAction ta1 = new TradingAction() {
            @Override
            public void exit(String strategyName, String symbol, Num closePrice) {
                exit1.set(true);
            }

            @Override
            public void enter(String strategyName, String symbol, Num closePrice) {
                enter1.set(true);
            }

            @Override
            public void sendStockCandlesCompareError(String symbol) {
                // do nothing
            }

            @Override
            public void sendConfigError(int id) {
                // do nothing
            }
        };

        TradingAction ta2 = new TradingAction() {
            @Override
            public void exit(String strategyName, String symbol, Num closePrice) {
                exit2.set(true);
            }

            @Override
            public void enter(String strategyName, String symbol, Num closePrice) {
                enter2.set(true);
            }

            @Override
            public void sendStockCandlesCompareError(String symbol) {
                // do nothing
            }

            @Override
            public void sendConfigError(int id) {
                // do nothing
            }
        };

        MultiAction multiAction = new MultiAction(ta1, ta2);
        assertFalse(enter1.get());
        assertFalse(enter2.get());
        assertFalse(exit1.get());
        assertFalse(exit2.get());

        multiAction.enter("a", "b", DecimalNum.valueOf(1));
        assertTrue(enter1.get());
        assertTrue(enter2.get());
        assertFalse(exit1.get());
        assertFalse(exit2.get());

        multiAction.exit("c", "d", DecimalNum.valueOf(1));
        assertTrue(enter1.get());
        assertTrue(enter2.get());
        assertTrue(exit1.get());
        assertTrue(exit2.get());
    }

    @Test
    public void testError() {
        try {
            new MultiAction();
            fail("must throw an exception");
        } catch (IllegalStateException e) {
            assertEquals("Multi Action must not be used when actions.length = 0", e.getMessage());
        }

        try {
            new MultiAction(NoAction.INSTANCE);
            fail("must throw an exception");
        } catch (IllegalStateException e) {
            assertEquals("Multi Action must not be used when actions.length = 1", e.getMessage());
        }
    }
}