package Darius.service;

import Darius.IChildService;
import Darius.domain.Child;
import java.util.Optional;
import Darius.repository.IChildRepository;
public class ChildService implements IChildService {
    private IChildRepository repository;
    public ChildService(IChildRepository repository) {
        this.repository = repository;
    }
    public void save(Child child) {repository.save(child);}
    public Optional<Child> findOneByCnp (String cnp) {
        return repository.findOneByCnp(cnp);
    }
}
