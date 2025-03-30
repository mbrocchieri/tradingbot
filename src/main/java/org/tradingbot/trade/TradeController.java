package org.tradingbot.trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.tradingbot.AbstractController;
import org.tradingbot.common.persistence.TradeEntity;
import org.tradingbot.common.repository.TradeRepository;
import org.tradingbot.stock.StockRepository;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@RestController
public class TradeController extends AbstractController {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/api/v1/trades/page/{page}/size/{size}")
    public List<TradeEntity> getAllTrades(@PathVariable("page") int page, @PathVariable("size") int size) {
        final var sort = Sort.by("date").descending();
        final var pageRequest = PageRequest.of(page, size, sort);
        return tradeRepository.findAll(pageRequest).toList();
    }
    @GetMapping("/api/v1/trades/count")
    public long count() {
        return tradeRepository.count();
    }

    @PostMapping("/api/v1/trades")
    @Transactional(propagation = REQUIRES_NEW)
    public TradeEntity createTrade(@RequestBody TradeUpdateBean tradeUpdateBean) {
        var tradeEntity = new TradeEntity();
        return saveTrade(tradeUpdateBean, tradeEntity);
    }

    @PutMapping("/api/v1/trades/{id}")
    @Transactional(propagation = REQUIRES_NEW)
    public TradeEntity updateTrade(@PathVariable("id") int id, @RequestBody TradeUpdateBean tradeUpdateBean) {
        return saveTrade(tradeUpdateBean, tradeRepository.getById(id));
    }

    private TradeEntity saveTrade(TradeUpdateBean tradeUpdateBean, TradeEntity tradeEntity) {
        tradeEntity.setDate(tradeUpdateBean.getDate());
        tradeEntity.setPaperTrading(tradeUpdateBean.isPaperTrading());
        tradeEntity.setBuy(tradeUpdateBean.isBuy());
        tradeEntity.setFees(tradeUpdateBean.getFees());
        tradeEntity.setComment(tradeUpdateBean.getComment());
        tradeEntity.setStockPrice(tradeUpdateBean.getStockPrice());
        tradeEntity.setQuantity(tradeUpdateBean.getQuantity());
        tradeEntity.setStock(stockRepository.getById(tradeUpdateBean.getStockId()));
        return tradeRepository.save(tradeEntity);
    }
}
