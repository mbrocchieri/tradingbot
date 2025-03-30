package org.tradingbot.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tradingbot.common.persistence.StockMetadataEntity;
import org.tradingbot.common.persistence.StockMetadataId;

public interface StockMetadataRepository extends JpaRepository<StockMetadataEntity, StockMetadataId> {
}
