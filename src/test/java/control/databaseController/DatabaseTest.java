package control.databaseController;

import control.MainController;
import model.card.Card;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @Test
    void exportCard() throws DatabaseException {
        MainController.getInstance();
        Database.save(Card.getCardByName("Raigeki"));
    }

    @Test
    void importCard() throws DatabaseException {
        MainController.getInstance();
        Assertions.assertEquals("Raigeki", Database.importCard("Raigeki").getCardName());
    }
}