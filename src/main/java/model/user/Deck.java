package model.user;

import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Comparator;

public class Deck {
    private String deckName;
    private ArrayList<Card> mainDeck;
    private ArrayList<Card> sideDeck;
    private boolean isValid;

    public Deck(User owner, String deckName) {

    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckName() {
        return deckName;
    }

    public ArrayList<Card> getMainDeck() {
        return mainDeck;
    }

    public ArrayList<Card> getSideDeck() {
        return sideDeck;
    }

    public void addDeckToUser(User owner) {

    }

    public void removeDeckFromUser(User owner) {

    }

    public boolean isDeckValid() {
        return isValid;
    }

    public String showDeck(String deckType) {
        StringBuilder showDeck = new StringBuilder();
        if (deckType.equals("Side")) {
            showDeck.append("Deck: ").append(deckName).append("\n");
            showDeck.append("Side deck: \n");
            showDeck.append("Monsters: \n");
            sideDeck.stream().filter(card -> card instanceof Monster)
                    .sorted((card1, card2) -> card1.getCardName().compareToIgnoreCase(card2.getCardName()))
                    .forEach(card -> showDeck.append(card.getCardName()).append(": ").append(card.getDescription()));
            showDeck.append("Spell and Traps: \n");
            sideDeck.stream().filter(card -> card instanceof SpellAndTrap)
                    .sorted((card1, card2) -> card1.getCardName().compareToIgnoreCase(card2.getCardName()))
                    .forEach(card -> showDeck.append(card.getCardName()).append(": ").append(card.getDescription()));
        } else {
            showDeck.append("Deck: ").append(deckName).append("\n");
            showDeck.append("Main deck: \n");
            showDeck.append("Monsters: \n");
            mainDeck.stream().filter(card -> card instanceof Monster)
                    .sorted((card1, card2) -> card1.getCardName().compareToIgnoreCase(card2.getCardName()))
                    .forEach(card -> showDeck.append(card.getCardName()).append(": ").append(card.getDescription()));
            showDeck.append("Spell and Traps: \n");
            mainDeck.stream().filter(card -> card instanceof SpellAndTrap)
                    .sorted((card1, card2) -> card1.getCardName().compareToIgnoreCase(card2.getCardName()))
                    .forEach(card -> showDeck.append(card.getCardName()).append(": ").append(card.getDescription()));
        }
        return showDeck.toString();
    }

    public String generalOverview() {
        if (isValid)
            return this.deckName + ": main deck " + this.mainDeck.size() + ", side deck " + this.sideDeck.size() + ", Valid";
        else
            return this.deckName + ": main deck " + this.mainDeck.size() + ", side deck " + this.sideDeck.size() + ", Invalid";
    }
}
