package Darius.domain;

import java.io.Serializable;
import java.util.List;

public class AgeGroup extends Entity<Integer> implements Serializable{
    private Integer Lower;
    private Integer Upper;

    public AgeGroup(Integer Lower, Integer Upper) {
        this.Lower = Lower;
        this.Upper = Upper;
    }

    public Integer getLower() {
        return Lower;
    }

    public void setLower(Integer Lower) {
        this.Lower = Lower;
    }

    public Integer getUpper() {
        return Upper;
    }

    public void setUpper(Integer Upper) {
        this.Upper = Upper;
    }

    @Override
    public String toString() {
        return "AgeGroup{" +
                "Lower=" + Lower +
                ", Upper=" + Upper +
                '}';
    }
}
