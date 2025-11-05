package Darius.repository.hibernate;

import Darius.domain.Employee;
import Darius.domain.orm.EmployeeORM;
import Darius.repository.IEmployeeRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class EmployeeHibernateRepository implements IEmployeeRepository {

    private Employee mapFromORM(EmployeeORM orm) {
        Employee e = new Employee(orm.getName(), orm.getSurname(), orm.getCnp(),
                orm.getPhoneNumber(), orm.getAddress(), orm.getUsername(), orm.getPassword());
        e.setId(orm.getId());
        return e;
    }

    private EmployeeORM mapToORM(Employee e) {
        EmployeeORM orm = new EmployeeORM(e.getName(), e.getSurname(), e.getCnp(),
                e.getPhoneNumber(), e.getAddress(), e.getUsername(), e.getPassword());
        orm.setId(e.getId());
        return orm;
    }

    @Override
    public Optional<Employee> findOne(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            EmployeeORM orm = session.get(EmployeeORM.class, id);
            return orm != null ? Optional.of(mapFromORM(orm)) : Optional.empty();
        }
    }

    @Override
    public Optional<Employee> findOneByUsername(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            EmployeeORM orm = session.createQuery(
                            "from EmployeeORM where username = :username", EmployeeORM.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return orm != null ? Optional.of(mapFromORM(orm)) : Optional.empty();
        }
    }

    @Override
    public Iterable<Employee> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            List<EmployeeORM> ormList = session.createQuery("from EmployeeORM", EmployeeORM.class).list();
            return ormList.stream().map(this::mapFromORM).toList();
        }
    }

    @Override
    public void save(Employee entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(mapToORM(entity));
            tx.commit();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            EmployeeORM orm = session.get(EmployeeORM.class, id);
            if (orm != null)
                session.remove(orm);
            tx.commit();
        }
    }

    @Override
    public void update(Employee entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(mapToORM(entity));
            tx.commit();
        }
    }
}
