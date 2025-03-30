package org.tradingbot.common.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ADVICE_CATEGORIES")
public class AdviceCategoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @Column(name ="NAME", nullable = false)
    private String name;

    public AdviceCategoryEntity() {
    }

    public AdviceCategoryEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdviceCategoryEntity that = (AdviceCategoryEntity) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "AdviceCategoryEntity{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
