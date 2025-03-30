package org.tradingbot.common.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "STOCKS")
//@UniqueConstraint({"marketEntity", "mnemomic"})
public class StockEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @Column(name = "NAME", unique = true)
    private String name;
    @Column(name = "CURRENCY")
    private String currency;

    @ManyToOne(optional = false)
    private MarketEntity marketEntity;

    @Column(name = "MNEMONIC", unique = true)
    private String mnemonic;


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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setMarketEntity(MarketEntity marketEntity) {
        this.marketEntity = marketEntity;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public MarketEntity getMarketEntity() {
        return marketEntity;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
}
