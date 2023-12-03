package org.test.entity.game.blackjack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {
    private String card;
    private CardSuit suit;
    //private List<CardSuit> suits = Arrays.asList(CardSuit.CLUB, CardSuit.DIAMOND, CardSuit.SPADE, CardSuit.HEART);
}
