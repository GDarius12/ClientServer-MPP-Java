package Darius.repository;

import Darius.domain.AgeGroup;

import java.util.Optional;

public interface IAgeGroupRepository extends IRepository<Integer, AgeGroup> {
    Optional<AgeGroup> findOneByAge(int age);
}
