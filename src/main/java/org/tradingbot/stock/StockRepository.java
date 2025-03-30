package org.tradingbot.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tradingbot.common.persistence.MarketEntity;
import org.tradingbot.common.persistence.StockEntity;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Integer> {

    Optional<StockEntity> findByMarketEntityAndMnemonic(MarketEntity marketEntity, String mnemonic);
    List<StockEntity> findByMnemonic(String mnemonic);

    @Query("from StockEntity s where upper(s.mnemonic) like %:search% or upper(s.name) like %:search% order by s.name")
    List<StockEntity> search(String search);
}
