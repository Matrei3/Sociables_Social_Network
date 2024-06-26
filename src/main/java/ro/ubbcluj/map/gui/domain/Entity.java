package ro.ubbcluj.map.gui.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

    protected ID id;
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> entity)) return false;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}