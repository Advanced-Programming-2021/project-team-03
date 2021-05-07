package control;

import model.card.Card;
import model.user.DatabaseException;
import model.user.Deck;
import model.user.User;

import java.util.ArrayList;

public class DeckController {
    private static DeckController deckController;

    private DeckController() {
    }

    public static DeckController getInstance() {
        if (deckController == null)
            deckController = new DeckController();
        return deckController;
    }

    public boolean doesDeckAlreadyExist(String username, String deckName) {
        return Deck.getByDeckName(deckName) != null;
    }

    public void createNewDeck(String username, String deckName) {
        try {
            Deck deck = new Deck(deckName);
            User.getByUsername(username).addDeck(deck);
        } catch (DatabaseException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void deleteDeck(String username, String deckName) {
        /*try {
            Deck.getByDeckName(deckName).r
            User.getByUsername(username).addDeck(deck);
        } catch (DatabaseException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }*/ // TODO
    }

    public boolean doesCardExist(String username, String cardName) {
        //TODO
        return false;
    }

    public boolean doesUserHaveAnymoreCard(String username, String cardName) {
        //TODO
        return false;
    }

    public boolean isDeckFull(String username, String deckName, String deckType) {
        //TODO
        return false;
    }

    public boolean canUserAddCardToDeck(String username, String deckName, String deckType, String cardName) {
        //TODO
        return false;
    }

    public void addCardToDeck(String username, String deckName, String deckType, String cardName) {
        //TODO
    }

    public boolean doesDeckContainThisCard(String username, String deckName, String cardName) {
        return false; // TODO
    }

    public void removeCardFromDeck(String username, String deckName, String deckType, String cardName) {

    }

    public Deck getUserActiveDeck(String username) {
        //TODO
        return null;
    }

    public ArrayList<Deck> getAllUsersDecks() {
         /* this method returns all decks except the active deck */
        return null;
    }

    public Deck getDeck(String username, String deckName) {
        return null;
    }

    public ArrayList<Card> getAllUsersCards(String username) {
        // return all cards that owned by the user
        return null;
    }
}
