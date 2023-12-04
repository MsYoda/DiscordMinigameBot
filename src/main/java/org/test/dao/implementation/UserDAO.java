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
import org.test.services.background.SessionManager;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class UserDAO implements UserDAOInterface {
    @Autowired
    private SessionManager sessionManager;

    @Override
    public void addEmptyUser(Long id) throws SQLException {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(id).build();

        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.save(user);

        session.getTransaction().commit();

    }

    @Override
    public void add(User user) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.save(user);

        session.getTransaction().commit();
    }

    @Override
    public Optional<User> get(Long id) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        User user = session.get(User.class, id);

        session.getTransaction().commit();

        return Optional.ofNullable(user);
    }

    @Override
    public void update(User user) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.update(user);

        session.getTransaction().commit();
    }

    @Override
    public void delete(User user) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.delete(user);

        session.getTransaction().commit();
    }

    @Override
    public Bag getBag(Long userID) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        Bag bag = (Bag) session.load(User.class, userID).getBag();

        session.getTransaction().commit();

        return bag;
    }

    @Override
    public void clearBag(Long userID) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        //session.delete(user);

        session.getTransaction().commit();
    }
}
