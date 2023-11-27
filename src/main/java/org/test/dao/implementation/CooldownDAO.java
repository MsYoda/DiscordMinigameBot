package org.test.dao.implementation;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.CooldownDAOInterface;
import org.test.entity.Cooldown;
import org.test.entity.User;

import java.sql.SQLException;

@Service
public class CooldownDAO implements CooldownDAOInterface {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void add(Cooldown cooldown) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(cooldown);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Cooldown get(Long id) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Cooldown cooldown = (Cooldown) session.get(Cooldown.class, id);

        session.getTransaction().commit();
        session.close();

        return cooldown;
    }

    @Override
    public void update(Cooldown cooldown) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.update(cooldown);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Cooldown cooldown) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(cooldown);

        session.getTransaction().commit();
        session.close();
    }
}
