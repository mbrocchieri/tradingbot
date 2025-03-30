package org.tradingbot.advice;

import static java.util.Objects.requireNonNull;

public class AdviceCategoryCreationBean {
    private String name;

    public AdviceCategoryCreationBean() {
    }

    public AdviceCategoryCreationBean(String name) {
        this.name = name;
    }

    public String getName() {
        return requireNonNull(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
