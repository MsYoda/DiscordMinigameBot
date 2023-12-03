package org.test.services.games;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.dao.implementation.UserDAO;
import org.test.entity.User;
import org.test.entity.game.blackjack.Card;
import org.test.entity.game.blackjack.BlackJackSession;
import org.test.entity.game.blackjack.CardSuit;
import org.test.utils.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class BlackJack {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private MathUtil mathUtil;
    private final HashMap<Long, BlackJackSession> blackJackSessions;

    public BlackJack(UserDAO userDAO, MathUtil mathUtil, HashMap<Long, BlackJackSession> blackJackSessions){
        this.blackJackSessions = blackJackSessions;
        this.mathUtil = mathUtil;
        this.userDAO = userDAO;
    }
    @Autowired
    public BlackJack(UserDAO userDAO, MathUtil mathUtil){
        this.blackJackSessions = new HashMap<>();
        this.mathUtil = mathUtil;
        this.userDAO = userDAO;
    }
    public static final Integer winMultiplayer = 2;

    public void deleteSession(Long userID)
    {
        blackJackSessions.remove(userID);
    }
    private Integer getSumOfHand(List<Card> hand)
    {
        Integer sum = 0;
        for (Card card : hand)
        {
            sum += getPoints(card, sum);
        }
        return sum;
    }
    private Integer getPoints(Card card, Integer sum)
    {
        int value = 0;
        try {
            value = Integer.parseInt(card.getCard());
        }
        catch (NumberFormatException e)
        {
            switch (card.getCard())
            {
                case "J", "Q", "K" -> value = 10;
                case "A" -> {
                    value = sum >= 21 ? 1 : 11;
                }
            }
        }
        return value;

    }

    private void deal(List<Card> deck, List<Card> hand, Integer count)
    {
        for (int i = 0; i < count; i++) {
            int ind = mathUtil.getRandomInt(0, deck.size() - 1);
            Card card = deck.get(ind);

            deck.remove(ind);

            hand.add(card);
        }
    }
    public boolean isSessionExist(Long userID)
    {
        return blackJackSessions.containsKey(userID);
    }
    public boolean isGameOver(Long userID)
    {
        BlackJackSession blackJackSession = blackJackSessions.get(userID);
        return getSumOfHand(blackJackSession.getUserDeck()) >= 21 || getSumOfHand(blackJackSession.getBotDeck()) >= 21 || blackJackSession.isBotEnough();
    }

    public boolean isUserWin(Long userID)
    {
        BlackJackSession blackJackSession = blackJackSessions.get(userID);
        int userDiff = getSumOfHand(blackJackSession.getUserDeck()) - 21;
        int botDiff = getSumOfHand(blackJackSession.getBotDeck()) - 21;

        if (userDiff <= 0)
        {
            if (botDiff <= 0)
            {
                return userDiff >= botDiff;
            }
            return true;
        }
        if (userDiff > 0 && botDiff > 0) {
            return userDiff <= botDiff;
        }
        return false;

    }
    public BlackJackSession botThinks(Long userID)
    {
        BlackJackSession blackJackSession = blackJackSessions.get(userID);
        if (blackJackSession.isBotEnough())
        {
            return blackJackSession;
        }

        List<Card> cards = blackJackSession.getBotDeck();

        Integer sum = getSumOfHand(cards);

        if (sum < 17)
        {
            deal(blackJackSession.getDeck(), cards, 1);
        }
        else {
            blackJackSession.setBotEnough(true);
        }

        if (blackJackSession.getBotDeck().size() > blackJackSession.getUserDeck().size())
        {
            blackJackSession.setBotEnough(true);
        }

        return blackJackSession;
    }
    public BlackJackSession deal(Long userID)
    {
        BlackJackSession blackJackSession = blackJackSessions.get(userID);
        deal(blackJackSession.getDeck(), blackJackSession.getUserDeck(), 1);
        return blackJackSession;
    }

    private List<Card> getFullDeck()
    {
        List<Card> deckElements = new ArrayList<>();
        for (int i = 2; i <= 10; i++)
        {
            for (CardSuit suit: CardSuit.values()) {
                Card card = Card.builder().card(Integer.toString(i)).suit(suit).build();
                deckElements.add(card);
            }
        }
        for (CardSuit suit: CardSuit.values()) {
            deckElements.add(Card.builder().card("J").suit(suit).build());
            deckElements.add(Card.builder().card("Q").suit(suit).build());
            deckElements.add(Card.builder().card("K").suit(suit).build());
            deckElements.add(Card.builder().card("A").suit(suit).build());
        }

        return deckElements;
    }
    public BlackJackSession runActivity(Long userID, Long bet) throws Exception {
        if (isSessionExist(userID)) throw new Exception("Игра уже идёт!");

        User user = userDAO.get(userID).orElseThrow();
        if (user.getMoney() < bet) throw new Exception("Недостаточно монет");
        user.setMoney(user.getMoney() - bet);

        List<Card> deck = getFullDeck();
        List<Card> userDeck = new ArrayList<>();
        List<Card> botDeck = new ArrayList<>();

        deal(deck, userDeck, 1);
        deal(deck, botDeck, 1);

        BlackJackSession blackJackSession = BlackJackSession.builder()
                .botDeck(botDeck)
                .userDeck(userDeck)
                .deck(deck)
                .userID(userID)
                .bet(bet)
                .botEnough(false)
                .build();
        blackJackSessions.put(userID, blackJackSession);
        userDAO.update(user);

        return blackJackSession;
    }

}
