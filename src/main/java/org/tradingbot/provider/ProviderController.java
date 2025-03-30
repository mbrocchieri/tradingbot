package org.tradingbot.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tradingbot.common.provider.ProvidersSingleton;

@RestController
public class ProviderController {

    @GetMapping("/providers")
    public List<ProviderBean> getProviders() {
        List<ProviderBean> list = new ArrayList<>();
        ProvidersSingleton.INSTANCE.getProviders().forEach((key, value) -> list.add(new ProviderBean(key, value)));
        return list;
    }
}
