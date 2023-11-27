package org.test.dao;

import org.test.entity.Cooldown;
import org.test.entity.Role;
import org.test.entity.User;

import java.sql.SQLException;

public interface CooldownDAOInterface {
    public void add(Cooldown cooldown) throws SQLException;
    public Cooldown get(Long id) throws SQLException;
    public void update(Cooldown cooldown) throws SQLException;
    public void delete(Cooldown cooldown) throws SQLException;
}
