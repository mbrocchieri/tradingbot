package org.tradingbot.common.persistence;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "STRATEGY_PARAMETERS")
public class StrategyParameterEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DEFAULT_VALUE", nullable = false)
    private BigDecimal defaultValue;

    @ManyToOne
    @JoinColumn(name = "STRATEGY_ID", nullable = false)
    private StrategyEntity strategy;

    public StrategyParameterEntity() {
    }

    public StrategyParameterEntity(String name, BigDecimal defaultValue, StrategyEntity strategyEntity) {
        this.name = Objects.requireNonNull(name);
        this.defaultValue = defaultValue;
        this.strategy = strategyEntity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultValue, id, name, strategy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StrategyParameterEntity other = (StrategyParameterEntity) obj;
        return Objects.equals(defaultValue, other.defaultValue) && id == other.id && Objects.equals(name, other.name)
                && Objects.equals(strategy, other.strategy);
    }

    @Override
    public String toString() {
        return "StrategyParameterEntity [defaultValue=" + defaultValue + ", id=" + id + ", name=" + name + ", strategy="
                + strategy + "]";
    }

    

}
