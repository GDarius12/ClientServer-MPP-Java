package Darius;
import Darius.domain.Event;

import java.util.Optional;

public interface IEventService {
    Iterable<Event> findAll();
    Optional<Event> findOne(int id);
    Optional<Event> findOneBySprintId(int id);
    Iterable<Event> findAllByAgeGroupId(int id);
}
