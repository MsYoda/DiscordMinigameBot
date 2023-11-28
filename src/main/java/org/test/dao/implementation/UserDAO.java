package org.test.dao.implementation;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.UserDAOInterface;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.Helmet;
import org.test.entity.user_elements.Pick;

import java.sql.SQLException;

@Service
public class UserDAO implements UserDAOInterface {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addEmptyUser(Long id) throws SQLException {
        Helmet helmet = Helmet.builder().lightPower(50).maxToughness(50).toughness(50).build();
        Pick pick = Pick.builder().oreMultiplayer(1.0f).rareOreProbability(0.9f).build();
        Bag bag = Bag.builder().bagSize(7).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(id).build();

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(user);

        session.getTransaction().commit();
        session.close();

    }

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
        if (user == null) throw new SQLException();

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
