package Darius.service;

import Darius.IEmployeeService;
import Darius.domain.Employee;
import Darius.repository.IEmployeeRepository;

import java.util.Objects;
import java.util.Optional;

public class EmployeeService implements IEmployeeService {
    private IEmployeeRepository repository;
    public EmployeeService(IEmployeeRepository repository){this.repository = repository;}
    public Optional<Employee> findOneByUsername(String username){return repository.findOneByUsername(username);}
    public Optional<Employee> login(String username, String password){
        var findEmployee = findOneByUsername(username);
        if (findEmployee.isPresent()){
            if (Objects.equals(findEmployee.get().getPassword(), password)){
                return findEmployee;
            }
        }

        return Optional.empty();
    }
}
