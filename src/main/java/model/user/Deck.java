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
    private final HashMap<DeckType, ArrayList<Card>> decks = new HashMap<>();

    public static void initialize() {
        Database.updateAllDecks();
    }

    public Deck(String deckName) throws DatabaseException {
        this.deckName = deckName;

        decks.put(DeckType.MAIN, new ArrayList<>());
        decks.put(DeckType.SIDE, new ArrayList<>());

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

    public ArrayList<Card> getDeck(DeckType deckType) {
        return decks.get(deckType);
    }

    public boolean isDeckValid() {
        if (decks.get(DeckType.MAIN).size() > DeckType.MAIN.maxCards
                || decks.get(DeckType.MAIN).size() < DeckType.MAIN.minCards
                || decks.get(DeckType.SIDE).size() > DeckType.SIDE.maxCards) return false;

        ArrayList<Card> allCards = new ArrayList<>(decks.get(DeckType.MAIN));
        allCards.addAll(decks.get(DeckType.SIDE));

        return allCards.stream().map(card -> Collections.frequency(allCards, card))
                .max(Integer::compare).get() <= 3;
    }

    public String showDeck(DeckType deckType) {
        StringBuilder showDeck = new StringBuilder();

        showDeck.append("Deck: ").append(deckName).append("\n");
        showDeck.append(deckType.name);
        showDeck.append(":\nMonsters: \n");
        decks.get(deckType).stream().filter(card -> card instanceof Monster)
                .sorted((card1, card2) -> card1.getCardName().compareToIgnoreCase(card2.getCardName()))
                .forEach(card -> showDeck.append(card.getCardName()).append(": ").append(card.getDescription()));
        showDeck.append("Spell and Traps: \n");
        decks.get(deckType).stream().filter(card -> card instanceof SpellAndTrap)
                .sorted((card1, card2) -> card1.getCardName().compareToIgnoreCase(card2.getCardName()))
                .forEach(card -> showDeck.append(card.getCardName()).append(": ").append(card.getDescription()));

        return showDeck.toString();
    }

    public void updateInDatabase() throws DatabaseException {
        Database.save(this);
    }

    public String generalOverview() {
        return this.deckName + ": main deck " + decks.get(DeckType.MAIN).size()
                + ", side deck " + decks.get(DeckType.SIDE).size() +
                (isDeckValid() ? ", Valid" : ", Invalid") + "\n";
    }

    public static Deck getByDeckName(String deckName) {
        return allDecks.get(deckName);
    }

    public boolean doesContainCard(Card card, DeckType deckType) {
        return decks.get(deckType).contains(card);
    }

    public void removeCard(Card card, DeckType deckType) throws DatabaseException {
        decks.get(deckType).remove(card);
        updateInDatabase();
    }

    public void deleteDeck() throws DatabaseException {
        allDecks.remove(this.deckName);
        Database.remove(this);
    }

    public void addCard(Card card, DeckType deckType) throws DatabaseException {
        decks.get(deckType).add(card.cloneForDeck());
        updateInDatabase();
    }

    public void addCard(ArrayList<Card> cards, DeckType deckType) throws DatabaseException {
        decks.get(deckType).addAll(cards);
        updateInDatabase();
    }

    public boolean isDeckFull(DeckType deckType) {
        return decks.get(deckType).size() >= deckType.maxCards;
    }

    public boolean isCardMaxedOut(Card card, DeckType deckType) {
        return Collections.frequency(decks.get(deckType), card) >= 3;
    }
}
