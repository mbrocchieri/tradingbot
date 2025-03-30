package org.tradingbot.advice;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.AdviceEntity;

import java.util.List;

public interface AdviceRepository extends JpaRepository<AdviceEntity, Integer> {
    List<AdviceEntity> findAllByDeletedNullAndExecutedFalse();

    @Modifying
    @Query("update AdviceEntity a set a.executed = true where a.id = :adviceId")
    int setExecuted(int adviceId);

    @Modifying
    @Query("update AdviceEntity a set a.deleted = CURRENT_TIMESTAMP where a.id = :adviceId")
    int setDeleted(int adviceId);

    List<AdviceEntity> findAllByDeletedNull(Pageable pageable);

    long countByDeletedNull();
}
