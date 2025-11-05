package Darius;
import Darius.domain.AgeGroup;

import java.util.Optional;

public interface IAgeGroupService {

    Iterable<AgeGroup> findAll();

    Optional<AgeGroup> findOneByAge(int age);
}

