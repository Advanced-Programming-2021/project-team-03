package model.card;

import model.enums.CardAttributes;
import model.user.Database;

import java.util.HashMap;

public abstract class Card {
    protected String cardName;
    protected String cardID; // this string is given to read the card effect from the database from the card ID field
    protected int cardIdInTheGame; // this number is a given value to the card which helps to find and identify this card later in the game
    protected String description;
    protected int price;
    protected CardAttributes attribute;

    private static final HashMap<String, Card> allCardsByName;

    static {
        allCardsByName = Database.updateAllCards();

        // TODO: We have to set card effects to them here after importing them from database
    }


    public Card(String cardName, String cardID, String description, int price, CardAttributes attribute) {
        this.cardName = cardName;
        this.cardID = cardID;
        this.description = description;
        this.price = price;
        this.attribute = attribute;
    }

    public int getCardIdInTheGame() {
        return cardIdInTheGame;
    }

    public void setCardIdInTheGame(int cardIdInTheGame) {
        this.cardIdInTheGame = cardIdInTheGame;
    }

    public String getCardName() {
        return cardName;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public CardAttributes getAttribute() {
        return attribute;
    }

    public static Card getCardByName(String cardName) {
        return allCardsByName.get(cardName);
    }
}
