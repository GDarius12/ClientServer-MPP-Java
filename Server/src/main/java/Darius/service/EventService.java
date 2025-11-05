package Darius.service;

import Darius.IEventService;
import Darius.domain.Event;
import Darius.repository.IEventRepository;

import java.util.Optional;

public class EventService implements IEventService {
    private IEventRepository repository;

    public EventService(IEventRepository repository){
        this.repository = repository;
    }
    public Iterable<Event> findAll(){
        return repository.findAll();
    }
    public Optional<Event> findOne(int id){
        return repository.findOne(id);
    }
    public Optional<Event> findOneBySprintId(int id){
        return  repository.findOneBySprintId(id);
    }
    public Iterable<Event> findAllByAgeGroupId(int id){
        return repository.findAllByAgeGroupId(id);
    }
}
