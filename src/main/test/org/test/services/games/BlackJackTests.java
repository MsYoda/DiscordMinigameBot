package org.test.services.games;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.test.dao.implementation.UserDAO;
import org.test.entity.User;
import org.test.entity.game.blackjack.BlackJackSession;
import org.test.entity.game.blackjack.Card;
import org.test.entity.game.blackjack.CardSuit;
import org.test.utils.MathUtil;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertThrows;

public class BlackJackTests {

    @Mock
    private UserDAO userDAO;
    @Mock
    private MathUtil mathUtil;
    @Before
    public void createMocks() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testIsUserWinWithUser21()
    {
        BlackJackSession blackJackSession = BlackJackSession.builder()
                .userDeck(List.of(new Card("7", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .botDeck(List.of(new Card("4", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);

        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);

        Assertions.assertTrue(blackJack.isUserWin(1L));
    }

    @Test
    public void testIsUserWinWithUserAndBotMoreThan21()
    {
        BlackJackSession blackJackSession = BlackJackSession.builder()
                .userDeck(List.of(new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .botDeck(List.of(new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("6", CardSuit.CLUB)))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);

        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);

        Assertions.assertTrue(blackJack.isUserWin(1L));
    }

    @Test
    public void testIsUserWinWithUserMoreThan21()
    {
        BlackJackSession blackJackSession = BlackJackSession.builder()
                .userDeck(List.of(new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .botDeck(List.of(new Card("4", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);

        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);

        Assertions.assertFalse(blackJack.isUserWin(1L));
    }

    @Test
    public void testIsUserWinWithUserLess21AndBotMore21()
    {
        BlackJackSession blackJackSession = BlackJackSession.builder()
                .userDeck(List.of(new Card("10", CardSuit.CLUB), new Card("7", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .botDeck(List.of(new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("4", CardSuit.CLUB)))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);

        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);

        Assertions.assertTrue(blackJack.isUserWin(1L));
    }

    @Test
    public void testBotThinkNotEnough()
    {
        Mockito.when(mathUtil.getRandomInt(Mockito.any(), Mockito.any())).thenReturn(0);

        BlackJackSession blackJackSession = BlackJackSession.builder()
                .deck(new ArrayList<>(List.of(new Card("J", CardSuit.SPADE))))
                .userDeck(new ArrayList<>(Arrays.asList(new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB))))
                .botDeck(new ArrayList<>(List.of(new Card("10", CardSuit.CLUB))))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);


        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);
        blackJack.botThinks(1L);
        Assertions.assertFalse(sessionHashMap.get(1L).isBotEnough());
    }

    @Test
    public void testBotThinkEnoughMore17()
    {
        Mockito.when(mathUtil.getRandomInt(Mockito.any(), Mockito.any())).thenReturn(0);

        BlackJackSession blackJackSession = BlackJackSession.builder()
                .deck(new ArrayList<>(List.of(new Card("J", CardSuit.SPADE))))
                .userDeck(new ArrayList<>(Arrays.asList(new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB), new Card("10", CardSuit.CLUB))))
                .botDeck(new ArrayList<>(List.of(new Card("10", CardSuit.CLUB), new Card("9", CardSuit.CLUB))))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);


        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);
        blackJack.botThinks(1L);
        Assertions.assertTrue(sessionHashMap.get(1L).isBotEnough());
    }
    @Test
    public void testBotThinkWhenUserEnoughSetEnough()
    {
        Mockito.when(mathUtil.getRandomInt(Mockito.any(), Mockito.any())).thenReturn(0);

        BlackJackSession blackJackSession = BlackJackSession.builder()
                .deck(new ArrayList<>(List.of(new Card("J", CardSuit.SPADE))))
                .userDeck(new ArrayList<>(Arrays.asList(new Card("10", CardSuit.CLUB))))
                .botDeck(new ArrayList<>(List.of(new Card("10", CardSuit.CLUB), new Card("9", CardSuit.CLUB))))
                .build();
        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);


        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);
        blackJack.botThinks(1L);
        Assertions.assertTrue(sessionHashMap.get(1L).isBotEnough());
    }

    @Test
    public void testRunActivity() throws Exception {
        User user = User.builder().money(500L).build();
        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        BlackJack blackJack = new BlackJack(userDAO, mathUtil);
        BlackJackSession session = blackJack.runActivity(1L, 200L);

       Assertions.assertEquals(session.getBotDeck().size(), session.getUserDeck().size(), BlackJack.startCardCount);
       Assertions.assertEquals(user.getMoney(), 500L - 200L);
    }
    @Test
    public void testRunActivityWithMoneyException() throws Exception {
        User user = User.builder().money(150L).build();
        Mockito.when(userDAO.get(1L)).thenReturn(Optional.of(user));

        BlackJack blackJack = new BlackJack(userDAO, mathUtil);

        assertThrows(Exception.class, ()->{
            BlackJackSession session = blackJack.runActivity(1L, 500L);
        });

    }

    @Test
    public void testRunActivityWithSessionException() throws Exception {
        BlackJackSession blackJackSession = BlackJackSession.builder()
                .userID(1L)
                .build();

        HashMap<Long, BlackJackSession> sessionHashMap = new HashMap<>();
        sessionHashMap.put(1L, blackJackSession);

        BlackJack blackJack = new BlackJack(userDAO, mathUtil, sessionHashMap);

        assertThrows(Exception.class, ()->{
            BlackJackSession session = blackJack.runActivity(1L, 200L);
        });

    }
}
