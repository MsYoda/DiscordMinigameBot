package org.test.dao;

import org.test.entity.CommandID;
import org.test.entity.Cooldown;
import org.test.entity.Role;
import org.test.entity.User;

import java.sql.SQLException;
import java.util.Optional;

public interface CooldownDAOInterface {
    public void add(Cooldown cooldown) throws SQLException;
    public Optional<Cooldown> get(Long id) throws SQLException;
    public Optional<Cooldown> getByCommandIDAndUserID(CommandID commandID, Long userID) throws SQLException;
    public void update(Cooldown cooldown) throws SQLException;
    public void addOrUpdate(Cooldown cooldown) throws SQLException;
    public void delete(Cooldown cooldown) throws SQLException;
}
