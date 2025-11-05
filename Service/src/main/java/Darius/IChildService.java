package Darius;

import Darius.domain.Child;

import java.util.Optional;

public interface IChildService {
    void save(Child child);

    Optional<Child> findOneByCnp(String cnp);
}
