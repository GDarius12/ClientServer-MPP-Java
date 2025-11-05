package Darius.service;

import Darius.*;
import Darius.domain.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService {
    private IEmployeeService employeeService;
    private IEventService eventService;
    private IAgeGroupService ageGroupService;
    private IRegistrationService registrationService;
    private IChildService childService;

    private Map<Integer, IObserver> loggedEmployees;

    public void setEmployeeService(IEmployeeService employeeService) {
        this.employeeService = employeeService;
        loggedEmployees = new ConcurrentHashMap<>();
    }


    public void setEventService(IEventService eventService) {
        this.eventService = eventService;
    }


    public void setAgeGroupService(IAgeGroupService ageGroupService) {
        this.ageGroupService = ageGroupService;
    }


    public void setRegistrationService(IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    public void setChildService(IChildService childService) {
        this.childService = childService;
    }

    @Override
    public Iterable<AgeGroup> findAllAgeGroup() {
        return ageGroupService.findAll();
    }

    @Override
    public Optional<AgeGroup> findOneByAgeAgeGroup(int age) {
        return ageGroupService.findOneByAge(age);
    }

    @Override
    public Iterable<Event> findAllEvent() {
        return eventService.findAll();
    }

    @Override
    public Optional<Event> findOneEvent(int id) {
        return eventService.findOne(id);
    }

    @Override
    public Optional<Event> findOneBySprintIdEvent(int id) {
        return eventService.findOneBySprintId(id);
    }

    @Override
    public Iterable<Event> findAllByAgeGroupIdEvent(int id) {
        return eventService.findAllByAgeGroupId(id);
    }

    @Override
    public void saveChild(Child child) {
        childService.save(child);
    }

    @Override
    public Optional<Child> findOneByCnpChild(String cnp) {
        return childService.findOneByCnp(cnp);
    }

    @Override
    public Optional<Employee> findOneEmployeeByUsername(String username) {
        return employeeService.findOneByUsername(username);
    }

    @Override
    public synchronized Optional<Employee> login(String username, String password, IObserver client) {
        var employee = employeeService.login(username, password);
        if (employee.isPresent()){
            if (loggedEmployees.get(employee.get().getId()) != null){
                throw new ServiceException("User already logged in!");
            }
            loggedEmployees.put(employee.get().getId(), client);
        }else{
            throw new ServiceException("Authentication failed.");
        }
        return employee;
    }

    private final int defaultThreadsNo = 3;
    private void notifyEmployeesLoggedIn(Registration registration){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (var obs : loggedEmployees.values()){
            executor.execute(()->{
                try{
                    obs.registrationAdded(registration);
                } catch (Exception e){
                    System.out.println("Error notifying" + e);
                }
            });
        }
    }

    @Override
    public Iterable<Registration> findAllRegistrationsBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup) {
        return registrationService.findAllBySprintAndGroupAge(sprint, ageGroup);
    }

    @Override
    public Optional<Registration> findOneRegistrationByPerson(Person person) {
        return registrationService.findOneByPerson(person);
    }

    @Override
    public Optional<Registration> findOneRegistration(int id) {
        return registrationService.findOne(id);
    }

    @Override
    public Optional<Registration> findOneRegistrationByEvent(Event event) {
        return registrationService.findOneByEvent(event);
    }

    @Override
    public void saveRegistration(Registration registration) {
        registrationService.save(registration);
        notifyEmployeesLoggedIn(registration);
    }

    @Override
    public boolean logout(Employee employee) {
        loggedEmployees.remove(employee.getId());
        return true;
    }

}

