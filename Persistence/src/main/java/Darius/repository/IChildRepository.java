package Darius.repository;

import Darius.domain.Child;

import java.util.Optional;

public interface IChildRepository extends IRepository<Integer,Child> {
    Optional<Child> findOneByCnp (String cnp);

}
