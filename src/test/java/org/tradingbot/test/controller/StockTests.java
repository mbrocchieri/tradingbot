package org.tradingbot.test.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.math.Decimal;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.common.persistence.CandleId;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.StockData;
import org.tradingbot.stock.*;
import org.tradingbot.test.provider.ProvidersSingletonUtils;
import org.tradingbot.util.FinancialProviderFile;
import org.tradingbot.util.FinancialProviderMock;
import org.tradingbot.util.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;
import static org.tradingbot.common.Constants.UTC;
import static org.tradingbot.common.Interval.DAILY;

class StockTests extends TradingBotApplicationTests {
    private static final String PROVIDER_STOCK_ID = "A";

    @Test
    void testStockAdd() {

        var list = stockController.listStocks();
        assertEquals(0, list.size());

        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_TEST);
        var stockEntity = (StockBean) stockController.createStock(stock);

        list = stockController.listStocks();

        assertEquals(1, list.size());
        var actualStock = list.get(0);
        assertEquals("A_ID", actualStock.getName());
        assertEquals("EUR", actualStock.getCurrency());
        var codeEntities = actualStock.getProviderCodes();
        assertEquals(1, codeEntities.size());
        assertEquals("A", codeEntities.get(-1));

        // var request =
        // MockMvcRequestBuilders.post("/stocks").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content("");
        // client.post().uri("/stocks").accept(MediaType.APPLICATION_JSON).exchange().expectBody(StockBean.class).consumeWith(
        // result -> {
        // var actualEntity = result.getResponseBody();
        // assertNotNull(actualEntity);
        // assertEquals("A_ID", actualEntity.getName());
        // assertEquals("EUR", actualEntity.getCurrency());

