package model.card;

import model.enums.CardAttributes;

public abstract class Card {
    protected String cardName;
    protected String cardID; // this string is given to read the card effect from the database from the card ID field
    protected int cardIdInTheGame; // this number is a given value to the card which helps to find and identify this card later in the game
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

    public int getCardIdInTheGame() {
        return cardIdInTheGame;
    }

    public void setCardIdInTheGame(int cardIdInTheGame) {
        this.cardIdInTheGame = cardIdInTheGame;
    }
}
