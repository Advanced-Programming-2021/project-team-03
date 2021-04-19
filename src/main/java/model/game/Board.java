package model.game;

import model.user.Deck;
import model.user.User;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.HashMap;

import static model.game.attackingPosition.ATTACKING_POSITION;

enum attackingPosition{
    ATTACKING_POSITION(0),
    DEFENDING_POSITION(1);

    private final int value;

    attackingPosition(int value) {
    this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class Board {
    private User owner;
    private HashMap<Monster, Integer[]> monstersInField; // holding each monster with its position and revealed status
    private HashMap<SpellAndTrap, Integer[]> spellAndTrapsInField; // holding all spells and traps in the field
    private ArrayList<Card> graveyard;
    private Card fieldCard;
    private ArrayList<Card> remainingCards;
    private ArrayList<Card> inHandCards;

    public Board(Deck deck, User player) {
            monstersInField = new HashMap<>();
            /*Integer[] newInt = new Integer[]{0,2,ATTACKING_POSITION.getValue()};

            monstersInField.put(new Monster(),);*/
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
