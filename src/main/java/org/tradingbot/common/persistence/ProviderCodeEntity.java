package org.tradingbot.common.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "FP_CODES")
@IdClass(ProviderCodeId.class)
public class ProviderCodeEntity implements Serializable {
    @Id
    @ManyToOne(optional = false)
    private StockEntity stock;
    @Id
    @ManyToOne(optional = false)
    private ProviderEntity provider;
    @Column(name = "CODE", nullable = false)
    private String code;


    // for jpa
    public ProviderCodeEntity() {
    }



    public ProviderCodeEntity(StockEntity stock, ProviderEntity provider, String code) {
        this.stock = stock;
        this.provider = provider;
        this.code = code;
    }



    public StockEntity getStock() {
        return stock;
    }


    public void setStock(StockEntity stock) {
        this.stock = stock;
    }


    public ProviderEntity getProvider() {
        return provider;
    }


    public void setProvider(ProviderEntity provider) {
        this.provider = provider;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderCodeEntity that = (ProviderCodeEntity) o;
        return stock.equals(that.stock) && provider.equals(that.provider) && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stock, provider, code);
    }
}
