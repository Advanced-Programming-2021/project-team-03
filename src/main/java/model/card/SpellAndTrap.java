package model.card;

import control.databaseController.Database;
import model.enums.CardAttributes;
import model.enums.SpellAndTrapIcon;

import java.io.IOException;
import java.util.HashMap;

public class SpellAndTrap extends Card {
    private SpellAndTrapIcon icon;
    private boolean isActive;

    private static HashMap<String, SpellAndTrap> allSpellAndTraps;


    public static void initialize() {
        try {
            allSpellAndTraps = Database.updateSpellAndTraps();
        } catch (IOException e) {
            System.out.println("Couldn't find spell and trap database files");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public SpellAndTrap(String cardName, CardAttributes attribute, String description,
                        int price, String cardID, SpellAndTrapIcon icon) {
        super(cardName, cardID, description, price, attribute);
        this.icon = icon;
        //TODO create a constructor
    }

    public static SpellAndTrap getSpellAndTrapByName(String cardName) {
        return allSpellAndTraps.get(cardName);
    }

    @Override
    public String toString() {
        return "Name: " + this.cardName + "\n" +
                "Model : " + this.attribute + "\n" +
                "Type: " + this.icon + "\n" +
                "Description: " + this.description + "\n";
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static HashMap<String, SpellAndTrap> getAllSpellAndTraps() {
        return allSpellAndTraps;
    }
}
