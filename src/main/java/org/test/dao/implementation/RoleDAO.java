package org.test.dao.implementation;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.RoleDAOInterface;
import org.test.entity.Ore;
import org.test.entity.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleDAO implements RoleDAOInterface {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public void add(Role role) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(role);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Optional<Role> get(Long id) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Role role = session.get(Role.class, id);

        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(role);
    }

    @Override
    public void update(Role role) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.update(role);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Role role) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(role);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<Role> getAll() throws SQLException {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
        criteria.from(Role.class);

        List<Role> roles = session.createQuery(criteria).getResultList();

        session.close();
        return roles;
    }
}
