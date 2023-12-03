package org.test.entity.game.blackjack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackJackSession {
    private Long userID;
    private List<Card> deck;
    private List<Card> userDeck;
    private List<Card> botDeck;
    private Long bet;
    private boolean botEnough;
}
