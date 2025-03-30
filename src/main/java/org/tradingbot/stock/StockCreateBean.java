package org.tradingbot.stock;

public class StockCreateBean {
    private int providerId;
    private String code;

    public StockCreateBean() {

    }

    public StockCreateBean(int providerId, String code) {
        this.providerId = providerId;
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public int getProviderId() {
        return providerId;
    }
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }


}
