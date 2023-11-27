package org.test.dao;

import org.test.entity.Ore;
import org.test.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface OreDAOInterface {
    public void add(Ore ore) throws SQLException;
    public Ore get(Long id) throws SQLException;
    public void update(Ore ore) throws SQLException;
    public void delete(Ore ore) throws SQLException;
    public List<Ore> getALl() throws SQLException;;
}
