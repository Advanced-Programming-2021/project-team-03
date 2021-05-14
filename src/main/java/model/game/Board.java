package model.game;

import model.user.Deck;
import model.user.User;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Board {
    private final User owner;
    private final HashMap<Integer, Monster> monstersInField; // holding each monster with its position and revealed status
    private final HashMap<Integer, SpellAndTrap> spellAndTrapsInField; // holding all spells and traps in the field
    private final ArrayList<Card> graveyard;
    private SpellAndTrap fieldCard;
    private final ArrayList<Card> remainingCards;
    private final ArrayList<Card> inHandCards;

    public Board(Deck deck, User player) {
        this.owner = player;
        monstersInField = new HashMap<>();
        spellAndTrapsInField = new HashMap<>();
        graveyard = new ArrayList<>();
        remainingCards = new ArrayList<>();
        addStartingDeckToTheRemainingCards(deck); // add main deck cards to the remaining cards Arraylist
        inHandCards = new ArrayList<>();
        addStartingCardsToInHandCards(); // adding 4 card from top of remaining cards to hand to start the game
    }

    private void addStartingDeckToTheRemainingCards(Deck deck) {
        remainingCards.addAll(deck.getMainDeck());
        Collections.shuffle(remainingCards); // shuffle all cards for start the game
    }

    public void addCardFromRemainingToInHandCards() {
        inHandCards.add(remainingCards.get(0));
        remainingCards.remove(0);
    }

    private void addStartingCardsToInHandCards() {
        for (int i = 0; i < 4; i++) {
            addCardFromRemainingToInHandCards();
        }
    }

    public User getOwner() {
        return owner;
    }

    public HashMap<Integer, Monster> getMonstersInField() {
        return monstersInField;
    }

    public HashMap<Integer, SpellAndTrap> getSpellAndTrapsInField() {
        return spellAndTrapsInField;
    }

    public ArrayList<Card> getGraveyard() {
        return graveyard;
    }

    public Card getFieldCard() {
        return fieldCard;
    }

    public void setFieldCard(SpellAndTrap fieldCard) {
        this.fieldCard = fieldCard;
    }

    public ArrayList<Card> getRemainingCards() {
        return remainingCards;
    }

    public ArrayList<Card> getInHandCards() {
        return inHandCards;
    }

    public Monster getMonsterByPosition(int position) {
        return monstersInField.get(position);
    }

    public Card getSpellAndTrapByPosition(int position) { // returning the card in the given position if needed
        return spellAndTrapsInField.get(position);
    }

    public void addCardToHand(Card card) {
        inHandCards.add(card);
    }

    public void addSpellAndTrapByPosition(int position, SpellAndTrap spellAndTrap) {
        spellAndTrapsInField.put(position, spellAndTrap);
    }

    public void addMonsterByPosition(int position, Monster monster, boolean isFacedUp, boolean isInAttackPosition) {
        monstersInField.put(position, monster);
    }

    public void addCardToGraveyard(Card card) {
        graveyard.add(card);
    }

    public void removeCardFromGraveyard(Card card){
        graveyard.remove(card);
    }

    public void removeCardFromHand(Card card){
        inHandCards.remove(card);
    }

    public void removeCardFromField(int position,boolean isMonster){
        if (isMonster)
            monstersInField.remove(position);
        else
            spellAndTrapsInField.remove(position);
    }

    public boolean doesContainCard(int cardGameId) {
        for (Monster monster : monstersInField.values()) {
            if (monster.getCardIdInTheGame() == cardGameId)
                return true;
        }
        for (SpellAndTrap spellAndTrap : spellAndTrapsInField.values()) {
            if (spellAndTrap.getCardIdInTheGame() == cardGameId)
                return true;
        }
        return false;
    }
}
