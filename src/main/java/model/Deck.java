package model;

import model.card.Card;

import java.util.ArrayList;

public class Deck {
    private String deckName;
    private ArrayList<Card> mainDeck;
    private ArrayList<Card> sideDeck;

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

    public void addDeck(User owner) {

    }

    public void removeDeck(User owner) {

    }

    public boolean isDeckValid() {
        //TODO
        return true;
    }
}
