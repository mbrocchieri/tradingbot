package org.tradingbot.stock;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tradingbot.common.persistence.ProviderCodeEntity;
import org.tradingbot.common.persistence.StockEntity;
import org.tradingbot.common.provider.ProvidersSingleton;
import org.tradingbot.common.provider.RestProvider;
import org.tradingbot.common.provider.StockData;
import org.tradingbot.strategy.StrategyRepository;
import org.tradingbot.tradingconfig.TradingConfigService;

@Service
public class StockService {

    private static final Logger LOG = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProviderCodeRepository providerCodeRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private TradingConfigService tradingConfigService;


    @Transactional
    public StockBean createStock(StockCreateBean stockBean) {
        var providerEntity = providerRepository.findById(stockBean.getProviderId());
        if (providerEntity.isEmpty()) {
            throw new IllegalStateException("Provider ID " + stockBean.getProviderId() + " not found");
        }

        var provider = ProvidersSingleton.INSTANCE.getProvider(stockBean.getProviderId());
        StockData stock;
        try {
            stock = provider.getStockData(stockBean.getCode());
        } catch (IOException e) {
            LOG.error("Error for {}", stockBean.getCode(), e);
            throw new IllegalStateException("Error for " + stockBean.getCode(), e);
        }
        var stockEntity = new StockEntity();
        stockEntity.setCurrency(stock.getCurrency());
        stockEntity.setName(stock.getName());
        stockEntity.setMnemonic(stock.getMnemonic());

        var optionalMarketEntity = marketRepository.findById(stock.getMarketId());
        if (optionalMarketEntity.isEmpty()) {
            throw new IllegalStateException("Error with market id " + stock.getMarketId());
        }

        var marketEntity = optionalMarketEntity.get();
        stockEntity.setMarketEntity(marketEntity);

        ProviderCodeEntity code = new ProviderCodeEntity(stockEntity, providerEntity.get(), stockBean.getCode());

        stockEntity = stockRepository.save(stockEntity);
        code = providerCodeRepository.save(code);

        for (var strategy : strategyRepository.findAll()) {
            tradingConfigService.create(strategy, stockEntity);
        }

        return new StockBean(stockEntity, List.of(code));

    }
}
