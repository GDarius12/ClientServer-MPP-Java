package Darius.repository;

import Darius.domain.Employee;

import java.util.Optional;

public interface IEmployeeRepository extends IRepository<Integer,Employee> {
    Optional<Employee> findOneByUsername(String username);

}
