package Darius.domain;


import java.io.Serializable;

public class Sprint extends Entity<Integer> implements Serializable {
    private Float distance;
    public Sprint() {}
    public Sprint(Float distance) {
        this.distance = distance;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
    @Override
    public String toString() {
        return "Sprint{id=" + getId() + ", distance=" + distance + "}";
    }

}

