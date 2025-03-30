package org.tradingbot.advice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.AdviceCategoryEntity;

public interface AdviceCategoryRepository extends JpaRepository<AdviceCategoryEntity, Integer> {
}
