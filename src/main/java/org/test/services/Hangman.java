package org.test.services;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.stereotype.Service;
import org.test.entity.minigames.HangmanSession;
import org.test.entity.minigames.HangmanTopic;
import org.test.utils.MathUtil;
import org.test.utils.RandomWordUtil;

import java.util.HashMap;
import java.util.Optional;

@Service
public class Hangman {
    private final HashMap<Long, HangmanSession> hangmanSessions = new HashMap<>();
    public HangmanSession runActivity(InteractionHook interactionHook, Long userID)
    {
        HangmanTopic topic = RandomWordUtil.generateWord();
        HangmanSession hangmanSession = HangmanSession.builder()
                .interactionHook(interactionHook)
                .errorCount((byte) 0)
                .answer(topic.getWords().get(0))
                .hint(topic.getTopic())
                .build();
        String guess = "#";
        guess = guess.repeat(hangmanSession.getAnswer().length());

        hangmanSession.setGuess(guess);
        hangmanSessions.put(userID, hangmanSession);

        int ind = MathUtil.getRandomInt(0, hangmanSession.getAnswer().length() - 1);
        char toOpen = hangmanSession.getAnswer().charAt(ind);

        hangmanSession = guess(toOpen, userID);

        return hangmanSession;
    }

    public HangmanSession guess(Character guess, Long userID)
    {
        HangmanSession session = hangmanSessions.get(userID);
        if (session == null) return null;
        char[] guessArr = session.getGuess().toCharArray();
        boolean find = false;
        for (int i = 0; i < session.getAnswer().length(); i++)
        {
            if (session.getAnswer().charAt(i) == guess)
            {
                guessArr[i] = guess;
                find = true;
            }
        }
        session.setGuess(String.valueOf(guessArr));
        if (!find) session.setErrorCount((byte) (session.getErrorCount() + 1));
        return session;
    }

    public boolean isUserLoose(Long userID)
    {
        HangmanSession session = hangmanSessions.get(userID);
        if (session == null) return true;

        return session.getErrorCount() >= 8;
    }

    public boolean isUserWin(Long userID)
    {
        HangmanSession session = hangmanSessions.get(userID);
        if (session == null) return false;

        return !session.getGuess().contains(String.valueOf('#'));
    }

    public Optional<InteractionHook> getInteraction(Long userID)
    {
        if (!hangmanSessions.containsKey(userID)) return Optional.empty();
        else return Optional.of(hangmanSessions.get(userID).getInteractionHook());
    }

    public Optional<HangmanSession> getSession(Long userID)
    {
        if (!hangmanSessions.containsKey(userID)) return Optional.empty();
        else return Optional.of(hangmanSessions.get(userID));
    }

    public void deleteSession(Long userID)
    {
        hangmanSessions.remove(userID);
    }

}
