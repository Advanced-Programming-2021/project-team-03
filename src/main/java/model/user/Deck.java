package model.user;

import control.databaseController.Database;
import control.databaseController.DatabaseException;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Deck {
    private static final HashMap<String, Deck> allDecks = new HashMap<>();
    private String deckName;
    private final ArrayList<Card> mainDeck;
    private final ArrayList<Card> sideDeck;

    public static void initialize() {
        Database.updateAllDecks();
    }

    public Deck(String deckName) throws DatabaseException {
        this.deckName = deckName;

        mainDeck = new ArrayList<>();
        sideDeck = new ArrayList<>();

        allDecks.put(deckName, this);
        updateInDatabase();
    }

    public void rename(String newName) throws DatabaseException {
        allDecks.remove(this.deckName);
        Database.remove(this);
        allDecks.put(newName, this);
        this.deckName = newName;
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

        ArrayList<Card> allCards = new ArrayList<>(mainDeck);
        allCards.addAll(sideDeck);

        return allCards.stream().map(card -> Collections.frequency(allCards, card))
                .max(Integer::compare).get() <= 3;
    }

    public String showDeck(DeckType deckType) {
        StringBuilder showDeck = new StringBuilder();
        switch (deckType) {
            case SIDE -> {
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
            case MAIN -> {
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
                (isDeckValid() ? ", Valid" : ", Invalid") + "\n";
    }

    public static Deck getByDeckName(String deckName) {
        return allDecks.get(deckName);
    }

    public boolean doesContainCard(Card card, DeckType deckType) {
        if (deckType == DeckType.MAIN) return mainDeck.contains(card);
        return sideDeck.contains(card);
    }

    public void removeCard(Card card, DeckType deckType) throws DatabaseException {
        if (deckType == DeckType.MAIN) mainDeck.remove(card);
        else sideDeck.remove(card);
        updateInDatabase();
    }

    public void deleteDeck() throws DatabaseException {
        allDecks.remove(this.deckName);
        Database.remove(this);
    }

    public void addCard(Card card, DeckType deckType) throws DatabaseException {
        if (deckType == DeckType.MAIN) mainDeck.add(card);
        else sideDeck.add(card);
        updateInDatabase();
    }

    public void addCard(ArrayList<Card> cards, DeckType deckType) throws DatabaseException {
        if (deckType == DeckType.MAIN) mainDeck.addAll(cards);
        else sideDeck.addAll(cards);
        updateInDatabase();
    }

    public boolean isDeckFull(DeckType deckType) {
        if (deckType == DeckType.MAIN) return mainDeck.size() >= 60;
        else return sideDeck.size() >= 15;
    }

    public boolean isCardMaxedOut(Card card, DeckType deckType) {
        if (deckType == DeckType.MAIN) return Collections.frequency(mainDeck, card) >= 3;
        else return Collections.frequency(sideDeck, card) >= 3;
    }
}
