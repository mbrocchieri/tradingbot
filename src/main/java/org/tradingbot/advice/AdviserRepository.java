package org.tradingbot.advice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.AdviserEntity;

public interface AdviserRepository extends JpaRepository<AdviserEntity, Integer> {
}
