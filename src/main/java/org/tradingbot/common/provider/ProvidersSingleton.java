package org.tradingbot.common.provider;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProvidersSingleton {
    public static final ProvidersSingleton INSTANCE = new ProvidersSingleton();
    static {
        ProvidersSingleton.INSTANCE.addProvider(1, "Yahoo", new RestProvider());
    }

    private final Map<Integer, FinancialProvider> map = new ConcurrentHashMap<>();
    private final Map<Integer, String> idNameMap = new ConcurrentHashMap<>();

    private ProvidersSingleton() {
    }

    public FinancialProvider getProvider(int providerId) {
        return Objects.requireNonNull(map.get(providerId));
    }

    public synchronized void addProvider(int id, String name, FinancialProvider provider) {
        map.put(id, provider);
        idNameMap.put(id, name);
    }

    public Map<Integer, String> getProviders() {
        return Collections.unmodifiableMap(idNameMap);
    }
}
