package model.card;

import model.enums.CardAttributes;

public abstract class Card {
    protected String cardName;
    protected String cardID;
    protected String description;
    protected int price;
    protected CardAttributes attribute;

    public Card(String cardName, String cardID, String description, int price, CardAttributes attribute) {
        this.cardName = cardName;
        this.cardID = cardID;
        this.description = description;
        this.price = price;
        this.attribute = attribute;
    }
}
