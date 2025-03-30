package org.tradingbot.test.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.tradingbot.common.Constants.UTC;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.CandleEntity;

public class CandleEntityTest {

    @Test
    public void testToString() {
        CandleEntity candleEntity = new CandleEntity();
        candleEntity.setStockId(1);
        candleEntity.setInterval(Interval.DAILY);
        candleEntity.setStartTime(ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, UTC));
        candleEntity.setHighPrice(BigDecimal.valueOf(1));
        candleEntity.setLowPrice(BigDecimal.valueOf(2));
        candleEntity.setOpenPrice(BigDecimal.valueOf(3));
        candleEntity.setClosePrice(BigDecimal.valueOf(4));
        candleEntity.setAdjClosePrice(BigDecimal.valueOf(5));
        candleEntity.setVolume(6);
        candleEntity.setUpdateDate(ZonedDateTime.of(2020,1,1, 0,0,0,0, UTC));
        assertEquals("CandleEntity{stockId=1, startTime=2021-01-01T00:00Z[UTC], interval=DAILY, highPrice=1, lowPrice=2, openPrice=3, closePrice=4, adjClosePrice=5, volume=6, updateDate=2020-01-01T00:00Z[UTC]}", candleEntity.toString());
    }

    @Test
    public void testEquals() {
        CandleEntity candleEntity = new CandleEntity();
        candleEntity.setStockId(1);
        candleEntity.setInterval(Interval.DAILY);
        candleEntity.setStartTime(ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, UTC));
        candleEntity.setHighPrice(BigDecimal.valueOf(1));
        candleEntity.setLowPrice(BigDecimal.valueOf(2));
        candleEntity.setOpenPrice(BigDecimal.valueOf(3));
        candleEntity.setClosePrice(BigDecimal.valueOf(4));
        candleEntity.setAdjClosePrice(BigDecimal.valueOf(5));
        candleEntity.setVolume(6);
        candleEntity.setUpdateDate(ZonedDateTime.of(2020,1,1, 0,0,0,0, UTC));
        assertEquals(candleEntity, candleEntity);
        CandleEntity candleEntity2 = new CandleEntity();
        candleEntity2.setStockId(1);
        candleEntity2.setInterval(Interval.DAILY);
        candleEntity2.setStartTime(ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, UTC));
        candleEntity2.setHighPrice(BigDecimal.valueOf(1));
        candleEntity2.setLowPrice(BigDecimal.valueOf(2));
        candleEntity2.setOpenPrice(BigDecimal.valueOf(3));
        candleEntity2.setClosePrice(BigDecimal.valueOf(4));
        candleEntity2.setAdjClosePrice(BigDecimal.valueOf(5));
        candleEntity2.setVolume(6);
        candleEntity2.setUpdateDate(ZonedDateTime.of(2020,1,1, 0,0,0,0, UTC));
        assertEquals(candleEntity, candleEntity2);
        CandleEntity candleEntity3 = new CandleEntity();
        assertNotEquals(candleEntity, candleEntity3);
    }
}
