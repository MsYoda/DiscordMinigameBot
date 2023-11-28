package org.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.CooldownDAO;
import org.test.dao.implementation.OreDAO;
import org.test.dao.implementation.RoleDAO;
import org.test.dao.implementation.UserDAO;
import org.test.entity.Role;
import org.test.entity.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class Shop {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RoleDAO roleDAO;

    public void addRoleToShop(Role role) throws Exception {
        if (roleDAO.get(role.getId()).isPresent()) throw new Exception("Роль уже существует");
        roleDAO.add(role);
    }

    public List<Role> getAllRoles() throws SQLException {
        return roleDAO.getAll();
    }


    public void buyRole(Long userID, Long roleID) throws Exception {
        User user = userDAO.get(userID);
        Optional<Role> roleOptional = roleDAO.get(roleID);
        if (roleOptional.isEmpty())
        {
            throw new Exception("Role not found");
        }

        Role role = roleOptional.get();

        if (user.getMoney() < role.getPrice()) throw new Exception("Dont have money");
        user.setMoney(user.getMoney() - role.getPrice());

        userDAO.update(user);
    }



}
