package model.card;

import model.enums.CardAttrebiute;

public abstract class Card {
    protected String cardName;
    protected String cardID;
    protected String description;
    protected int price;
    protected CardAttrebiute attribute;

    public abstract void cardAction();
}
