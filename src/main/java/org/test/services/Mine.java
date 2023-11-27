package org.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.dao.implementation.CooldownDAO;
import org.test.dao.implementation.OreDAO;
import org.test.dao.implementation.UserDAO;
import org.test.dto.MineDTO;
import org.test.entity.CommandIDs;
import org.test.entity.Ore;
import org.test.entity.User;
import org.test.utils.MathUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class Mine {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CooldownDAO cooldownDAO;
    @Autowired
    private OreDAO oreDAO;

    void mineOre(User user) throws SQLException {
        List<Ore> ores = oreDAO.getALl();
        ores = ores.stream()
                .filter(ore -> user.getPick().getRareOreProbability() <= ore.getRarity())
                .sorted(Comparator.comparing(Ore::getRarity))
                .toList();

        Float totalRarity = (float) ores.stream().mapToDouble(Ore::getRarity).sum();
        Float randomValue = MathUtil.getRandomFloat(0.0f, totalRarity);

        Float cumulativeRarity = 0f;
        int oreCount = MathUtil.getRandomInt(0, 3) + (user.getHelmet().getLightPower() / 50);
        for (int i = 0; i < oreCount; i++) {
            for (Ore ore : ores) {
                cumulativeRarity += ore.getRarity();
                if (randomValue < cumulativeRarity) {
                    try {
                        user.getBag().addOre(ore, 10L);
                        break;
                    } catch (Exception e) {
                        userDAO.update(user);
                        return;
                    }
                }
            }
        }
    }

    public void caveDestructionEvent(User user)
    {
        Integer p = MathUtil.getRandomInt(0, 10);

        if (p < 2)
        {
            int damage = MathUtil.getRandomInt(0, 70) + 20;
            Integer helmetHP = user.getHelmet().getToughness();
            helmetHP -= damage;
            if (helmetHP <= 0)
            {
                helmetHP = 0;
                user.getBag().getContent().clear();
            }
            user.getHelmet().setToughness(helmetHP);
        }
    }

    public MineDTO runActivity(Long userID) throws SQLException {
        MineDTO mineDTO = new MineDTO();

        User user = userDAO.get(userID);
        user.getBag().setContent(new ArrayList<>());

        mineOre(user);
        caveDestructionEvent(user);

        if (user.getHelmet().getToughness() <= 0) mineDTO.setUserDead(true);
        mineDTO.setUserBag(user.getBag());

        userDAO.update(user);
        return mineDTO;
    }
}
