package org.tradingbot.test.provider;

import org.tradingbot.common.persistence.ProviderEntity;
import org.tradingbot.common.provider.FinancialProvider;
import org.tradingbot.common.provider.ProvidersSingleton;
import org.tradingbot.stock.ProviderRepository;

public abstract class ProvidersSingletonUtils {
    public static void addProvider(String name, FinancialProvider provider, ProviderRepository providerRepository) {
        ProvidersSingleton.INSTANCE.addProvider(provider.getProviderId(), "Yahoo", provider);
        var providerEntity = new ProviderEntity();
        providerEntity.setId(provider.getProviderId());
        providerEntity.setName(name);
        providerRepository.save(providerEntity);
    }

}
