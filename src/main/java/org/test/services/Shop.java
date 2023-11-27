package org.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.CooldownDAO;
import org.test.dao.implementation.OreDAO;
import org.test.dao.implementation.UserDAO;
import org.test.dto.ShopDTO;
import org.test.entity.Ore;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.BagElement;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Service
public class Shop {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OreDAO oreDAO;

    public ShopDTO sellOre(Bag bag, Long userID) throws SQLException {
        ShopDTO shopDTO = new ShopDTO();
        User user = userDAO.get(userID);
        List<Ore> ores = oreDAO.getALl();

        Long sum = 0L;
        for (BagElement element : bag.getContent())
        {
            Ore ore = ores.stream().filter(x -> Objects.equals(x.getId(), element.getOreID())).findFirst().get();
            sum += ore.getPrice() * element.getOreAmount();
        }
        user.setMoney(user.getMoney() + sum);
        userDAO.update(user);

        shopDTO.setSelledSum(sum);
        return shopDTO;
    }
}
