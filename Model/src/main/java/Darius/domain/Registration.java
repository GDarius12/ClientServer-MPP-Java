package Darius.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Registration extends Entity<Integer> implements Serializable {
    private Person person;
    private List<Event> events;
    private LocalDateTime dateTime;

    public static final Integer numberOfEvents = 2;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Registration(Person person, List<Event> events, LocalDateTime dateTime) {
        this.person = person;
        this.events = events;
        this.dateTime = dateTime;
    }
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
