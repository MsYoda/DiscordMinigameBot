package org.test.services.games;

import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.test.entity.game.hangman.HangmanSession;
import org.test.entity.game.hangman.HangmanTopic;
import org.test.utils.MathUtil;
import org.test.utils.RandomWordUtil;

import java.util.HashMap;
import java.util.List;

public class HangmanTests {
    @Mock
    private RandomWordUtil randomWordUtil;
    @Mock
    private MathUtil mathUtil;

    @Before
    public void createMocks() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void isUserLooseTrue()
    {
        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
        HangmanSession session = HangmanSession.builder().errorCount(Hangman.maxErrorCount.byteValue()).build();
        hangmanSessions.put(1L, session);

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        Assertions.assertTrue(hangman.isUserLoose(1L));
    }
    @Test
    public void isUserLooseFalse()
    {
        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
        HangmanSession session = HangmanSession.builder().errorCount((byte) (Hangman.maxErrorCount.byteValue() - 1)).build();
        hangmanSessions.put(1L, session);

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        Assertions.assertFalse(hangman.isUserLoose(1L));
    }

    @Test
    public void isUserLooseSessionDontExsist()
    {
        Hangman hangman = new Hangman(randomWordUtil, mathUtil);

        Assertions.assertTrue(hangman.isUserLoose(1L));
    }

    @Test
    public void isUserWinTrue()
    {
        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
        HangmanSession session = HangmanSession.builder().guess("boba").build();
        hangmanSessions.put(1L, session);

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        Assertions.assertTrue(hangman.isUserWin(1L));
    }

    @Test
    public void isUserWinFalse()
    {
        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
        HangmanSession session = HangmanSession.builder().guess("b#ba").build();
        hangmanSessions.put(1L, session);

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        Assertions.assertFalse(hangman.isUserWin(1L));
    }

    @Test
    public void isUserWinSessionDontExsist()
    {
        Hangman hangman = new Hangman(randomWordUtil, mathUtil);

        Assertions.assertFalse(hangman.isUserWin(1L));
    }

    @Test
    public void runActivity()
    {
        HangmanTopic topic = HangmanTopic.builder().words(List.of("potato")).build();
        Mockito.when(randomWordUtil.generateWord()).thenReturn(topic);
        Mockito.when(mathUtil.getRandomInt(Mockito.any(), Mockito.any())).thenReturn(0);

        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        hangman.runActivity(null, 1L);

        Assertions.assertNotNull(hangmanSessions.get(1L));

        HangmanSession session = hangmanSessions.get(1L);

        Assertions.assertEquals(session.getAnswer(), "potato");
        Assertions.assertEquals(session.getErrorCount(), (byte)0);
        Assertions.assertTrue(session.getGuess().contains("p"));

    }

    @Test
    public void guessWithoutErrors()
    {
        HangmanSession session = HangmanSession.builder().guess("p#####").answer("potato").errorCount((byte)0).build();

        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
        hangmanSessions.put(1L, session);

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        hangman.guess('o', 1L);

        Assertions.assertTrue(session.getGuess().contains("o"));
        Assertions.assertEquals(0, (byte) session.getErrorCount());
    }

    @Test
    public void guessCharNotFound()
    {
        HangmanSession session = HangmanSession.builder().guess("p#####").answer("potato").errorCount((byte)0).build();

        HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
        hangmanSessions.put(1L, session);

        Hangman hangman = new Hangman(randomWordUtil, mathUtil, hangmanSessions);

        hangman.guess('l', 1L);

        Assertions.assertFalse(session.getGuess().contains("o"));
        Assertions.assertEquals(1, (byte) session.getErrorCount());
    }

    @Test
    public void guessSessionDontExsist()
    {
        Hangman hangman = new Hangman(randomWordUtil, mathUtil);
        Assertions.assertNull(hangman.guess('o', 1L));

    }
}
