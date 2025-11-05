package Darius.domain.orm;

import jakarta.persistence.*;

@Entity
@Table(name = "sprints")
public class SprintORM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Float distance;

    public SprintORM() {}

    public SprintORM(Float distance) {
        this.distance = distance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
