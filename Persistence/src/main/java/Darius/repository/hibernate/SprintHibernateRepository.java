package Darius.repository.hibernate;

import Darius.domain.Sprint;
import Darius.domain.orm.SprintORM;
import Darius.repository.ISprintRepository;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SprintHibernateRepository implements ISprintRepository {

    private final SessionFactory sessionFactory;

    public SprintHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Sprint> findOne(Integer id) {
        try (var session = sessionFactory.openSession()) {
            SprintORM orm = session.find(SprintORM.class, id);
            if (orm == null) return Optional.empty();

            Sprint sprint = new Sprint(orm.getDistance());
            sprint.setId(orm.getId());
            return Optional.of(sprint);
        }
    }

    @Override
    public Iterable<Sprint> findAll() {
        List<Sprint> result = new ArrayList<>();

        try (var session = sessionFactory.openSession()) {
            List<SprintORM> sprints = session.createQuery("from SprintORM", SprintORM.class).list();
            for (SprintORM orm : sprints) {
                Sprint s = new Sprint(orm.getDistance());
                s.setId(orm.getId());
                result.add(s);
            }
        }

        return result;
    }

    @Override
    public void save(Sprint s) {
        try (var session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            SprintORM orm = new SprintORM(s.getDistance());
            session.persist(orm);
            tx.commit();
        }
    }

    @Override
    public void delete(Integer id) {
        try (var session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            SprintORM orm = session.find(SprintORM.class, id);
            if (orm != null) {
                session.remove(orm);
            }
            tx.commit();
        }
    }

    @Override
    public void update(Sprint s) {
        try (var session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            SprintORM orm = session.find(SprintORM.class, s.getId());
            if (orm != null) {
                orm.setDistance(s.getDistance());
                session.merge(orm);
            }
            tx.commit();
        }
    }

    @Override
    public Sprint SprintSave(Sprint sprint) {
        return null;
    }
}
