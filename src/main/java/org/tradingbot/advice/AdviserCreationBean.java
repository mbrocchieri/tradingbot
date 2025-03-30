package org.tradingbot.advice;

import static java.util.Objects.requireNonNull;

public class AdviserCreationBean {

    private String name;

    public AdviserCreationBean() {
    }

    public AdviserCreationBean(String name) {
        this.name = name;
    }

    public String getName() {
        return requireNonNull(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
