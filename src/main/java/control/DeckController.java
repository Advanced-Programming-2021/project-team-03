package control;

import model.card.Card;
import model.user.Deck;
import model.user.User;

public class DeckController {
    private static DeckController deckController;

    private DeckController() {
    }

    public static DeckController getInstance() {
        if (deckController == null)
            deckController = new DeckController();
        return deckController;
    }

    public boolean doesDeckAlreadyExists(String username, String deckName) {
        //TODO
        return false;
    }

    public void createNewDeck(String username, String deckName) {
        //TODO
    }

    public void deleteDeck(String username, String deckName) {
        //TODO
    }

    public boolean doesCardExists(String username, String cardName) {
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
}
