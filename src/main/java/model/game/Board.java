package model.game;

import model.user.Deck;
import model.user.User;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

enum AttackingOrDefending {
    ATTACKING,
    DEFENDING
}

enum FaceUpOrFaceDown {
    FACE_UP,
    FACE_DOWN,
}

class CardPositionInField {
    public AttackingOrDefending attackingOrDefending;
    public FaceUpOrFaceDown faceUpOrFaceDown;
    public int position;

    public CardPositionInField(int position, FaceUpOrFaceDown faceUpOrFaceDown, AttackingOrDefending attackingOrDefending){
        this.position = position;
        this.faceUpOrFaceDown = faceUpOrFaceDown;
        this.attackingOrDefending = attackingOrDefending;
    }

    public AttackingOrDefending getAttackingOrDefending() {
        return attackingOrDefending;
    }

    public void setAttackingOrDefending(AttackingOrDefending attackingOrDefending) {
        this.attackingOrDefending = attackingOrDefending;
    }

    public FaceUpOrFaceDown getFaceUpOrFaceDown() {
        return faceUpOrFaceDown;
    }

    public void setFaceUpOrFaceDown(FaceUpOrFaceDown faceUpOrFaceDown) {
        this.faceUpOrFaceDown = faceUpOrFaceDown;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

public class Board {
    private User owner;
    private HashMap<CardPositionInField, Monster> monstersInField; // holding each monster with its position and revealed status
    private HashMap<CardPositionInField, SpellAndTrap> spellAndTrapsInField; // holding all spells and traps in the field
    private ArrayList<Card> graveyard;
    private SpellAndTrap fieldCard;
    private ArrayList<Card> remainingCards;
    private ArrayList<Card> inHandCards;

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

    private void addStartingDeckToTheRemainingCards(Deck deck){
        remainingCards.addAll(deck.getMainDeck());
        Collections.shuffle(remainingCards); // shuffle all cards for start the game
    }

    public void addCardFromRemainingToInHandCards(){
        inHandCards.add(remainingCards.get(0));
        remainingCards.remove(0);
    }

    private void addStartingCardsToInHandCards(){
        for (int i = 0; i < 4; i++) {
            addCardFromRemainingToInHandCards();
        }
    }

    public User getOwner() {
        return owner;
    }

    public HashMap<CardPositionInField, Monster> getMonstersInField() {
        return monstersInField;
    }

    public HashMap<CardPositionInField, SpellAndTrap> getSpellAndTrapsInField() {
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

    public void setFieldCard(SpellAndTrap fieldCard) {
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
