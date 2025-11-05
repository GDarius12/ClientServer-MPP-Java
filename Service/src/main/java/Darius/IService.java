package Darius;
import Darius.domain.*;
import java.util.Optional;

public interface IService {
    void setEmployeeService(IEmployeeService employeeService);

    void setEventService(IEventService eventService);

    void setAgeGroupService(IAgeGroupService ageGroupService);

    void setRegistrationService(IRegistrationService registrationService);

    void setChildService(IChildService childService);

    Iterable<AgeGroup> findAllAgeGroup();
    Optional<AgeGroup> findOneByAgeAgeGroup(int age);
    Iterable<Event> findAllEvent();

    Optional<Event> findOneEvent(int id);
    Optional<Event> findOneBySprintIdEvent(int id);
    Iterable<Event> findAllByAgeGroupIdEvent(int id);
    void saveChild(Child child);
    Optional<Child> findOneByCnpChild(String cnp);
    Optional<Employee> findOneEmployeeByUsername(String username);
    public Optional<Employee> login(String username, String password, IObserver client);
    Iterable<Registration> findAllRegistrationsBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup);
    Optional<Registration> findOneRegistrationByPerson(Person person);
    Optional<Registration> findOneRegistration(int id);
    Optional<Registration> findOneRegistrationByEvent(Event event);
    void saveRegistration(Registration registration);
    boolean logout(Employee employee);

}

