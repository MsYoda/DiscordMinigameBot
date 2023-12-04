package org.test.services.games;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.test.dao.implementation.RoleDAO;
import org.test.dao.implementation.UserDAO;
import org.test.entity.Role;
import org.test.entity.User;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.Helmet;
import org.test.entity.user_elements.Pick;

import java.util.Optional;

import static org.junit.Assert.assertThrows;

public class ShopTests {
    @Mock
    private UserDAO userDAO;
    @Mock
    private RoleDAO roleDAO;

    @Before
    public void createMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void buyRole() throws Exception {
        User user = User.builder().money(500L).id(1L).build();
        Role role = Role.builder().id(1L).price(400L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));
        Mockito.when(roleDAO.get(1L)).thenReturn(Optional.of(role));

        Shop shop = new Shop(userDAO, roleDAO);

        Assertions.assertDoesNotThrow(() -> shop.buyRole(1L, 1L));
        Assertions.assertEquals(user.getMoney(), 100L);
    }

    @Test
    public void buyRoleNotEnoughMoney() throws Exception {
        User user = User.builder().money(300L).id(1L).build();
        Role role = Role.builder().id(1L).price(400L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));
        Mockito.when(roleDAO.get(1L)).thenReturn(Optional.of(role));

        Shop shop = new Shop(userDAO, roleDAO);

        Assertions.assertThrows(Exception.class, () -> shop.buyRole(1L, 1L));
        Assertions.assertEquals(user.getMoney(), 300L);
    }

    @Test
    public void buyRoleDontExsist() throws Exception {
        User user = User.builder().money(300L).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));
        Mockito.when(roleDAO.get(1L)).thenReturn(Optional.empty());

        Shop shop = new Shop(userDAO, roleDAO);

        Assertions.assertThrows(Exception.class, () -> shop.buyRole(1L, 1L));
        Assertions.assertEquals(user.getMoney(), 300L);
    }

    @Test
    public void upgradePick() throws Exception {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(Pick.startUpgradePrice + 1).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        Shop shop = new Shop(userDAO, roleDAO);
        shop.upgradePick(1L);

        Assertions.assertEquals(user.getMoney(), 1);
    }

    @Test
    public void upgradePickNotEnoughMoney() throws Exception {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        Shop shop = new Shop(userDAO, roleDAO);


        Assertions.assertThrows(Exception.class, () -> shop.upgradePick(1L));
    }

    @Test
    public void upgradeHelmet() throws Exception {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money((long) (Helmet.startUpgradePrice + 1)).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        Shop shop = new Shop(userDAO, roleDAO);
        shop.upgradeHelmet(1L);

        Assertions.assertEquals(user.getMoney(), 1);
    }

    @Test
    public void upgradeHelmetNotEnoughMoney() throws Exception {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        Shop shop = new Shop(userDAO, roleDAO);


        Assertions.assertThrows(Exception.class, () -> shop.upgradeHelmet(1L));
    }

    @Test
    public void upgradeBag() throws Exception {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money((long) (Bag.startUpgradePrice + 1)).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        Shop shop = new Shop(userDAO, roleDAO);
        shop.upgradeBag(1L);

        Assertions.assertEquals(user.getMoney(), 1);
    }

    @Test
    public void upgradeBagNotEnoughMoney() throws Exception {
        Helmet helmet = Helmet.builder().lightPower(Helmet.startLightPower).maxToughness(Helmet.startToughness).toughness(Helmet.startToughness).build();
        Pick pick = Pick.builder().oreMultiplayer(Pick.startOreMultiplayer).rareOreProbability(Pick.startRareOreProbability).build();
        Bag bag = Bag.builder().bagSize(Bag.startBagSize).build();
        User user = User.builder().bag(bag).helmet(helmet).pick(pick).money(0L).id(1L).build();

        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        Shop shop = new Shop(userDAO, roleDAO);


        Assertions.assertThrows(Exception.class, () -> shop.upgradeBag(1L));
    }

}
