package org.tradingbot.common.persistence;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONFIG_PARAMETERS")
public class ConfigParameterEntity implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "PARAMETER_ID", nullable = false)
    private StrategyParameterEntity parameter;

    @Id
    @ManyToOne
    @JoinColumn(name = "CONFIG_ID", nullable = false)
    private TradingConfigEntity config;

    @Column(name = "VALUE", nullable = false)
    private BigDecimal value;

    public ConfigParameterEntity() {

    }

    public ConfigParameterEntity(TradingConfigEntity configEntity, StrategyParameterEntity parameter, BigDecimal value) {
        this.parameter = parameter;
        this.config = configEntity;
        this.value = value;
    }

    public StrategyParameterEntity getParameter() {
        return parameter;
    }

    public TradingConfigEntity getConfig() {
        return config;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((config == null) ? 0 : config.hashCode());
        result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigParameterEntity other = (ConfigParameterEntity) obj;
        if (config == null) {
            if (other.config != null)
                return false;
        } else if (!config.equals(other.config))
            return false;
        if (parameter == null) {
            if (other.parameter != null)
                return false;
        } else if (!parameter.equals(other.parameter))
            return false;
        if (value == null) {
            return other.value == null;
        } else
            return value.equals(other.value);
    }


}
