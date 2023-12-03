package org.test.services.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.RoleDAO;
import org.test.dao.implementation.UserDAO;
import org.test.entity.Role;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.Helmet;
import org.test.entity.user_elements.Pick;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
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

    public void updateRole(Long id, Long newPrice) throws SQLException, NoSuchElementException {
        Role role = roleDAO.get(id).orElseThrow();
        role.setPrice(newPrice);
        roleDAO.update(role);
    }

    public void deleteRole(Long id) throws SQLException {
        Role role = roleDAO.get(id).orElseThrow();
        roleDAO.delete(role);
    }

    public Optional<Role> getRole(Long roleID) throws SQLException {
       return roleDAO.get(roleID);
    }

    public List<Role> getAllRoles() throws SQLException {
        return roleDAO.getAll();
    }


    public void buyRole(Long userID, Long roleID) throws Exception {
        User user = userDAO.get(userID).orElseThrow();
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

    public Long getPickUpgradePrice(Long userID) throws SQLException {
        User user = userDAO.get(userID).orElseThrow();
        return getPickUpgradePrice(user);
    }
    public Long getPickUpgradePrice(User user) throws SQLException {
        Float differenceProbability = -1 *(user.getPick().getRareOreProbability() - Pick.startRareOreProbability);
        return (long) (Pick.startUpgradePrice + differenceProbability * Pick.startUpgradePrice * 10);
    }

    public Long getHelmetUpgradePrice(Long userID) throws SQLException {
        User user = userDAO.get(userID).orElseThrow();
        return getHelmetUpgradePrice(user);
    }
    public Long getHelmetUpgradePrice(User user) throws SQLException {
        int difference = user.getHelmet().getLightPower() - Helmet.startLightPower;
        return Helmet.startUpgradePrice + (long) difference * Helmet.startUpgradePrice;
    }

    public Long getBagUpgradePrice(Long userID) throws SQLException {
        User user = userDAO.get(userID).orElseThrow();
        return getBagUpgradePrice(user);
    }
    public Long getBagUpgradePrice(User user) throws SQLException {
        int difference = user.getBag().getBagSize() - Bag.startBagSize;
        return Bag.startUpgradePrice + (long) difference * Bag.startUpgradePrice;
    }

    public void upgradePick(Long userID) throws Exception {
        User user = userDAO.get(userID).orElseThrow();
        Long price = getPickUpgradePrice(user);
        if (user.getMoney() < price) throw new Exception("Недостаточно монет!");

        Pick pick = user.getPick();
        pick.setOreMultiplayer(pick.getOreMultiplayer() + Pick.oreMultiplayerPerUpgrade);
        pick.setRareOreProbability(pick.getRareOreProbability() + Pick.rareOreProbabilityPerUpgrade);
        user.setPick(pick);
        user.setMoney(user.getMoney() - price);

        userDAO.update(user);
    }

    public void upgradeHelmet(Long userID) throws Exception {
        User user = userDAO.get(userID).orElseThrow();
        Long price = getHelmetUpgradePrice(user);
        if (user.getMoney() < price) throw new Exception("Недостаточно монет!");

        Helmet helmet = user.getHelmet();
        helmet.setToughness(helmet.getToughness() + Helmet.toughnessPerUpgrade);
        helmet.setLightPower(helmet.getLightPower() + Helmet.lightPowerPerUpgrade);
        user.setHelmet(helmet);
        user.setMoney(user.getMoney() - price);

        userDAO.update(user);
    }

    public void upgradeBag(Long userID) throws Exception {
        User user = userDAO.get(userID).orElseThrow();
        Long price = getBagUpgradePrice(user);
        if (user.getMoney() < price) throw new Exception("Недостаточно монет!");

        Bag bag = user.getBag();
        bag.setBagSize(bag.getBagSize() + Bag.bagSizePerUpgrade);
        user.setBag(bag);
        user.setMoney(user.getMoney() - price);

        userDAO.update(user);
    }

}
