package server.model.card;

import server.model.enums.CardAttributes;

public abstract class Card {
    protected String cardName;

    public String getCardID() {
        return cardID;
    }

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
        if (Monster.getMonsterByName(cardName) != null) return Monster.getMonsterByName(cardName);
        return SpellAndTrap.getSpellAndTrapByName(cardName);
    }

    public abstract Card cloneForDeck();
}
