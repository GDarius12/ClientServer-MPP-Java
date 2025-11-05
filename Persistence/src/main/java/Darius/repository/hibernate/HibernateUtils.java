package Darius.repository.hibernate;

import Darius.domain.orm.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){
        if ((sessionFactory == null) || (sessionFactory.isClosed())){
            sessionFactory = createNewSessionFactory();
        }

        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory(){
        sessionFactory = new Configuration()
                .addAnnotatedClass(ChildORM.class)
                .addAnnotatedClass(SprintORM.class)
                .addAnnotatedClass(EmployeeORM.class)
                .buildSessionFactory();

        return sessionFactory;
    }

    public static void closeSessionFactory(){
        if (sessionFactory != null){
            sessionFactory.close();
        }
    }
}
