package model.user;

import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

public class Deck {
    private static final HashMap<String, Deck> allDecks;
    private String deckName;
    private ArrayList<Card> mainDeck;
    private ArrayList<Card> sideDeck;

    static {
        allDecks = Database.updateAllDecks();
    }

    public Deck(String deckName) throws DatabaseException {
        this.deckName = deckName;

        allDecks.put(deckName, this);
        updateInDatabase();
    }

    // TODO: add remove deck

    public void setDeckName(String deckName) throws DatabaseException {
        allDecks.remove(this.deckName);
        Database.remove(this);
        allDecks.put(deckName, this);
        this.deckName = deckName;
        updateInDatabase();
        // TODO (high-priority!): remove the previous deck from database
    }

    public void addCardToMainDeck(Card card) throws DatabaseException {
        mainDeck.add(card);
        updateInDatabase();
    }

    public void addCardToMainDeck(ArrayList<Card> cards) throws DatabaseException {
        mainDeck.addAll(cards);
        updateInDatabase();
    }

    public void addCardToSideDeck(Card card) throws DatabaseException {
        sideDeck.add(card);
        updateInDatabase();
    }

    public void addCardToSideDeck(ArrayList<Card> cards) throws DatabaseException {
        sideDeck.addAll(cards);
        updateInDatabase();
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

    public boolean isDeckValid() {
        if (mainDeck.size() > 60 || mainDeck.size() < 40 || sideDeck.size() > 15) return false;

         return Stream.concat(mainDeck.stream(), sideDeck.stream())
                .map(card -> Collections.frequency(mainDeck, card) + Collections.frequency(sideDeck, card))
                .max(Integer::compare).get() <= 3;
    }

    public String showDeck(String deckType) {
        StringBuilder showDeck = new StringBuilder();
        switch (deckType) {
            case "Side" -> {
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
            }
            case "Main" -> {
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
        }
        return showDeck.toString();
    }

    public void updateInDatabase() throws DatabaseException {
        Database.save(this);
    }

    public String generalOverview() {
        return this.deckName + ": main deck " + this.mainDeck.size() + ", side deck " + this.sideDeck.size() +
                (isDeckValid() ? ", Valid" : ", Invalid");
    }

    public static Deck getByDeckName(String deckName) {
        return allDecks.get(deckName);
    }
}
