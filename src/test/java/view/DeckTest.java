package view;

import control.DeckController;
import control.MainController;
import control.UserController;
import control.databaseController.DatabaseException;
import control.game.GameController;
import model.card.Card;
import model.user.DeckType;
import model.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void newDeck() {
        MainController mainController = MainController.getInstance();
        UserController userController = UserController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        userController.registerUsername("newDeck", "pass", "newDeck");
        view.loginUser("user login -u newDeck -p pass");

        view.preparatoryDeckWorks("deck create newDeckTest", "Create deck", 3);
        assert DeckController.getInstance().doesDeckAlreadyExist("newDeckTest");

        view.logoutUser();
        View.testing = false;
    }

    @Test
    void addCardToDeck() throws DatabaseException {
        MainController mainController = MainController.getInstance();
        UserController userController = UserController.getInstance();
        DeckController deckController = DeckController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        userController.registerUsername("addCard", "pass", "addCard");
        view.loginUser("user login -u addCard -p pass");
        User.getByUsername("addCard").addCard(Card.getCardByName("Call of The Haunted"));
        User.getByUsername("addCard").addCard(Card.getCardByName("Fireyarou"));



        view.preparatoryDeckWorks("deck create addCard", "Create deck", 3);

        view.addOrDeleteCardFromDeck("deck add-card -d addCard -c Fireyarou -s",
        "Add card to deck", 6);
        view.addOrDeleteCardFromDeck("deck add-card -d addCard -c Call of The Haunted",
                "Add card to deck", 17);

        ArrayList<Card> mainDeck = deckController.getDeck("addCard").getDeck(DeckType.MAIN);
        ArrayList<Card> sideDeck = deckController.getDeck("addCard").getDeck(DeckType.SIDE);

        assert sideDeck.size() == 1;
        assert mainDeck.size() == 1;
        assert mainDeck.get(0).getCardName().equals("Call of The Haunted");
        assert sideDeck.get(0).getCardName().equals("Fireyarou");

        view.logoutUser();
        View.testing = false;
    }



}