package control.databaseController;

import model.card.Card;
import model.user.Deck;
import model.user.DeckType;

import java.util.ArrayList;

public class DeckJson {
    private String deckName;
    private final ArrayList<String> mainDeck;
    private final ArrayList<String> sideDeck;

    public DeckJson(Deck deck) {
        mainDeck = new ArrayList<>();
        sideDeck = new ArrayList<>();

        deckName = deck.getDeckName();
        deck.getMainDeck().forEach(card -> mainDeck.add(card.getCardName()));
        deck.getSideDeck().forEach(card -> sideDeck.add(card.getCardName()));
    }

    public Deck convert() throws DatabaseException {
        Deck deck = new Deck(deckName);

        ArrayList<Card> mainDeckCards = new ArrayList<>();
        ArrayList<Card> sideDeckCards = new ArrayList<>();

        mainDeck.forEach(cardName -> mainDeckCards.add(Card.getCardByName(cardName)));
        sideDeck.forEach(cardName -> sideDeckCards.add(Card.getCardByName(cardName)));

        deck.addCard(mainDeckCards, DeckType.MAIN);
        deck.addCard(mainDeckCards, DeckType.SIDE);

        return deck;
    }
}
