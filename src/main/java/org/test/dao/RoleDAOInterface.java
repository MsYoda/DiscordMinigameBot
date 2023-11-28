package org.test.dao;

import org.test.entity.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoleDAOInterface {
    public void add(Role user) throws SQLException;
    public Optional<Role> get(Long id) throws SQLException;
    public void update(Role user) throws SQLException;
    public void delete(Role user) throws SQLException;

    public List<Role> getAll() throws SQLException;
}
