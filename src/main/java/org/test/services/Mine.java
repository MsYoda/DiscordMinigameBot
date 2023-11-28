package org.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.dao.implementation.CooldownDAO;
import org.test.dao.implementation.OreDAO;
import org.test.dao.implementation.UserDAO;
import org.test.dto.MineDTO;
import org.test.dto.OreDTO;
import org.test.dto.ShopDTO;
import org.test.entity.CommandIDs;
import org.test.entity.Ore;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.BagElement;
import org.test.utils.MathUtil;

import java.sql.SQLException;
import java.util.*;

@Service
public class Mine {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CooldownDAO cooldownDAO;
    @Autowired
    private OreDAO oreDAO;

    private List<OreDTO> mapBagToOreDTOList(List<BagElement> content, List<Ore> ores)
    {
        List<OreDTO> result = new ArrayList<>();
        for (BagElement element: content) {
            Ore ore = ores.stream().filter(x -> Objects.equals(x.getId(), element.getOreID())).findFirst().get();

            OreDTO oreDTO = OreDTO.builder()
                    .name(ore.getName())
                    .price(Long.valueOf(ore.getPrice()))
                    .amount(element.getOreAmount())
                    .build();
            result.add(oreDTO);
        }
        return result;
    }

    private void sellOre(User user, List<Ore> ores) throws SQLException {
        Long sum = 0L;
        for (BagElement element : user.getBag().getContent())
        {
            Ore ore = ores.stream().filter(x -> Objects.equals(x.getId(), element.getOreID())).findFirst().get();
            sum += ore.getPrice() * element.getOreAmount();
        }
        user.setMoney(user.getMoney() + sum);
    }
    private void mineOre(User user, List<Ore> ores) throws SQLException {
        Float totalRarity = (float) ores.stream().mapToDouble(Ore::getRarity).sum();
        Float randomValue = MathUtil.getRandomFloat(0.0f, totalRarity);

        Float cumulativeRarity = 0f;
        int oreCount = MathUtil.getRandomInt(1, 3);
        oreCount += (user.getHelmet().getLightPower() / 50);
        for (int i = 0; i < oreCount; i++) {
            for (Ore ore : ores) {
                cumulativeRarity += ore.getRarity();
                if (randomValue < cumulativeRarity) {
                    try {
                        user.getBag().addOre(ore, 2L);
                        break;
                    } catch (Exception e) {
                        return;
                    }
                }
            }
            cumulativeRarity = 0f;
            randomValue = MathUtil.getRandomFloat(0.0f, totalRarity);
        }
    }

    private void caveDestructionEvent(User user)
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

        List<Ore> ores = oreDAO.getALl();
        ores = ores.stream()
                .filter(ore -> user.getPick().getRareOreProbability() <= ore.getRarity())
                .sorted(Comparator.comparing(Ore::getRarity))
                .toList();

        mineOre(user, ores);
        caveDestructionEvent(user);

        if (user.getHelmet().getToughness() <= 0) mineDTO.setUserDead(true);

        sellOre(user, ores);

        mineDTO.setOreDTOList(mapBagToOreDTOList(user.getBag().getContent(), ores));

        userDAO.update(user);
        return mineDTO;
    }
}
