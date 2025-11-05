package Darius;

import Darius.domain.Employee;

import java.util.Optional;

public interface IEmployeeService {
    Optional<Employee> findOneByUsername(String username);

    public Optional<Employee> login(String username, String password);
}
