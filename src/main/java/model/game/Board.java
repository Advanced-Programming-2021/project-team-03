package model.game;

import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;
import model.enums.AttackingFormat;
import model.enums.FaceUpSituation;
import model.enums.SpellAndTrapIcon;
import model.user.Deck;
import model.user.DeckType;
import model.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static model.enums.AttackingFormat.ATTACKING;
import static model.enums.AttackingFormat.DEFENDING;
import static model.enums.FaceUpSituation.FACE_DOWN;
import static model.enums.FaceUpSituation.FACE_UP;

public class Board {
    private final User owner;
    private final HashMap<Integer, Monster> monstersInField; // holding each monster with its position and revealed status
    private final HashMap<Integer, SpellAndTrap> spellAndTrapsInField; // holding all spells and traps in the field
    private final ArrayList<Card> graveyard;
    private SpellAndTrap fieldCard;
    private final ArrayList<Card> remainingCards;
    private final ArrayList<Card> inHandCards;
    private final StringBuilder board;

    public Board(Deck deck, User player) {
        this.owner = player;
        this.board = new StringBuilder();
        this.monstersInField = new HashMap<>();
        this.spellAndTrapsInField = new HashMap<>();
        this.graveyard = new ArrayList<>();
        this.remainingCards = new ArrayList<>();
        addStartingDeckToTheRemainingCards(deck); // add main deck cards to the remaining cards Arraylist
        this.inHandCards = new ArrayList<>();
        addStartingCardsToInHandCards(); // adding 4 card from top of remaining cards to hand to start the game
    }

    private void addStartingDeckToTheRemainingCards(Deck deck) {
        remainingCards.addAll(deck.getDeck(DeckType.MAIN));
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

    public Monster getMonsterInFieldByPosition(int position) {
        return monstersInField.get(position);
    }

    public Card getCardInHandByPosition(int position) {
        return inHandCards.get(position - 1);
    }

    public int getMonsterPosition(Monster monster) {
        for (Integer i : monstersInField.keySet()) {
            if (monstersInField.get(i).equals(monster))
                return i;
        }
        return 0;
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

    public void removeCardFromGraveyard(Card card) {
        graveyard.remove(card);
    }

    public void removeCardFromHand(Card card) {
        inHandCards.remove(card);
    }

    public void removeCardFromField(int position, boolean isMonster) {
        if (isMonster)
            monstersInField.remove(position);
        else
            spellAndTrapsInField.remove(position);
    }

    private int findPositionToSetOrSummonMonsterCard(HashMap<Integer, Monster> monsters) {
        for (int i = 1; i <= 5; i++) {
            if (!monsters.containsKey(i)) return i;
        }
        return 5;
    }

    private int findPositionToSetSpellOrTrapCard(HashMap<Integer, SpellAndTrap> spellAndTraps) {
        for (int i = 1; i <= 5; i++) {
            if (!spellAndTraps.containsKey(i)) return i;
        }
        return 5;
    }

    public void setSpellAndTrapsInField(SpellAndTrap spellAndTrap) {
        int index = findPositionToSetSpellOrTrapCard(spellAndTrapsInField);
        addSpellAndTrapByPosition(index, spellAndTrap);
        spellAndTrap.setActive(false);
        removeCardFromHand((Card) spellAndTrap);
    }

    public void setOrSummonMonsterFromHandToFiled(Card card, String actionType) { //action type will be set or summon
        int index = findPositionToSetOrSummonMonsterCard(monstersInField);
        monstersInField.put(index, (Monster) card);
        if (actionType.equals("Set")) {
            ((Monster) card).setAttackingFormat(DEFENDING);
            ((Monster) card).setFaceUpSituation(FACE_DOWN);
        } else if (actionType.equals("Summon")) {
            ((Monster) card).setAttackingFormat(ATTACKING);
            ((Monster) card).setFaceUpSituation(FACE_UP);
        }
        removeCardFromHand(card);
    }

    //representation of the boards in console
    public String showAsEnemyBoard() {
        board.delete(0, board.length());
        int[] cardOrders = new int[]{4, 2, 1, 3, 5};

        int GY = graveyard.size();
        int DN = remainingCards.size();

        board.append("\t").append("c\t".repeat(inHandCards.size()));
        board.append(DN).append("\n");

        showSpellAndTrapsOnBoard(cardOrders);
        showMonstersOnBoard(cardOrders);

        if (fieldCard != null)
            board.append(GY).append("\t\t\t\t\t\tO");
        else
            board.append(GY).append("\t\t\t\t\t\tE");
        board.append("\n");
        return board.toString();
    }

    @Override
    public String toString() {
        board.delete(0, board.length());
        int[] cardOrders = new int[]{5, 3, 1, 2, 4};

        int GY = graveyard.size();
        int DN = remainingCards.size();
        if (fieldCard != null)
            board.append("O\t\t\t\t\t\t").append(GY).append("\n\t");
        else
            board.append("E\t\t\t\t\t\t").append(GY).append("\n\t");

        showMonstersOnBoard(cardOrders);
        showSpellAndTrapsOnBoard(cardOrders);

        board.append("\t\t\t\t\t\t").append(DN).append("\n");
        board.append("c\t".repeat(inHandCards.size()));
        board.append("\n");
        return board.toString();
    }

    private void showSpellAndTrapsOnBoard(int[] cardOrders) {
        for (int i : cardOrders) {
            if (spellAndTrapsInField.containsKey(i) && spellAndTrapsInField.get(5).isActive())
                board.append("O \t");
            else if (spellAndTrapsInField.containsKey(i))
                board.append("H \t");
            else
                board.append("E \t");
        }
    }

    private void showMonstersOnBoard(int[] cardOrders) {
        AttackingFormat cardAttackingFormat;
        FaceUpSituation cardFaceUpSituation;

        for (int i : cardOrders) {
            if (monstersInField.containsKey(i)) {
                cardAttackingFormat = monstersInField.get(i).getAttackingFormat();
                cardFaceUpSituation = monstersInField.get(i).getFaceUpSituation();
                if (cardAttackingFormat == DEFENDING)
                    board.append("OO\t");
                else if (cardFaceUpSituation == FACE_UP)
                    board.append("DO\t");
                else
                    board.append("DH\t");
            } else
                board.append("E \t");
        }
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
        for (Card card : inHandCards) {
            if (card.getCardIdInTheGame() == cardGameId)
                return true;
        }
        return false;
    }

    public Card getInHandCardByPosition(int cardPosition) {
        return inHandCards.get(cardPosition);
    }

    public void addFieldSpellToHand() {
        for (Card card : remainingCards) {
            if (card instanceof SpellAndTrap) {
                SpellAndTrap spell = (SpellAndTrap) card;
                if (spell.getIcon().equals(SpellAndTrapIcon.FIELD)) {
                    addCardToHand(spell);
                    remainingCards.remove(card);
                    return;
                }
            }
        }
    }
}
