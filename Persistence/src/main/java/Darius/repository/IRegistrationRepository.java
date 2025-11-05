package Darius.repository;
import Darius.domain.*;

import java.util.Optional;

public interface IRegistrationRepository extends IRepository<Integer,Registration> {
    public Optional<Registration> findOneByPerson(Person person);
    public Optional<Registration> findOneByEvent(Event event);
    public Iterable<Registration> findAllBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup);
}
