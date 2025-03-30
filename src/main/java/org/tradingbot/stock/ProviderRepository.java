package org.tradingbot.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.ProviderEntity;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Integer> {

}
