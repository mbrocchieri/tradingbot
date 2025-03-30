package org.tradingbot.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ta4j.core.BaseBarSeries;
import org.tradingbot.common.bot.period.TradingPeriodEnum;
import org.tradingbot.common.persistence.PerfByPeriodViewEntity;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.StrategyParameterEntity;
import org.tradingbot.common.persistence.TradingStatisticsEntity;
import org.tradingbot.common.repository.TradingStatisticsRepository;
import org.tradingbot.common.strategy.Strategy;
import org.tradingbot.common.strategy.StrategyInitException;
import org.tradingbot.common.strategy.StrategyParameter;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class StrategyController {
    private static final Logger LOG = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private TradingStatisticsRepository tradingStatisticsRepository;

    @Autowired
    private PeriodPerfRepository periodPerfRepository;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> error(Throwable t) {
        LOG.error(t.getMessage(), t);
        return ResponseEntity.internalServerError().body(new ErrorResponse(t.getMessage()));
    }


    @GetMapping("/strategies")
    public List<StrategyBean> listStrategies() {

        List<StrategyBean> list = new ArrayList<>();

        List<Integer> strategyIds = new ArrayList<>();
        final var strategyEntityList = strategyRepository.findAll(Sort.by("name"));
        for (var strategyEntity : strategyEntityList) {
            strategyIds.add(strategyEntity.getId());
        }

        var statistics = tradingStatisticsRepository.findStrategyStatistics(strategyIds);
        Map<Integer, TradingStatisticsEntity> idToStats = new HashMap<>();
        for (var stat : statistics) {
            idToStats.put(stat.getId(), stat);
        }

        Map<Integer, Map<String, BigDecimal>> stratIdToPeriodPerf = new HashMap<>();
        List<PerfByPeriodViewEntity> perfList = periodPerfRepository.findAllPeriods();
        for (var p : perfList) {
            stratIdToPeriodPerf.computeIfAbsent(p.getStrategyId(), (ignore) -> new HashMap<>());
            stratIdToPeriodPerf.get(p.getStrategyId()).put(p.getPeriod(), p.getPerf());
        }

        for (var strategyEntity : strategyEntityList) {
            List<StrategyParameterBean> defaultParameters = new ArrayList<>();
            for (var paramEntity : strategyEntity.getDefaultParameters()) {
                defaultParameters.add(new StrategyParameterBean(paramEntity.getId(), paramEntity.getName(),
                        paramEntity.getDefaultValue()));
            }
            final var stats = idToStats.get(strategyEntity.getId());
            BigDecimal perf = null;
            if (stats != null) {
                perf = stats.getPercentPerf();
            }

            final var strategyBean =
                    new StrategyBean(strategyEntity.getId(), strategyEntity.getName(), strategyEntity.getXml(),
                            defaultParameters, strategyEntity.isAlerts(), perf,
                            stratIdToPeriodPerf.get(strategyEntity.getId()),
                            TradingPeriodEnum.getFromId(strategyEntity.getTradingPeriod()));
            list.add(strategyBean);
        }

        return list;
    }

    @GetMapping("/strategies/{id}")
    public StrategyEntity getStrategy(@PathVariable("id") int id) {
        Optional<StrategyEntity> strategyEntity = strategyRepository.findById(id);
        if (strategyEntity.isEmpty()) {
            throw new IllegalStateException("strategy with id " + id + " is not found");
        }
        return strategyEntity.get();
    }

    @GetMapping("/strategies/{id}/alerts/enable")
    public void enableStrategyAlerts(@PathVariable("id") int id) {
        updateStrategyAlerts(id, true);
    }

    @GetMapping("/strategies/{id}/alerts/disable")
    public void disableStrategyAlerts(@PathVariable("id") int id) {
        updateStrategyAlerts(id, false);
    }

    public void updateStrategyAlerts(int strategyId, boolean enable) {
        strategyService.enableAlerts(strategyId, enable);
    }

    @DeleteMapping("/strategies/{id}")
    public void deleteStrategy(@PathVariable("id") int id) {
        strategyService.delete(id);
    }

    @PostMapping("/strategies")
    public StrategyEntity createStrategy(@RequestBody StrategyCreationBean strategyBean) {

        if (!isXmlValid(strategyBean.getXml(), strategyBean.getDefaultParameters())) {
            throw new IllegalStateException("xml is not valid");
        }

        var strategyEntity = new StrategyEntity(strategyBean.getName(), strategyBean.getXml(),
                strategyBean.getTradingPeriod().getId(),
                StrategyParameter.toString(strategyBean.getTradingPeriodParameters()));

        for (var parameter : strategyBean.getDefaultParameters()) {
            strategyEntity.getDefaultParameters()
                    .add(new StrategyParameterEntity(parameter.getName(), parameter.getDefaultValue(), strategyEntity));
        }

        return strategyService.save(strategyEntity);
    }

    private boolean isXmlValid(String xml, Collection<StrategyParameterCreationBean> parameters) {

        Map<String, BigDecimal> map = new HashMap<>();
        parameters.forEach(p -> map.put(p.getName(), p.getDefaultValue()));
        return isXmlValid(xml, map);
    }

    private boolean isXmlValid(String xml, Map<String, BigDecimal> parameters) {
        var strategy = new Strategy("validate", xml, parameters);
        try {
            strategy.testStrategy(new BaseBarSeries());
            return true;
        } catch (StrategyInitException e) {
            LOG.error("Error validating xml", e);
            return false;
        }
    }
}