        // }
        // );
        // var toto = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var actualEntity = (StockBean) stockController.getStock(stockEntity.getId());
        assertNotNull(actualEntity);
        assertEquals("A_ID", actualEntity.getName());
        assertEquals("EUR", actualEntity.getCurrency());
        codeEntities = actualEntity.getProviderCodes();
        assertEquals(1, codeEntities.size());
        assertEquals("A", codeEntities.get(-1));

    }

    @Test
    void testStockAddAlreadyExists() {
        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_TEST);
        stockController.createStock(stock);

        stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_TEST);
        try {
            stockController.createStock(stock);
            Assertions.fail("must throw an exception");
        } catch (DataIntegrityViolationException e) {
            // OK
        }
    }

    @Test
    void testUnknownProviderCode() {
        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_FAIL);
        try {
            stockController.createStock(stock);
            Assertions.fail(("must throw an exception"));
        } catch (IllegalStateException e) {
            // OK
        }

    }

    @Test
    void testStockCandles() throws IOException {
        int providerId = -2;
        ProvidersSingletonUtils.addProvider("providerTestStockCandles",
                new FinancialProviderFile(-2, Utils.getFile("CGG.csv"), "A"), providerRepository);

        final var from = ZonedDateTime.parse("2021-01-04T00:00:00+00:00");
        final var to = ZonedDateTime.parse("2021-01-31T00:00:00+00:00");

        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stock);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));
        var candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(20, candles.size());
    }

    @Test
    void testStockCandlesOrderedByStartDate() throws IOException {
        final int providerId = -2;
        ProvidersSingletonUtils.addProvider("providerTestStockCandles",
                new FinancialProviderFile(-2, Utils.getFile("CGG.csv"), "A"), providerRepository);

        final var from = ZonedDateTime.parse("2021-01-04T00:00:00+00:00");
        final var to = ZonedDateTime.parse("2021-01-31T00:00:00+00:00");

        final var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(providerId);
        final var stockEntity = (StockBean) stockController.createStock(stock);
        assertNotNull(stockEntity);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));
        final var candleEntity = new CandleEntity();
        candleEntity.setInterval(DAILY);
        candleEntity.setStartTime(ZonedDateTime.parse("2020-01-04T00:00:00+00:00"));
        candleEntity.setStockId(stockEntity.getId());
        candleEntity.setClosePrice(BigDecimal.ONE);
        candleEntity.setOpenPrice(BigDecimal.ONE);
        candleEntity.setClosePrice(BigDecimal.ONE);
        candleEntity.setLowPrice(BigDecimal.ONE);
        candleEntity.setHighPrice(BigDecimal.ONE);
        candleEntity.setVolume(1L);
        candleEntity.setUpdateDate(ZonedDateTime.now());
        candleRepository.save(candleEntity);

        final var candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(21, candles.size());
        assertTrue(candles.get(0).getStartTime().isEqual(ZonedDateTime.parse("2020-01-04T00:00:00+00:00")));
        for (int i = 1; i < 21; i++) {
            assertTrue(candles.get(i).getStartTime().isAfter(candles.get(i - 1).getStartTime()));
        }
    }

    @Test
    public void testGetBefore() throws IOException {

        var from = ZonedDateTime.parse("2020-03-01T00:00:00+00:00");
        var to = ZonedDateTime.parse("2020-04-01T00:00:00+00:00");
        var providerId = -2;
        var provider = new FinancialProviderMock(providerId);
        ProvidersSingletonUtils.addProvider("ProviderTmp", provider, providerRepository);
        final var stockBean = new StockCreateBean();

        stockBean.setCode(PROVIDER_STOCK_ID);
        stockBean.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stockBean);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));

        var candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(provider.getCandles(PROVIDER_STOCK_ID, from, to, DAILY).size(), candles.size());

        var from2 = ZonedDateTime.parse("2020-01-01T00:00:00+00:00");
        var to2 = ZonedDateTime.parse("2020-02-01T00:00:00+00:00");
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from2, to2));
        candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(64, candles.size());
    }

    @Test
    public void testGetBeforeStockIntroduction() throws IOException {
        var providerId = -2;
        var provider = new FinancialProviderMock(providerId);
        ProvidersSingletonUtils.addProvider("ProviderTmp", provider, providerRepository);

        var from = ZonedDateTime.of(LocalDateTime.of(2010, Month.MARCH, 1, 0, 0, 0), UTC);
        var to = ZonedDateTime.of(LocalDateTime.of(2011, Month.APRIL, 1, 0, 0, 0), UTC);
        final var stockBean = new StockCreateBean();
        stockBean.setProviderId(providerId);
        stockBean.setCode(PROVIDER_STOCK_ID);
        var stockEntity = (StockBean) stockController.createStock(stockBean);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(from, to));

        final var body = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }

    @Test
    public void testGetBeforeStockIntroductionAfterAlreadyLoaded() throws IOException {

        // load candles into database
        var from = ZonedDateTime.of(LocalDateTime.of(2020, JANUARY, 1, 0, 0, 0), UTC);
        var to = ZonedDateTime.of(LocalDateTime.of(2021, Month.APRIL, 1, 0, 0, 0), UTC);

        var providerId = -2;
        var provider = new FinancialProviderMock(providerId);
        ProvidersSingletonUtils.addProvider("ProviderTmp", provider, providerRepository);
        final var stockBean = new StockCreateBean();
        stockBean.setProviderId(providerId);
        stockBean.setCode(PROVIDER_STOCK_ID);
        var stockEntity = (StockBean) stockController.createStock(stockBean);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));

        var candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);

        assertEquals(327, candles.size());

        from = ZonedDateTime.of(LocalDateTime.of(2010, Month.MARCH, 1, 0, 0, 0), UTC);
        to = ZonedDateTime.of(LocalDateTime.of(2011, Month.APRIL, 1, 0, 0, 0), UTC);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));

        candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);

        assertEquals(327, candles.size());
    }

    @Test
    public void testUpdate() throws IOException {
        var from = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, UTC);
        var to = ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, UTC);
        var providerId = -1;
        FinancialProvider financialProvider = new FinancialProvider() {
            @Override
            public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
                return List.of(new Candle.Builder().date(from).open(new Decimal("1.0")).high(new Decimal("2.0"))
                        .low(new Decimal("1.0")).close(new Decimal("2.0")).adjClose(new Decimal("2.0")).volume(2L)
                        .build(),
                        new Candle.Builder().date(to).open(new Decimal("1.0")).high(new Decimal("3.0"))
                                .low(new Decimal("1.0")).close(new Decimal("3.0")).adjClose(new Decimal("2.0"))
                                .volume(2L)
                                .build());
            }

            @Override
            public StockData getStockData(String providerCode) {
                return new StockData("EUR", providerCode, providerCode, 1);
            }

            @Override
            public int getProviderId() {
                return providerId;
            }
        };
        ProvidersSingletonUtils.addProvider("ProviderTmp", financialProvider, providerRepository);
        var stockCreation = new StockCreateBean();
        stockCreation.setCode(PROVIDER_STOCK_ID);
        stockCreation.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stockCreation);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(from, to));
        final var candleSeries = new RestBarSeries(stockController, stockEntity.getId());
        assertEquals(2, candleSeries.getBarData().size());
    }

    /**
     * Simulate what happen when in one day when LiveTradingBot runs on a daily
     * configuration
     */
    @Test
    public void testSeveralUpdateDaily() throws IOException {

        AtomicInteger step = new AtomicInteger();
        var date1 = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, UTC);
        var date2 = ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, UTC);
        var date3 = ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, UTC);

        final var candle1 = new Candle.Builder().date(date1).open(new Decimal("1.0")).high(new Decimal("1.0"))
                .low(new Decimal("1.0")).close(new Decimal("1.0")).adjClose(new Decimal("1.0")).volume(1L).build();
        final var candle2 = new Candle.Builder().date(date2).open(new Decimal("2.0")).high(new Decimal("2.0"))
                .low(new Decimal("2.0")).close(new Decimal("2.0")).adjClose(new Decimal("2.0")).volume(1L).build();
        final var candle31 = new Candle.Builder().date(date3).open(new Decimal("3.1")).high(new Decimal("3.1"))
                .low(new Decimal("3.1")).close(new Decimal("3.1")).adjClose(new Decimal("3.1")).volume(31L).build();
        final var candle32 = new Candle.Builder().date(date3).open(new Decimal("3.2")).high(new Decimal("3.2"))
                .low(new Decimal("3.2")).close(new Decimal("3.2")).adjClose(new Decimal("3.2")).volume(32L).build();

        FinancialProvider financialProvider = new FinancialProvider() {
            @Override
            public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
                assertTrue(step.get() >= 0);
                if (step.get() == 0) {
                    return Arrays.asList(candle1, candle2);
                }
                if (step.get() == 1) {
                    return Collections.singletonList(candle31);
                } else {
                    return Collections.singletonList(candle32);
                }
            }

            @Override
            public StockData getStockData(String providerCode) {
                return new StockData("EUR", "test", "test", 1);
            }

            @Override
            public int getProviderId() {
                return -2;
            }
        };

        ProvidersSingletonUtils.addProvider("ProviderTest", financialProvider, providerRepository);
        var stockCreation = new StockCreateBean();
        stockCreation.setCode(PROVIDER_STOCK_ID);
        stockCreation.setProviderId(-2);
        var stockEntity = (StockBean) stockController.createStock(stockCreation);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(date1, date2));

        step.set(1);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2,
                new RefreshBean(date1, ZonedDateTime.of(2021, 1, 3, 1, 0, 0, 0, UTC)));
        var candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(3, candles.size());
        assertEquals(candle1, Candle.toCandle(candles.get(0)));
        assertEquals(candle2, Candle.toCandle(candles.get(1)));
        assertEquals(candle31, Candle.toCandle(candles.get(2)));

        // Stock from database must be equals
        step.set(-1);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(date1, date3));
        candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);
        assertEquals(3, candles.size());
        assertEquals(candle1, Candle.toCandle(candles.get(0)));
        assertEquals(candle2, Candle.toCandle(candles.get(1)));
        assertEquals(candle31, Candle.toCandle(candles.get(2)));

        step.set(2);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2,
                new RefreshBean(date1, ZonedDateTime.of(2021, 1, 3, 2, 0, 0, 0, UTC)));
        candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);

        assertEquals(3, candles.size());
        assertEquals(candle1, Candle.toCandle(candles.get(0)));
        assertEquals(candle2, Candle.toCandle(candles.get(1)));
        assertEquals(candle32, Candle.toCandle(candles.get(2)));

        // Stock from database must be equals
        step.set(-1);
        stockController.refreshCandles(stockEntity.getId(), DAILY, -2, new RefreshBean(date1, date3));
        candles = stockController.getStockCandles(stockEntity.getId(), DAILY);
        assertNotNull(candles);

        assertEquals(3, candles.size());
        assertEquals(candle1, Candle.toCandle(candles.get(0)));
        assertEquals(candle2, Candle.toCandle(candles.get(1)));
        assertEquals(candle32, Candle.toCandle(candles.get(2)));
    }

    /**
     * Reproduce case when trading bot stopped before day is finished, and trading
     * bot starts few days later.
     * The last candle in database needs to be updated
     */
    @Test
    public void testLoadWithLastCandleInBddNotComplete() throws IOException {

        List<Candle> candles = new ArrayList<>();
        var date1 = ZonedDateTime.parse("2021-01-01T00:00:00+00:00");
        var date2 = ZonedDateTime.parse("2021-01-02T00:00:00+00:00");
        var date3 = ZonedDateTime.parse("2021-01-03T00:00:00+00:00");

        final var candle1 = new Candle.Builder().date(date1).open(new Decimal("1.0")).high(new Decimal("1.0"))
                .low(new Decimal("1.0")).close(new Decimal("1.0")).adjClose(new Decimal("1.0")).volume(1L).build();
        final var candle20 = new Candle.Builder().date(date2).open(new Decimal("2.0")).high(new Decimal("2.0"))
                .low(new Decimal("2.0")).close(new Decimal("2.0")).adjClose(new Decimal("2.0")).volume(1L).build();
        final var candle21 = new Candle.Builder().date(date2).open(new Decimal("2.1")).high(new Decimal("2.1"))
                .low(new Decimal("2.1")).close(new Decimal("2.1")).adjClose(new Decimal("2.1")).volume(21L).build();
        final var candle3 = new Candle.Builder().date(date3).open(new Decimal("3.0")).high(new Decimal("3.0"))
                .low(new Decimal("3.0")).close(new Decimal("3.0")).adjClose(new Decimal("3.0")).volume(30L).build();

        var providerId = -1;

        var financialProvider = new FinancialProvider() {
            @Override
            public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
                return candles;
            }

            @Override
            public StockData getStockData(String providerCode) {
                return new StockData("EUR", "A", "A", 1);
            }

            @Override
            public int getProviderId() {
                return providerId;
            }
        };

        ProvidersSingletonUtils.addProvider("ProviderTest", financialProvider, providerRepository);
        var stockCreation = new StockCreateBean();
        stockCreation.setCode(PROVIDER_STOCK_ID);
        stockCreation.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stockCreation);

        candles.add(candle1);
        candles.add(candle20);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(date1, date2));

        candles.clear();
        candles.add(candle21);
        candles.add(candle3);

        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(date1, date3));

        candles.clear();
        candles.add(candle1);
        candles.add(candle21);
        candles.add(candle3);
        assertTrue(stockController.isIdentical(stockEntity.getId(), DAILY, providerId));

    }

    /**
     * Reproduce case with Yahoo. The last candle get the current date do not have
     * the same values as the same
     * candle get the next day
     */
    @Test
    public void testBugYahooCandleNotSameValues() throws IOException {

        List<Candle> candles = new ArrayList<>();
        var date1 = ZonedDateTime.parse("2021-01-01T00:00:00+00:00");
        var date2 = ZonedDateTime.parse("2021-01-02T00:00:00+00:00");
        var date3 = ZonedDateTime.parse("2021-01-03T00:00:00+00:00");

        final var candle1 = new Candle.Builder().date(date1).open(new Decimal("1.0")).high(new Decimal("1.0"))
                .low(new Decimal("1.0")).close(new Decimal("1.0")).adjClose(new Decimal("1.0")).volume(1L).build();
        final var candle20 = new Candle.Builder().date(date2).open(new Decimal("2.0")).high(new Decimal("2.0"))
                .low(new Decimal("2.0")).close(new Decimal("2.0")).adjClose(new Decimal("2.0")).volume(1L).build();
        final var candle21 = new Candle.Builder().date(date2).open(new Decimal("2.1")).high(new Decimal("2.1"))
                .low(new Decimal("2.1")).close(new Decimal("2.1")).adjClose(new Decimal("2.1")).volume(21L).build();
        final var candle3 = new Candle.Builder().date(date3).open(new Decimal("2.1")).high(new Decimal("2.1"))
                .low(new Decimal("2.1")).close(new Decimal("2.1")).adjClose(new Decimal("2.1")).volume(21L).build();

        var providerId = -1;

        var financialProvider = new FinancialProvider() {
            int i = 0;

            @Override
            public List<Candle> getCandles(String symbol, ZonedDateTime from, ZonedDateTime to, Interval interval) {
                if (i++ == 1) {
                    return List.of(candle21, candle3);
                }
                return candles;
            }

            @Override
            public StockData getStockData(String providerCode) {
                return new StockData("EUR", "A", "A", 1);
            }

            @Override
            public int getProviderId() {
                return providerId;
            }
        };

        candles.add(candle1);
        candles.add(candle20);

        ProvidersSingletonUtils.addProvider("ProviderTest", financialProvider, providerRepository);
        var stockCreation = new StockCreateBean();
        stockCreation.setCode(PROVIDER_STOCK_ID);
        stockCreation.setProviderId(providerId);
        var stockEntity = (StockBean) stockController.createStock(stockCreation);
        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(date1, date2));

        candles.clear();
        var candleEntity = candleRepository.findById(new CandleId(stockEntity.getId(), date2, DAILY)).orElseThrow();
        candleEntity.setUpdateDate(date2);
        candleRepository.save(candleEntity);

        stockController.refreshCandles(stockEntity.getId(), DAILY, providerId, new RefreshBean(date1, date2));

        candles.clear();
        candles.add(candle1);
        candles.add(candle21);
        assertTrue(stockController.isIdentical(stockEntity.getId(), DAILY, providerId));
    }

    @Test
    public void testSearch() {
        var stock = new StockCreateBean();
        stock.setCode("ABC");
        stock.setProviderId(PROVIDER_TEST);
        stockController.createStock(stock);
        stock = new StockCreateBean();
        stock.setCode("CDE");
        stock.setProviderId(PROVIDER_TEST);
        stockController.createStock(stock);
        final var stockBean = new StockSearchBean();
        stockBean.setSearch("c");
        List<StockBean> list = stockController.searchStock(stockBean);
        assertEquals(2, list.size());

        stockBean.setSearch("C");
        list = stockController.searchStock(stockBean);
        assertEquals(2, list.size());

        stockBean.setSearch("a");
        list = stockController.searchStock(stockBean);
        assertEquals(1, list.size());
    }
}
