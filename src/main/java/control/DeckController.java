package control;

import model.card.Card;
import model.user.Deck;
import model.user.User;
import org.json.JSONArray;

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
        //TODO
        return false;
    }

    public void createNewDeck(String username, String deckName) {
        //TODO
    }

    public void deleteDeck(String username, String deckName) {
        //TODO
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
        return false;
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
