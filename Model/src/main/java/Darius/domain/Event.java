package Darius.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Event extends Entity<Integer> implements Serializable {
    private Sprint eventName;
    private List<AgeGroup> ageGroups;
    private LocalDateTime dateTime;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Event(Sprint eventName, List<AgeGroup> ageGroups, LocalDateTime dateTime) {
        this.eventName = eventName;
        this.ageGroups = ageGroups;
        this.dateTime = dateTime;
    }



    public Sprint getEventName() {
        return eventName;
    }

    public void setEventName(Sprint eventName) {
        this.eventName = eventName;
    }

    public List<AgeGroup> getAgeGroups() {
        return ageGroups;
    }

    public void setAgeGroups(List<AgeGroup> ageGroups) {
        this.ageGroups = ageGroups;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}