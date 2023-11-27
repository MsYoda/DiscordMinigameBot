package org.test.dao.implementation;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.OreDAOInterface;
import org.test.entity.Ore;
import org.test.entity.User;

import java.sql.SQLException;
import java.util.List;

@Service
public class OreDAO implements OreDAOInterface {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public void add(Ore ore) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(ore);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Ore get(Long id) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Ore ore = (Ore) session.get(Ore.class, id);

        session.getTransaction().commit();
        session.close();

        return ore;
    }

    @Override
    public void update(Ore ore) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.update(ore);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Ore ore) throws SQLException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(ore);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<Ore> getALl() throws SQLException {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Ore> criteria = builder.createQuery(Ore.class);
        criteria.from(Ore.class);

        List<Ore> ores = session.createQuery(criteria).getResultList();

        session.close();
        return ores;
    }
}
