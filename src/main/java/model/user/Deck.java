package model.user;

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

    public void addDeckToUser(User owner) {

    }

    public void removeDeckFromUser(User owner) {

    }

    public boolean isDeckValid() {
        //TODO
        return true;
    }

    public String showDeck(String deckType) {
        /*TODO showing the details of the given deck
         * for show deck request
         * */
        return null;
    }

    public String generalOverview() {
        /*TODO showing the general overview
         * for show all deck request
         * */
        return null;
    }
}
