package org.tradingbot.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.Interval;
import org.tradingbot.common.persistence.CandleEntity;
import org.tradingbot.common.persistence.CandleId;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface CandleRepository extends JpaRepository<CandleEntity, CandleId> {
    // TODO voir requete générée et voir si optimisation possible avec index
    List<CandleEntity> findAllByStockIdAndIntervalOrderByStartTime(int stockId, Interval interval);

    List<CandleEntity> findAllByStockIdAndIntervalAndStartTimeBetweenOrderByStartTime(int stockId, Interval interval,
                                                                                      ZonedDateTime before,
                                                                                      ZonedDateTime after);

    Optional<CandleEntity> findFirstByStockIdAndIntervalOrderByStartTimeDesc(int stockId, Interval interval);

    Optional<CandleEntity> findByStockIdAndIntervalAndStartTime(int stockId, Interval interval,
                                                                ZonedDateTime startTime);

    Optional<CandleEntity> findFirstByStockIdAndIntervalAndStartTimeGreaterThan(int stockId, Interval interval,
                                                                                ZonedDateTime startTime);

}
