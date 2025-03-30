package org.tradingbot.common.persistence;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STRATEGIES")
public class StrategyEntity implements Serializable {
    @OneToMany(mappedBy = "strategy", fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private final Collection<StrategyParameterEntity> defaultParameters = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Column(name = "XML", nullable = false, columnDefinition = "text")
    private String xml;
    @Column(name = "ALERTS", nullable = false)
    private boolean alerts = true;
    @Column(name = "TRADING_PERIOD", nullable = false)
    private int tradingPeriod;
    @Column(name = "TP_PARAMS")
    private String tradingPeriodParameters;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted = false;

    // for hibernate
    public StrategyEntity() {

    }

    public StrategyEntity(String name, String xml, int tradingPeriod, String tradingPeriodParameters) {
        this.name = Objects.requireNonNull(name);
        this.xml = Objects.requireNonNull(xml);
        this.tradingPeriod = tradingPeriod;
        this.tradingPeriodParameters = tradingPeriodParameters;
    }

    public StrategyEntity(String name, String xml, int tradingPeriod) {
        this(name, xml, tradingPeriod, null);
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

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public boolean isAlerts() {
        return alerts;
    }

    public void setAlerts(boolean alerts) {
        this.alerts = alerts;
    }

    public Collection<StrategyParameterEntity> getDefaultParameters() {
        return defaultParameters;
    }

    public int getTradingPeriod() {
        return tradingPeriod;
    }

    public String getTradingPeriodParameters() {
        return tradingPeriodParameters;
    }

    public void setTradingPeriod(int tradingPeriod) {
        this.tradingPeriod = tradingPeriod;
    }

    public void setTradingPeriodParameters(String tradingPeriodParameters) {
        this.tradingPeriodParameters = tradingPeriodParameters;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StrategyEntity that = (StrategyEntity) o;
        return id == that.id && alerts == that.alerts && tradingPeriod == that.tradingPeriod &&
                deleted == that.deleted && Objects.equals(defaultParameters, that.defaultParameters) &&
                Objects.equals(name, that.name) && Objects.equals(xml, that.xml) &&
                Objects.equals(tradingPeriodParameters, that.tradingPeriodParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultParameters, id, name, xml, alerts, tradingPeriod, tradingPeriodParameters, deleted);
    }

    @Override
    public String toString() {
        return "StrategyEntity{" + "defaultParameters=" + defaultParameters + ", id=" + id + ", name='" + name + '\'' +
                ", xml='" + xml + '\'' + ", alerts=" + alerts + ", tradingPeriod=" + tradingPeriod +
                ", tradingPeriodParameters='" + tradingPeriodParameters + '\'' + ", deleted=" + deleted + '}';
    }
}
