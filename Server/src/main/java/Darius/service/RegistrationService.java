package Darius.service;
import java.util.Optional;

import Darius.IRegistrationService;
import Darius.domain.*;
import Darius.repository.IRegistrationRepository;

public class RegistrationService implements IRegistrationService {
    private IRegistrationRepository repository;
    public RegistrationService(IRegistrationRepository repository){this.repository = repository;}
    public Iterable<Registration> findAllBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup){
        return repository.findAllBySprintAndGroupAge(sprint, ageGroup);
    }
    public Optional<Registration> findOneByPerson(Person person){
        return repository.findOneByPerson(person);
    }
    public Optional<Registration> findOne(int id){
        return repository.findOne(id);
    }
    public Optional<Registration> findOneByEvent(Event event){
        return repository.findOneByEvent(event);
    }
    public void save(Registration registration){
        repository.save(registration);
    }
}
