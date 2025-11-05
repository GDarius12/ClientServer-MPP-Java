package Darius;
import Darius.domain.*;

import java.util.Optional;

public interface IRegistrationService {
    Iterable<Registration> findAllBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup);
    Optional<Registration> findOneByPerson(Person person);
    Optional<Registration> findOne(int id);

    Optional<Registration> findOneByEvent(Event event);

    void save(Registration registration);
}
