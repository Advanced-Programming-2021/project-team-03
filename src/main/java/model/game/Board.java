package model.game;

import model.user.Deck;
import model.user.User;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private User owner;
    private HashMap<Monster, Integer[]> monstersInField;
    private HashMap<SpellAndTrap, Integer[]> spellAndTrapsInField;
    private ArrayList<Card> graveyard;
    private Card fieldCard;
    private ArrayList<Card> remainingCards;
    private ArrayList<Card> inHandCards;

    public Board(Deck deck, User player) {

    }

    public User getOwner() {
        return owner;
    }

    public HashMap<Monster, Integer[]> getMonstersInField() {
        return monstersInField;
    }

    public HashMap<SpellAndTrap, Integer[]> getSpellAndTrapsInField() {
        return spellAndTrapsInField;
    }

    public ArrayList<Card> getGraveyard() {
        return graveyard;
    }

    public Monster getMonsterByPosition(int position) {
        // TODO
        return null;
    }

    public Card getFieldCard() {
        return fieldCard;
    }

    public void setFieldCard(Card fieldCard) {
        this.fieldCard = fieldCard;
    }

    public ArrayList<Card> getRemainingCards() {
        return remainingCards;
    }

    public ArrayList<Card> getInHandCards() {
        return inHandCards;
    }

    public void addCardToHand() {
        // TODO
    }

    public void addCardToHand(Card card) {
        // TODO
    }

    public void addSpellAndTrapByPosition(int position, SpellAndTrap spellAndTrap, boolean isActive) {
        // TODO
    }

    public void addCardToGraveyard(Card card) {
        // TODO
    }

    public void addMonsterByPosition(int position, Monster monster, boolean isFacedUp, boolean isInAttackPosition) {
        // TODO
    }


}
