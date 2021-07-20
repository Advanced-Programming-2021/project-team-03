package server.control.databaseController;

import server.model.card.Card;
import server.model.user.Deck;
import server.model.user.DeckType;

import java.util.ArrayList;
import java.util.Objects;

public class DeckJson {
    private final String deckName;
    private final ArrayList<String> mainDeck;
    private final ArrayList<String> sideDeck;

    public DeckJson(Deck deck) {
        mainDeck = new ArrayList<>();
        sideDeck = new ArrayList<>();

        deckName = deck.getDeckName();
        deck.getDeck(DeckType.MAIN).forEach(card -> mainDeck.add(card.getCardName()));
        deck.getDeck(DeckType.SIDE).forEach(card -> sideDeck.add(card.getCardName()));
    }

    public Deck convert() throws DatabaseException {
        Deck deck = new Deck(deckName);

        ArrayList<Card> mainDeckCards = new ArrayList<>();
        ArrayList<Card> sideDeckCards = new ArrayList<>();

        mainDeck.stream().map(Card::getCardByName).filter(Objects::nonNull).forEach(mainDeckCards::add);
        sideDeck.stream().map(Card::getCardByName).filter(Objects::nonNull).forEach(sideDeckCards::add);

        deck.addCard(mainDeckCards, DeckType.MAIN);
        deck.addCard(sideDeckCards, DeckType.SIDE);

        return deck;
    }
}
