package org.test.dao;

import org.test.entity.User;

import java.sql.SQLException;

public interface UserDAOInterface {
    public void add(User user) throws SQLException;
    public User get(Long id) throws SQLException;
    public void update(User user) throws SQLException;
    public void delete(User user) throws SQLException;
}
