package org.tradingbot.advice;

public class AdviserBean {
    private String name;
    private int adviserId;

    public AdviserBean(int id, String name) {
        adviserId = id;
        this.name = name;
    }

    public int getAdviserId() {
        return adviserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdviserId(int adviserId) {
        this.adviserId = adviserId;
    }
}
