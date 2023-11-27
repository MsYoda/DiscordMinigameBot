package org.test.dao.implementation;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.UserDAOInterface;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;

import java.sql.SQLException;

@Service
public class UserDAO implements UserDAOInterface {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public void add(User user) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(user);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public User get(Long id) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, id);

        session.getTransaction().commit();
        session.close();

        return user;
    }

    @Override
    public void update(User user) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.update(user);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(User user) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(user);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Bag getBag(Long userID) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Bag bag = (Bag) session.load(User.class, userID).getBag();

        session.getTransaction().commit();
        session.close();

        return bag;
    }

    @Override
    public void clearBag(Long userID) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        //session.delete(user);

        session.getTransaction().commit();
        session.close();
    }
}
