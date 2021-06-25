package control;

import control.databaseController.DatabaseException;
import model.card.Card;
import model.user.Deck;
import model.user.DeckType;
import model.user.User;

import java.util.ArrayList;
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

    public boolean doesDeckExist(String deckName) {
        return Deck.getByDeckName(deckName) != null;
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

    public boolean doesCardExist(String cardName) {
        return Card.getCardByName(cardName) != null;
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

    public void removeCardFromDeck(String deckName, DeckType deckType, String cardName) {
        try {
            Deck.getByDeckName(deckName).removeCard(Card.getCardByName(cardName), deckType);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public String getUserActiveDeck(String username) {
        if (User.getByUsername(username).getActiveDeck() == null) return "No active deck\n";
        return User.getByUsername(username).getActiveDeck().generalOverview();
    }

    public ArrayList<Deck> getAllUserDecks(String username) {
        /* this method returns all decks except the active deck */
        return User.getByUsername(username).getDecks();
    }

    public Deck getDeck(String deckName) {
        return Deck.getByDeckName(deckName);
    }

    public ArrayList<Card> getAllUsersCards(String username) {
        // return all cards that owned by the user
        return User.getByUsername(username).getCards();
    }

    public void setActiveDeck(String username, String deckName) {
        try {
            User.getByUsername(username).setActiveDeck(Deck.getByDeckName(deckName));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
