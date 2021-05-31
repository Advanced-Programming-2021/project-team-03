package model.card;

import model.enums.CardAttributes;
import model.enums.SpellAndTrapIcon;

import java.util.HashMap;

public class SpellAndTrap extends Card {
    private SpellAndTrapIcon icon;
    private boolean isActive;

    private static HashMap<String, Monster> allSpellAndTrapsByName;

    public SpellAndTrap(String cardName, String cardID, String description, int price, CardAttributes attribute) {
        super(cardName, cardID, description, price, attribute);
        //TODO create a constructor
    }

    public static Card getSpellAndTrapByName(String cardName) {
        return allSpellAndTrapsByName.get(cardName);
    }

    @Override
    public String toString() {
        return "Name: " + this.cardName + "\n" +
                "Model : " + this.attribute + "\n" +
                "Type: " + this.icon + "\n" +
                "Description: " + this.description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
