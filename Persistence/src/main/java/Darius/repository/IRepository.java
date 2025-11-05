package Darius.repository;

import Darius.domain.Entity;
import java.util.List;
import java.util.Optional;

public interface IRepository<ID, T extends Entity<ID>> {
    // Salvează o entitate
    void save(T entity);

    // Găsește o entitate după ID
    Optional<T> findOne(ID id);

    // Găsește toate entitățile
    Iterable<T> findAll();

    // Șterge o entitate
    void delete(ID id);

    void update(T entity);
}
