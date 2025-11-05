package Darius.repository;

import Darius.domain.Event;
import java.util.Optional;

public interface IEventRepository extends IRepository<Integer,Event> {
    Optional<Event> findOneBySprintId(int id);
    Iterable<Event> findAllByAgeGroupId(int id);
}
