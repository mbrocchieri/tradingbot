package org.tradingbot.provider;

public class ProviderBean {
    private int id;
    private String name;

    public ProviderBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProviderBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
