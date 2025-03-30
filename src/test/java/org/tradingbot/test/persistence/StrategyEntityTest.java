package org.tradingbot.test.persistence;

import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.StrategyParameterEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tradingbot.common.bot.period.TradingPeriodEnum.CLOSE_MARKET;

public class StrategyEntityTest extends TradingBotApplicationTests {


    @Test
    public void testWithParameters() {

        Set<StrategyParameterEntity> parameterEntitySet = new HashSet<>();
        StrategyEntity strategy = new StrategyEntity("name", "xml", CLOSE_MARKET.getId());
        final var a = new StrategyParameterEntity("a", BigDecimal.ONE, strategy);
        parameterEntitySet.add(a);
        final var b = new StrategyParameterEntity("b", BigDecimal.valueOf(2), strategy);
        parameterEntitySet.add(b);
        strategy.getDefaultParameters().addAll(parameterEntitySet);
        strategy = strategyRepository.save(strategy);
        assertEquals(2, strategy.getDefaultParameters().size());
    }
}