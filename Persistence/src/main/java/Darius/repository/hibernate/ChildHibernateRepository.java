package Darius.repository.hibernate;

import Darius.domain.Child;
import Darius.domain.orm.ChildORM;
import Darius.repository.IChildRepository;
import Darius.repository.exception.RepositoryException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChildHibernateRepository implements IChildRepository {

    private Child fromORM(ChildORM orm) {
        Child c = new Child(orm.getName(), orm.getSurname(), orm.getCnp());
        c.setId(orm.getId());
        return c;
    }

    private ChildORM toORM(Child c) {
        ChildORM orm = new ChildORM();
        orm.setId(c.getId());
        orm.setName(c.getName());
        orm.setSurname(c.getSurname());
        orm.setCnp(c.getCnp());
        return orm;
    }

    @Override
    public void save(Child entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Optional<Child> existing = findOneByCnp(entity.getCnp());
            if (existing.isPresent()) {
                throw new RepositoryException("Exista deja o persoana cu acest CNP!");
            }

            session.persist(toORM(entity));
            tx.commit();
        }
    }

    @Override
    public Optional<Child> findOne(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            ChildORM orm = session.get(ChildORM.class, id);
            return orm == null ? Optional.empty() : Optional.of(fromORM(orm));
        }
    }

    @Override
    public Optional<Child> findOneByCnp(String cnp) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            ChildORM orm = session
                    .createQuery("from ChildORM where cnp = :cnp", ChildORM.class)
                    .setParameter("cnp", cnp)
                    .uniqueResult();
            return orm == null ? Optional.empty() : Optional.of(fromORM(orm));
        }
    }

    @Override
    public Iterable<Child> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            List<ChildORM> orms = session.createQuery("from ChildORM", ChildORM.class).list();
            List<Child> result = new ArrayList<>();
            for (ChildORM orm : orms) {
                result.add(fromORM(orm));
            }
            return result;
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ChildORM orm = session.get(ChildORM.class, id);
            if (orm != null) {
                session.remove(orm);
            }
            tx.commit();
        }
    }

    @Override
    public void update(Child entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Optional<Child> existing = findOneByCnp(entity.getCnp());
            if (existing.isPresent() && !existing.get().getId().equals(entity.getId())) {
                throw new RepositoryException("Exista deja un copil cu acest CNP!");
            }

            session.merge(toORM(entity));
            tx.commit();
        }
    }
}
