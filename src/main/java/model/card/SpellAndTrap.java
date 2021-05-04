package model.card;

import model.enums.CardAttributes;
import model.enums.SpellAndTrapIcon;

public class SpellAndTrap extends Card {
    private SpellAndTrapIcon icon;

    public SpellAndTrap(String cardName, String cardID, String description, int price, CardAttributes attribute) {
        super(cardName, cardID, description, price, attribute);
        //TODO create a constructor
    }

    @Override
    public String toString() {
        return "Name: " + this.cardName + "\n" +
                "Model : " + this.attribute + "\n" +
                "Type: " + this.icon + "\n" +
                "Description: " + this.description;
    }
}
