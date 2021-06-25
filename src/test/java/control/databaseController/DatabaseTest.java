package control.databaseController;

import control.MainController;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

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

    @Test
    void importAllCards() {
        Database.updateImportedCards();
    }

    @Test
    void AITest() {
        MainController.getInstance();
        Collection<Monster> monsters = Monster.getAllMonsters().values();
        Collection<SpellAndTrap> spellAndTraps = SpellAndTrap.getAllSpellAndTraps().values();

        for (Card card : monsters) {
            System.out.print("\"" + card.getCardName() + "\",");
        }
        for (Card card : spellAndTraps) {
            System.out.print("\"" + card.getCardName() + "\",");
        }
    }
}