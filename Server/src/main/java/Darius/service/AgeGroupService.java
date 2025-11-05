package Darius.service;

import Darius.IAgeGroupService;
import Darius.domain.AgeGroup;
import Darius.repository.IAgeGroupRepository;

import java.util.Optional;

public class AgeGroupService implements IAgeGroupService {
        private IAgeGroupRepository repository;
        public AgeGroupService(IAgeGroupRepository repository){this.repository = repository;}
        public Iterable<AgeGroup> findAll(){return repository.findAll();}
        public Optional<AgeGroup> findOneByAge(int age){
        return repository.findOneByAge(age);
    }

}
