package org.tradingbot.stock;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tradingbot.common.Candle;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.common.persistence.CandleId;
import org.tradingbot.common.persistence.ProviderCodeEntity;
import org.tradingbot.common.persistence.ProviderCodeId;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.persistence.StockMetadataEntity;
import org.tradingbot.common.persistence.StockMetadataId;
import org.tradingbot.common.provider.ProvidersSingleton;
import org.tradingbot.strategy.ErrorResponse;

@RestController
public class StockController {
    private static final Logger LOG = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private StockMetadataRepository stockMetadataRepository;

    @Autowired
    private ProviderCodeRepository providerCodeRepository;

    @Autowired
    private StockService stockService;

    public static CandleEntity toCandleEntity(Candle candle, int stockId, Interval interval) {
        var candleEntity = new CandleEntity();
        candleEntity.setStockId(stockId);
        candleEntity.setInterval(interval);
        candleEntity.setStartTime(candle.getDate());
        candle.updateCandleEntity(candleEntity);
        return candleEntity;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> error(Throwable t) {
        LOG.error(t.getMessage(), t);
        return ResponseEntity.internalServerError().body(new ErrorResponse(t.getMessage()));
    }

    @GetMapping("/stocks")
    public List<StockBean> listStocks() {
        var all = stockRepository.findAll(Sort.by("name"));
        List<StockBean> l = new ArrayList<>(all.size());
        for (var a : all) {
            Collection<ProviderCodeEntity> providerCodes = providerCodeRepository.findAllByStock(a);
            l.add(new StockBean(a, providerCodes));
        }
        return l;
    }

    @GetMapping("/stocks/{id}")
    public StockBean getStock(@PathVariable("id") int id) {
        Optional<StockEntity> optionalStockEntity = stockRepository.findById(id);
        if (optionalStockEntity.isEmpty()) {
            throw new IllegalStateException(("stock with id " + id + " is not found"));
        }
        var stockEntity = optionalStockEntity.get();
        Collection<ProviderCodeEntity> providerCodes = providerCodeRepository.findAllByStock(stockEntity);
        return new StockBean(stockEntity, providerCodes);
    }

    @PostMapping("/stocks/search")
    public List<StockBean> searchStock(@RequestBody StockSearchBean stockBean) {
        if (StringUtils.isBlank(stockBean.getSearch())) {
            return listStocks();
        }
        List<StockEntity> stockEntity = stockRepository.search(stockBean.getSearch().toUpperCase());
        List<StockBean> stockBeans = new ArrayList<>();
        for (var s : stockEntity) {
            Collection<ProviderCodeEntity> providerCodes = providerCodeRepository.findAllByStock(s);
            stockBeans.add(new StockBean(s, providerCodes));
        }
        return stockBeans;
    }

    @PostMapping("/stocks")
    public StockBean createStock(@RequestBody StockCreateBean stockBean) {
        return stockService.createStock(stockBean);
    }

    @PostMapping("/stocks/{stockId}/interval/{interval}/provider/{providerId}/refresh-candles")
    public void refreshCandles(@PathVariable("stockId") int stockId, @PathVariable("interval") Interval interval,
            @PathVariable("providerId") int providerId, @RequestBody RefreshBean refreshBean)
            throws IOException {
        Optional<StockEntity> optionalStockEntity = stockRepository.findById(stockId);
        if (optionalStockEntity.isEmpty()) {
            return;
        }
        var stockEntity = optionalStockEntity.get();
        updateLastCandle(stockId, interval, providerId);
        final var stockMetadataId = new StockMetadataId(stockId, interval);
        var optionalStockMetadata = stockMetadataRepository.findById(stockMetadataId);

        final var from = refreshBean.getFrom();
        final var to = refreshBean.getTo();
        final var provider = ProvidersSingleton.INSTANCE.getProvider(providerId);
        var providerCodeId = new ProviderCodeId(stockEntity.getId(), providerId);
        var optional = providerCodeRepository.findById(providerCodeId);
        if (optional.isEmpty()) {
            throw new IllegalStateException("Can not find provider code for id : " + providerCodeId);
        }
         final var stockProviderId = optional.get().getCode();

        if (optionalStockMetadata.isEmpty()) {

            var candles = provider.getCandles(stockProviderId, from, to, interval);
            insertCandles(stockId, candles, interval);
            var stockMetadata = new StockMetadataEntity();
            stockMetadata.setStockId(stockId);
            stockMetadata.setInterval(interval);
            stockMetadata.setStartTime(from);
            stockMetadata.setEndTime(to);
            stockMetadataRepository.save(stockMetadata);

        } else {

            var stockMetadata = optionalStockMetadata.get();
            var stockFrom = stockMetadata.getStartTime();
            var stockTo = stockMetadata.getEndTime();

            if (from.isBefore(stockFrom)) {
                var candles = provider.getCandles(stockProviderId, from, stockFrom, interval);
                if (!candles.isEmpty() && candles.get(candles.size() - 1).getDate().equals(stockFrom)) {
                    candles.remove(candles.size() - 1);
                }
                insertCandles(stockId, candles, interval);
                stockMetadata.setStartTime(from);
                stockMetadataRepository.save(stockMetadata);
            }

            if (to.isAfter(stockTo)) {
                var candles = new ArrayList<>(provider.getCandles(stockProviderId, stockTo, to, interval));
                if (!candles.isEmpty()) {
                    if (candles.get(0).getDate().equals(stockTo)) {
                        // in case trading bot stops in the middle of the day, and the last candle in
                        // database
                        // is not complete, the last candle in database is updated
                        var candleToUpdate = candles.remove(0);
                        var optionalCandle = candleRepository.findById(new CandleId(stockId, stockTo, interval));
                        if (optionalCandle.isEmpty()) {
                            throw new IOException();
                        }
                        var ce = optionalCandle.get();
                        candleToUpdate.updateCandleEntity(ce);
                        candleRepository.save(ce);
                    }
                    insertCandles(stockId, candles, interval);
                    stockMetadata.setEndTime(to);
                    stockMetadataRepository.save(stockMetadata);
                }
            }
        }
    }

    private void insertCandles(int stockId, List<Candle> candles, Interval interval) {
        for (var candle : candles) {
            var candleEntity = toCandleEntity(candle, stockId, interval);
            candleRepository.save(candleEntity);
        }
    }

    @GetMapping("/stocks/{stockId}/interval/{interval}/provider/{providerId}/update-last-candle")
    public Optional<CandleEntity> updateLastCandle(@PathVariable("stockId") int stockId,
            @PathVariable("interval") Interval interval,
            @PathVariable("providerId") int providerId) throws IOException {
        Optional<StockEntity> optionalStockEntity = stockRepository.findById(stockId);
        if (optionalStockEntity.isEmpty()) {
            throw new IOException(stockId + "not found");
        }
        var stockEntity = optionalStockEntity.get();
        var optionalLastCandle = candleRepository.findFirstByStockIdAndIntervalOrderByStartTimeDesc(stockId, interval);
        if (optionalLastCandle.isEmpty()) {
            return Optional.empty();
        }
        var lastCandle = optionalLastCandle.get();
        if (lastCandle.getUpdateDate().toLocalDate().compareTo(LocalDate.now()) != 0 &&
                lastCandle.getUpdateDate().toLocalDate().compareTo(lastCandle.getStartTime().toLocalDate()) == 0) {
            // var providerEntity = providerRepository.findById(providerId).get();
            var provider = ProvidersSingleton.INSTANCE.getProvider(providerId);
            var stockProviderCode = providerCodeRepository.findById(new ProviderCodeId(stockEntity.getId(), providerId))
                    .get().getCode();
            var l = provider.getCandles(stockProviderCode, lastCandle.getStartTime(),
                    lastCandle.getStartTime().plusDays(1), interval);
            if (!l.isEmpty()) {
                LOG.info("Last candle is updated for {}", stockEntity.getName());
                l.get(0).updateCandleEntity(lastCandle);
                return Optional.of(candleRepository.save(lastCandle));
            }
        }
        return Optional.empty();
    }

    @GetMapping("/stocks/{stockId}/interval/{interval}/candles")
    public List<CandleBean> getStockCandles(@PathVariable("stockId") int stockId,
            @PathVariable("interval") Interval interval) {
        Optional<StockEntity> optionalStockEntity = stockRepository.findById(stockId);
        if (optionalStockEntity.isEmpty()) {
            throw new IllegalStateException("no candle found for stockId " + stockId + " and interval " + interval);
        }
        var stockEntity = optionalStockEntity.get();
        List<CandleBean> list = new ArrayList<>();

        var l = candleRepository.findAllByStockIdAndIntervalOrderByStartTime(stockEntity.getId(), interval);
        for (var entity : l) {
            list.add(new CandleBean(entity));
        }
        return list;
    }

    @GetMapping("/stocks/{stockId}/price")
    public PriceBean getCurrentPrice(@PathVariable("stockId") int stockId) {

        Optional<StockEntity> stock = stockRepository.findById(stockId);
        if (stock.isEmpty()) {
            throw new IllegalStateException("stockId " + stockId + " not found");
        }
        Optional<CandleEntity> result = candleRepository.findFirstByStockIdAndIntervalOrderByStartTimeDesc(stockId,
                Interval.DAILY);
        if (result.isEmpty()) {
            throw new IllegalStateException("stockId " + stockId + " has no candle");
        }
        var candle = result.get();
        return new PriceBean(candle.getClosePrice(), stock.get().getCurrency());
    }

    @GetMapping("/stocks/{stockId}/interval/{interval}/provider/{providerId}/is-identical")
    public boolean isIdentical(@PathVariable("stockId") int stockId, @PathVariable("interval") Interval interval,
            @PathVariable("providerId") int providerId) throws IOException {
        var optionStockMetadata = stockMetadataRepository.findById(new StockMetadataId(stockId, interval));
        if (optionStockMetadata.isEmpty()) {
            throw new IOException();
        }
        var stockMetadataEntity = optionStockMetadata.get();
        var metaFrom = stockMetadataEntity.getStartTime();
        var metaTo = stockMetadataEntity.getEndTime();
        var provider = ProvidersSingleton.INSTANCE.getProvider(providerId);
        var optionalStockEntity = stockRepository.findById(stockId);
        if (optionalStockEntity.isEmpty()) {
            throw new IOException();
        }
        var stockEntity = optionalStockEntity.get();
        // var providerEntity = providerRepository.findById(providerId).get();
        var providerStockId = providerCodeRepository.findById(new ProviderCodeId(stockEntity.getId(), providerId)).get()
                .getCode();

        var providerCandles = provider.getCandles(providerStockId, metaFrom, metaTo, interval);
        var databaseCandleEntities = Objects.requireNonNull(getStockCandles(stockId, interval));
        Map<Instant, Candle> mapDatabase = new HashMap<>();
        Map<Instant, Candle> mapProvider = new HashMap<>();
        for (var ce : databaseCandleEntities) {
            mapDatabase.put(ce.getStartTime().toInstant(), Candle.toCandle(ce));
        }
        for (var c : providerCandles) {
            mapProvider.put(c.getDate().toInstant(), c);
        }

        Set<Instant> dates = new HashSet<>();
        dates.addAll(mapProvider.keySet());
        dates.addAll(mapDatabase.keySet());

        boolean errorDetected = false;
        for (var date : dates) {
            final var providerCandle = mapProvider.get(date);
            final var databaseCandle = mapDatabase.get(date);
            if (!Objects.equals(providerCandle, databaseCandle)) {
                LOG.error("Diff detected for stock {}, date {}\nProvider candle : {}\nDatabase candle : {}", stockId,
                        date, providerCandle, databaseCandle);
                errorDetected = true;
            }
        }

        return !errorDetected;
    }

}
