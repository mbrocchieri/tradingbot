package org.tradingbot.strategy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.StrategyEntity;

public interface StrategyRepository extends JpaRepository<StrategyEntity, Integer> {

    StrategyEntity findByName(String strategyName);

    @Modifying
    @Query("update StrategyEntity s set s.alerts = :enable where s.id = :strategyId")
    void enableAlerts(int strategyId, boolean enable);

}
