package org.tradingbot.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.ProviderCodeEntity;
import org.tradingbot.common.persistence.ProviderCodeId;
import org.tradingbot.common.persistence.StockEntity;

import java.util.Collection;

public interface ProviderCodeRepository extends JpaRepository<ProviderCodeEntity, ProviderCodeId> {

    public Collection<ProviderCodeEntity> findAllByStock(StockEntity stock);
}
