package org.tradingbot.advice;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.AdviceCategoryEntity;
import org.tradingbot.common.persistence.AdviceEntity;
import org.tradingbot.common.persistence.AdviserEntity;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.stock.CandleRepository;
import org.tradingbot.stock.StockRepository;
import org.tradingbot.strategy.ErrorResponse;

@RestController
public class AdviceController {
    private static final Logger LOG = LoggerFactory.getLogger(AdviceController.class);
    @Autowired
    AdviceRepository adviceRepository;

    @Autowired
    AdviceService adviceService;


    @Autowired
    StockRepository stockRepository;

    @Autowired
    AdviserRepository adviserRepository;

    @Autowired
    AdviceCategoryRepository adviceCategoryRepository;

    @Autowired
    CandleRepository candleRepository;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> error(Throwable t) {
        LOG.warn(t.getMessage(), t);
        return ResponseEntity.internalServerError().body(new ErrorResponse(t.getMessage()));
    }

    @GetMapping("/advices/page/{page}/size/{size}")
    public List<AdviceBean> list(@PathVariable("page") int page, @PathVariable("size") int size) {
        var sort = Sort.by("adviceDate").descending().and(Sort.by("id").descending());
        var all = adviceRepository.findAllByDeletedNull(PageRequest.of(page, size, sort));
        List<AdviceBean> list = new ArrayList<>();
        for (var adviceEntity : all) {
            list.add(new AdviceBean(adviceEntity.getId(), adviceEntity.getStockEntity().getId(), adviceEntity.isBuy(),
                    adviceEntity.getPrice(), adviceEntity.getAdviserEntity().getId(), adviceEntity.getAdviceDate(),
                    adviceEntity.getCategoryEntity().getId()));
        }
        return list;
    }

    @GetMapping("/advices/count")
    public long count() {
        return adviceRepository.countByDeletedNull();
    }

    @PostMapping("/advices")
    public AdviceBean createAdvice(@RequestBody AdviceCreationBean adviceCreationBean) {
        var optionalStockEntity = stockRepository.findById(adviceCreationBean.getStockId());
        if (optionalStockEntity.isEmpty()) {
            throw new IllegalStateException("stockId " + adviceCreationBean.getStockId() + " not found");
        }
        var stockEntity = optionalStockEntity.get();

        var optionalAdviserEntity = adviserRepository.findById(adviceCreationBean.getAdviserId());
        if (optionalAdviserEntity.isEmpty()) {
            throw new IllegalStateException("adviserId " + adviceCreationBean.getAdviserId() + " not found");
        }
        var adviserEntity = optionalAdviserEntity.get();

        var optionalAdviceCategoryEntity = adviceCategoryRepository.findById(adviceCreationBean.getAdviceCategoryId());
        if (optionalAdviceCategoryEntity.isEmpty()) {
            throw new IllegalStateException(
                    "adviceCategoryId " + adviceCreationBean.getAdviceCategoryId() + " not found");
        }
        var adviceCategoryEntity = optionalAdviceCategoryEntity.get();

        var adviceEntity =
                new AdviceEntity(stockEntity, adviceCreationBean.isBuy(), adviceCreationBean.getPrice(), adviserEntity,
                        adviceCreationBean.getAdviceDate(), adviceCategoryEntity);
        adviceEntity = adviceRepository.save(adviceEntity);
        return new AdviceBean(adviceEntity.getId(), stockEntity.getId(), adviceEntity.isBuy(), adviceEntity.getPrice(),
                adviceEntity.getAdviserEntity().getId(), adviceEntity.getAdviceDate(),
                adviceEntity.getCategoryEntity().getId());
    }

    @DeleteMapping("/advices/{adviceId}")
    public void deleteAdvice(@PathVariable("adviceId") int adviceId) {
        adviceService.deleteAdvice(adviceId);
    }

    @PostMapping("/advices/advisers")
    public AdviserBean createAdviser(@RequestBody AdviserCreationBean adviserCreationBean) {
        var adviserEntity = new AdviserEntity(adviserCreationBean.getName());
        adviserEntity = adviserRepository.save(adviserEntity);
        return new AdviserBean(adviserEntity.getId(), adviserEntity.getName());
    }

    @GetMapping("/advices/advisers")
    public List<AdviserBean> listAdvisers() {
        var advisersEntity = adviserRepository.findAll();
        List<AdviserBean> list = new ArrayList<>();
        for (var adviserEntity : advisersEntity) {
            list.add(new AdviserBean(adviserEntity.getId(), adviserEntity.getName()));
        }
        return list;
    }

    @PostMapping("/advices/categories")
    public AdviceCategoryBean createCategory(@RequestBody AdviceCategoryCreationBean adviceCategoryCreationBean) {
        var adviceCategory = new AdviceCategoryEntity(adviceCategoryCreationBean.getName());
        var adviceCategoryEntity = adviceCategoryRepository.save(adviceCategory);
        return new AdviceCategoryBean(adviceCategoryEntity.getId(), adviceCategoryEntity.getName());
    }

    @GetMapping("/advices/categories")
    public List<AdviceCategoryBean> listCategories() {
        var adviceCategoryEntities = adviceCategoryRepository.findAll();
        List<AdviceCategoryBean> list = new ArrayList<>();
        for (var adviceCategoryEntity : adviceCategoryEntities) {
            list.add(new AdviceCategoryBean(adviceCategoryEntity.getId(), adviceCategoryEntity.getName()));
        }
        return list;
    }

    @GetMapping("/advices/lists")
    public List<Integer> getAdvicesAppliedList() {
        var all = adviceRepository.findAll();

        List<Integer> buyList = new ArrayList<>();
        for (var advice : all) {
            if (advice.isBuy()) {
                List<CandleEntity> candles =
                        candleRepository.findAllByStockIdAndIntervalOrderByStartTime(advice.getStockEntity().getId(),
                                Interval.DAILY);
                if (candles.get(candles.size() - 1).getClosePrice().compareTo(advice.getPrice()) < 0) {
                    buyList.add(advice.getStockEntity().getId());
                }
            }
        }
        return buyList;
    }
}
