package org.test.services.background;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionManager {
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;

    public Session getSession() {
        if (session == null || !session.isOpen())
        {
            session = sessionFactory.openSession();
        }
        return session;
    }
    public void closeSession()
    {
        session.close();
    }
}
