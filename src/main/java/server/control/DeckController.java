package server.control;

import server.control.databaseController.DatabaseException;
import server.model.card.Card;
import server.model.user.Deck;
import server.model.user.DeckType;
import server.model.user.User;

import java.util.Collections;

public class DeckController {
    private static DeckController deckController;

    private DeckController() {
    }

    public static DeckController getInstance() {
        if (deckController == null)
            deckController = new DeckController();
        return deckController;
    }

    public void createNewDeck(String username, String deckName) {
        try {
            Deck deck = new Deck(deckName);
            User.getByUsername(username).addDeck(deck);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void deleteDeck(String username, String deckName) {
        try {
            User.getByUsername(username).deleteDeck(Deck.getByDeckName(deckName));
            Deck.getByDeckName(deckName).deleteDeck();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public boolean doesUserHaveAnymoreCard(String username, String cardName, String deckName) {
        Card card = Card.getCardByName(cardName);
        Deck deck = Deck.getByDeckName(deckName);

        return Collections.frequency(User.getByUsername(username).getCards(), card) >
                Collections.frequency(deck.getCardNames(DeckType.MAIN), card.getCardName())
                        + Collections.frequency(deck.getCardNames(DeckType.SIDE), card.getCardName());
    }

    public boolean canUserAddCardToDeck(String deckName, String cardName) {
        return !Deck.getByDeckName(deckName).isCardMaxedOut(Card.getCardByName(cardName));
    }

    public void addCardToDeck(String deckName, DeckType deckType, String cardName) {
        try {
            Deck.getByDeckName(deckName).addCard(Card.getCardByName(cardName), deckType);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public String getUserActiveDeck(String username) {
        if (User.getByUsername(username).getActiveDeck() == null) return "No active deck\n";
        return User.getByUsername(username).getActiveDeck().generalOverview();
    }

    public void setActiveDeck(String username, String deckName) {
        try {
            User.getByUsername(username).setActiveDeck(Deck.getByDeckName(deckName));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
