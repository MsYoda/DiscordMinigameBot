package org.test.dao;

import org.test.entity.Role;
import org.test.entity.User;

import java.sql.SQLException;

public interface RoleDAOInterface {
    public void add(Role user) throws SQLException;
    public User get(Long id) throws SQLException;
    public void update(Role user) throws SQLException;
    public void delete(Role user) throws SQLException;
}
