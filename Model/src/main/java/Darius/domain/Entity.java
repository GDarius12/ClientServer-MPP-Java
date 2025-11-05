package Darius.domain;

import java.io.Serial;
import java.io.Serializable;

public class Entity<T> implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
