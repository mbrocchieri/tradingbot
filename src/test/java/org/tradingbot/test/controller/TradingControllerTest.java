package org.tradingbot.test.controller;

import org.junit.jupiter.api.Test;
import org.tradingbot.TradingBotApplicationTests;
import org.tradingbot.advice.*;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.TradingAdviceEntity;
import org.tradingbot.common.persistence.TradingConfigEntity;
import org.tradingbot.common.persistence.TradingRecordEntity;
import org.tradingbot.stock.StockBean;
import org.tradingbot.stock.StockCreateBean;
import org.tradingbot.trading.TradingBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;

class TradingControllerTest extends TradingBotApplicationTests {
    @Test
    public void testNominal() {

        var stock = new StockCreateBean();
        stock.setCode("A");
        stock.setProviderId(PROVIDER_TEST);
        var stockBean = (StockBean) stockController.createStock(stock);
        assertNotNull(stockBean);

        var stockEntity = stockRepository.findById(stockBean.getId()).orElseThrow();

        AdviserBean adviser = adviceController.createAdviser(new AdviserCreationBean("test"));
        assertNotNull(adviser);

        AdviceCategoryBean category = adviceController.createCategory(new AdviceCategoryCreationBean("test"));
        assertNotNull(category);

        var advice = (AdviceBean) adviceController.createAdvice(
                new AdviceCreationBean(stockBean.getId(), true, BigDecimal.ONE, LocalDate.now(),
                        adviser.getAdviserId(), category.getCategoryId()));
        assertNotNull(advice);

        TradingAdviceEntity tradingAdviceEntity = new TradingAdviceEntity();
        final var adviceEntity = adviceRepository.findById(advice.getId()).orElseThrow();
        tradingAdviceEntity.setAdvice(adviceEntity);
        tradingAdviceEntity.setDate(ZonedDateTime.now());
        tradingAdviceEntity.setAmount(TEN);
        tradingAdviceEntity.setBuy(true);
        tradingAdviceRepository.save(tradingAdviceEntity);


        var strategyEntity = getStrategyEntity();
        TradingConfigEntity tradingConfigEntity =
                new TradingConfigEntity(strategyEntity, stockEntity, true, ZonedDateTime.now(),
                        Interval.DAILY);
        tradingConfigEntity = tradingConfigRepository.save(tradingConfigEntity);

        TradingRecordEntity tradingRecordEntity =
                new TradingRecordEntity(tradingConfigEntity, true, ZonedDateTime.now(), TEN, TEN);
        tradingRecordRepository.save(tradingRecordEntity);


        tradingAdviceEntity = new TradingAdviceEntity();
        tradingAdviceEntity.setAdvice(adviceEntity);
        tradingAdviceEntity.setDate(ZonedDateTime.now());
        tradingAdviceEntity.setAmount(TEN);
        tradingAdviceEntity.setBuy(true);
        tradingAdviceRepository.save(tradingAdviceEntity);

        tradingRecordEntity = new TradingRecordEntity(tradingConfigEntity, true, ZonedDateTime.now(), TEN, TEN);
        tradingRecordRepository.save(tradingRecordEntity);


        tradingAdviceEntity = new TradingAdviceEntity();
        tradingAdviceEntity.setAdvice(adviceEntity);
        tradingAdviceEntity.setDate(ZonedDateTime.now());
        tradingAdviceEntity.setAmount(TEN);
        tradingAdviceEntity.setBuy(true);
        tradingAdviceRepository.save(tradingAdviceEntity);

        tradingRecordEntity = new TradingRecordEntity(tradingConfigEntity, true, ZonedDateTime.now(), TEN, TEN);
        tradingRecordRepository.save(tradingRecordEntity);

        tradingAdviceEntity = new TradingAdviceEntity();
        tradingAdviceEntity.setAdvice(adviceEntity);
        tradingAdviceEntity.setDate(ZonedDateTime.now());
        tradingAdviceEntity.setAmount(TEN);
        tradingAdviceEntity.setBuy(true);
        tradingAdviceRepository.save(tradingAdviceEntity);

        tradingRecordEntity = new TradingRecordEntity(tradingConfigEntity, true, ZonedDateTime.now(), TEN, TEN);
        tradingRecordRepository.save(tradingRecordEntity);

        tradingAdviceEntity = new TradingAdviceEntity();
        tradingAdviceEntity.setAdvice(adviceEntity);
        tradingAdviceEntity.setDate(ZonedDateTime.now());
        tradingAdviceEntity.setAmount(TEN);
        tradingAdviceEntity.setBuy(true);
        tradingAdviceRepository.save(tradingAdviceEntity);

        tradingRecordEntity = new TradingRecordEntity(tradingConfigEntity, true, ZonedDateTime.now(), TEN, TEN);
        tradingRecordRepository.save(tradingRecordEntity);

        tradingAdviceEntity = new TradingAdviceEntity();
        tradingAdviceEntity.setAdvice(adviceEntity);
        tradingAdviceEntity.setDate(ZonedDateTime.now());
        tradingAdviceEntity.setAmount(TEN);
        tradingAdviceEntity.setBuy(true);
        tradingAdviceRepository.save(tradingAdviceEntity);

        tradingRecordEntity = new TradingRecordEntity(tradingConfigEntity, true, ZonedDateTime.now(), TEN, TEN);
        tradingRecordRepository.save(tradingRecordEntity);

        var response = tradingController.getTradings(0, 10);
        assertNotNull(response);
        assertInstanceOf(List.class, response, response.toString());
        var list = (List<TradingBean>) response;
        assertNotNull(list);
        assertEquals(10, list.size());

        response = tradingController.getTradings(1, 10);
        assertNotNull(response);
        assertInstanceOf(List.class, response, response.toString());
        list = (List<TradingBean>) response;
        assertNotNull(list);
        assertEquals(2, list.size());

    }

}