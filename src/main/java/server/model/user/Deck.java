package server.model.user;

import server.control.MainController;
import server.control.databaseController.Database;
import server.control.databaseController.DatabaseException;
import server.model.card.Card;
import server.model.card.Monster;
import server.model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class Deck {
    private static final HashMap<String, Deck> allDecks = new HashMap<>();
    private final String deckName;
    private final HashMap<DeckType, ArrayList<Card>> decks = new HashMap<>();
    private final HashMap<DeckType, ArrayList<String>> decksCardNames = new HashMap<>();

    public static void initialize() {
        Database.updateAllDecks();
    }

    public Deck(String deckName) throws DatabaseException {
        this.deckName = deckName;

        decks.put(DeckType.MAIN, new ArrayList<>());
        decks.put(DeckType.SIDE, new ArrayList<>());

        decksCardNames.put(DeckType.MAIN, new ArrayList<>());
        decksCardNames.put(DeckType.SIDE, new ArrayList<>());

        allDecks.put(deckName, this);
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

        ArrayList<String> allCards = new ArrayList<>(decksCardNames.get(DeckType.MAIN));
        allCards.addAll(decksCardNames.get(DeckType.SIDE));

        return allCards.size() == 0 || allCards.stream().map(cardName -> Collections.frequency(allCards, cardName))
                .max(Integer::compare).get() <= 3;
    }

    public String showDeck(DeckType deckType) {
        StringBuilder showDeck = new StringBuilder();

        showDeck.append("Deck: ").append(deckName).append("\n");
        showDeck.append(deckType.getName());
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
        if (!MainController.initializing) Database.save(this);
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
        return decksCardNames.get(deckType).contains(card.getCardName());
    }

    public synchronized void removeCard(String cardName, DeckType deckType) throws DatabaseException {
        for (Card card1 : decks.get(deckType)) {
            if (card1.getCardName().equals(cardName)) {
                decks.get(deckType).remove(card1);
                break;
            }
        }
        decksCardNames.get(deckType).remove(cardName);
        updateInDatabase();
    }

    public synchronized void deleteDeck() throws DatabaseException {
        allDecks.remove(this.deckName);
        Database.remove(this);
    }

    public synchronized void addCard(Card card, DeckType deckType) throws DatabaseException {
        decks.get(deckType).add(card.cloneForDeck());
        decksCardNames.get(deckType).add(card.getCardName());
        updateInDatabase();
    }

    public synchronized void addCard(ArrayList<Card> cards, DeckType deckType) throws DatabaseException {
        cards.stream().filter(Objects::nonNull).forEach(card -> {
            decks.get(deckType).add(card.cloneForDeck());
            decksCardNames.get(deckType).add(card.getCardName());
        });
        updateInDatabase();
    }

    public boolean isDeckFull(DeckType deckType) {
        return decks.get(deckType).size() >= deckType.maxCards;
    }

    public boolean isCardMaxedOut(Card card) {
        ArrayList<String> allCards = new ArrayList<>(decksCardNames.get(DeckType.MAIN));
        allCards.addAll(decksCardNames.get(DeckType.SIDE));

        return Collections.frequency(allCards, card.getCardName()) >= 3;
    }

    public ArrayList<String> getCardNames(DeckType deckType) {
        return decksCardNames.get(deckType);
    }

    public int getNumberOfCards(DeckType deckType) {
        return decks.get(deckType).size();
    }

    public String showDeckSummary(DeckType deckType) {
        StringBuilder showDeck = new StringBuilder();

        showDeck.append("Deck: ").append(deckName).append("\n");
        showDeck.append(deckType.getName());
        showDeck.append(":\nMonsters: \n");
        decks.get(deckType).stream().filter(card -> card instanceof Monster)
                .forEach(card -> showDeck.append(card.getCardName()).append(", "));
        showDeck.append("\n\nSpell and Traps: \n");
        decks.get(deckType).stream().filter(card -> card instanceof SpellAndTrap)
                .forEach(card -> showDeck.append(card.getCardName()).append(", "));

        return showDeck.toString();
    }
}
