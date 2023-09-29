package org.test.db;

import org.test.entity.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class UserRepository{
    private final String dbUrl;
    private final String tableName;

    private final String columnsNames = "(username, money)";

    public UserRepository(String url, String tableName)
    {
        this.dbUrl = url;
        this.tableName = tableName;
        if (!checkDatabase())
        {
            LoggerFactory.getLogger("User repository").error("Database config has error! Check link to the DB!");
            System.exit(-1);
        }

    }

    private Boolean checkDatabase()
    {
        try(Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists " + tableName + " (" +
                   "username datatype TEXT,"+
                   "money datatype INT" +
                   ");");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public Boolean isUserExist(String username)
    {
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + tableName + " where username='" + username + "';");
            result = resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public User findUserByUsername(String username)
    {
        User result = null;
        try(Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + tableName + " where username='" + username + "';");
            if (resultSet.next())
            {
                result = new User();
                result.setUsername(resultSet.getString("username"));
                result.setMoney(resultSet.getInt("money"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public Boolean createUser(User user)
    {
        boolean result = true;

        try(Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    "insert into " + tableName +  " " + columnsNames + " " +
                            "values " + "('" + user.getUsername() + "', " + user.getMoney().toString() + ");"
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            result = false;
        }

        return result;
    }

    public Boolean updateUser(User user)
    {
        boolean result = true;

        try(Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    "update " + tableName +  " " + "set" + " " +
                            "money=" + user.getMoney().toString() + " " +
                            "where username=" + "'" + user.getUsername() + "'" + ";"
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }

}