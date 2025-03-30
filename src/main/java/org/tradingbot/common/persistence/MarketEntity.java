package org.tradingbot.common.persistence;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MARKETS")
public class MarketEntity implements Serializable {

    @Id
    @Column(name = "ID")
    private int id;
    @Column(name ="NAME")
    private String name;
    @Column(name = "OPEN_HOUR")
    private LocalTime openHour;
    @Column(name = "CLOSE_HOUR")
    private LocalTime closeHour;
    @Column(name = "TIMEZONE")
    private String timezone;

    public MarketEntity() {
    }

    public MarketEntity(int id, String name, LocalTime openHour, LocalTime closeHour, String timezone) {
        this.id = id;
        this.name = name;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.timezone = timezone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getOpenHour() {
        return openHour;
    }

    public void setOpenHour(LocalTime openHour) {
        this.openHour = openHour;
    }

    public LocalTime getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(LocalTime closeHour) {
        this.closeHour = closeHour;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MarketEntity that = (MarketEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(openHour, that.openHour) &&
                Objects.equals(closeHour, that.closeHour) && Objects.equals(timezone, that.timezone);
    }

    @Override
    public String toString() {
        return "MarketEntity{" + "id=" + id + ", name='" + name + '\'' + ", openHour=" + openHour + ", closeHour=" +
                closeHour + ", timezone='" + timezone + '\'' + '}';
    }
}
