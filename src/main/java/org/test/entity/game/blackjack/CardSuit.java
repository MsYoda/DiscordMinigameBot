package org.test.entity.game.blackjack;

public enum CardSuit {
    CLUB,
    DIAMOND,
    HEART,
    SPADE;

    public static String getSymbol(CardSuit cardSuit)
    {
        switch (cardSuit){
            case CLUB -> {
                return "\u2663";
            }
            case DIAMOND -> {
                return "\u2666";
            }
            case HEART -> {
                return "\u2665";
            }
            case SPADE -> {
                return "\u2660";
            }
        }
        return "X";
    }

}
