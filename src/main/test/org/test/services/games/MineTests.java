package org.test.services.games;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.test.dao.implementation.OreDAO;
import org.test.dao.implementation.UserDAO;
import org.test.dto.MineDTO;
import org.test.entity.Ore;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.Helmet;
import org.test.entity.user_elements.Pick;
import org.test.services.background.CooldownManager;
import org.test.utils.MathUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MineTests {
    @Mock
    private UserDAO userDAO;
    @Mock
    private OreDAO oreDAO;
    @Mock
    private CooldownManager cooldownManager;
    @Mock
    private MathUtil mathUtil;
    @Before
    public void createMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void runActivityWithoutCaveDestruction() throws SQLException {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(1L).build();

        List<Ore> ores = new ArrayList<>(List.of(Ore.builder().rarity(0.9f).price(250).build()));

        Mockito.when(userDAO.get(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(oreDAO.getALl()).thenReturn(ores);

        Mockito.when(mathUtil.getRandomFloat(0.0f, 0.9f)).thenReturn(0.8f);
        Mockito.when(mathUtil.getRandomInt(1, 3)).thenReturn(1);
        Mockito.when(mathUtil.getRandomInt(1, 10)).thenReturn(2);
        Mockito.when(mathUtil.getRandomInt(0, 10)).thenReturn(5);
        Mockito.when(mathUtil.getRandomInt(0, 70)).thenReturn(0);

        Mine mine = new Mine(userDAO, oreDAO, cooldownManager, mathUtil);
        MineDTO mineDTO = mine.runActivity(1L);

        Assertions.assertFalse(mineDTO.isUserDead());
        Assertions.assertEquals(mineDTO.getOreDTOList().size(), 1);
        Assertions.assertEquals(mineDTO.getOreDTOList().get(0).getAmount(), 4);
        Assertions.assertEquals(user.getMoney(), 250*4);
    }

    @Test
    public void runActivityWithCaveDestruction() throws SQLException {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(1L).build();

        List<Ore> ores = new ArrayList<>(List.of(Ore.builder().rarity(0.9f).price(250).build()));

        Mockito.when(userDAO.get(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(oreDAO.getALl()).thenReturn(ores);

        Mockito.when(mathUtil.getRandomFloat(0.0f, 0.9f)).thenReturn(0.8f);
        Mockito.when(mathUtil.getRandomInt(1, 3)).thenReturn(1);
        Mockito.when(mathUtil.getRandomInt(1, 10)).thenReturn(2);
        Mockito.when(mathUtil.getRandomInt(0, 10)).thenReturn(1);
        Mockito.when(mathUtil.getRandomInt(0, 70)).thenReturn(50);

        Mine mine = new Mine(userDAO, oreDAO, cooldownManager, mathUtil);
        MineDTO mineDTO = mine.runActivity(1L);

        Assertions.assertTrue(mineDTO.isUserDead());
        Assertions.assertEquals(0, mineDTO.getOreDTOList().size());
        Assertions.assertEquals(0, user.getMoney());
        Assertions.assertEquals(user.getHelmet().getMaxToughness(), user.getHelmet().getToughness());
    }

    @Test
    public void runActivityWithCaveDestructionNotDead() throws SQLException {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(1L).build();

        List<Ore> ores = new ArrayList<>(List.of(Ore.builder().rarity(0.9f).price(250).build()));

        Mockito.when(userDAO.get(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(oreDAO.getALl()).thenReturn(ores);

        Mockito.when(mathUtil.getRandomFloat(0.0f, 0.9f)).thenReturn(0.8f);
        Mockito.when(mathUtil.getRandomInt(1, 3)).thenReturn(1);
        Mockito.when(mathUtil.getRandomInt(1, 10)).thenReturn(2);
        Mockito.when(mathUtil.getRandomInt(0, 10)).thenReturn(1);
        Mockito.when(mathUtil.getRandomInt(0, 70)).thenReturn(0);

        Mine mine = new Mine(userDAO, oreDAO, cooldownManager, mathUtil);
        MineDTO mineDTO = mine.runActivity(1L);

        Assertions.assertFalse(mineDTO.isUserDead());
        Assertions.assertEquals(1, mineDTO.getOreDTOList().size());
        Assertions.assertEquals(250 * 4, user.getMoney());
        Assertions.assertEquals(user.getHelmet().getMaxToughness() - 20, user.getHelmet().getToughness());
    }

}
