package org.test.dao.implementation;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.CooldownDAOInterface;
import org.test.entity.CommandID;
import org.test.entity.Cooldown;
import org.test.entity.User;
import org.test.services.background.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CooldownDAO implements CooldownDAOInterface {

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void add(Cooldown cooldown) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.save(cooldown);

        session.getTransaction().commit();
    }

    @Override
    public Optional<Cooldown> get(Long id) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        Cooldown cooldown = session.get(Cooldown.class, id);

        session.getTransaction().commit();

        return Optional.ofNullable(cooldown);
    }

    @Override
    public Optional<Cooldown> getByCommandIDAndUserID(CommandID commandID, Long userID) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Cooldown> criteria = criteriaBuilder.createQuery(Cooldown.class);
        Root<Cooldown> root = criteria.from(Cooldown.class);
        Join<Cooldown, User> userJoin = root.join("user");

        criteria.where(criteriaBuilder.and(criteriaBuilder.equal(userJoin.get("id"), userID), criteriaBuilder.equal(root.get("commandID"), commandID)));

        List<Cooldown> cooldownList = session.createQuery(criteria).getResultList();

        session.getTransaction().commit();

        if (cooldownList.size() == 0) return Optional.empty();

        return Optional.ofNullable(cooldownList.get(0));
    }

    @Override
    public void update(Cooldown cooldown) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.update(cooldown);

        session.getTransaction().commit();
    }

    @Override
    public void addOrUpdate(Cooldown cooldown) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.saveOrUpdate(cooldown);

        session.getTransaction().commit();
    }

    @Override
    public void delete(Cooldown cooldown) throws SQLException {
        Session session = sessionManager.getSession();
        session.beginTransaction();

        session.delete(cooldown);

        session.getTransaction().commit();
    }
}
