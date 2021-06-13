package control.game;

import model.card.AllMonsterEffects;
import model.card.Card;
import model.card.Monster;
import model.game.Game;

import java.util.ArrayList;
import java.util.HashMap;

import static control.game.UpdateEnum.*;

public class Update {
    private final Game game;
    private final ArrayList<Monster> alreadyAttackedMonsters;
    private boolean haveBeenSetOrSummonACardInPhase; //TODO make this filed false for each turn
    private ArrayList<Monster> alreadyChangedPositionMonsters; //TODO initialize this filed for each turn
    private final HashMap<UpdateEnum, Object> allUpdates;


    public Update(Game game) {
        this.game = game;
        alreadyAttackedMonsters = new ArrayList<>();
        haveBeenSetOrSummonACardInPhase = false;
        alreadyChangedPositionMonsters = new ArrayList<>();
        allUpdates = new HashMap<>();
    }

    public void addMonstersToAttackedMonsters(Monster monster) {
        alreadyAttackedMonsters.add(monster); //TODO reset after each round
        allUpdates.put(ATTACKING_CARD, monster);
    }

    public boolean didMonsterAttack(Monster monster) {
        return alreadyAttackedMonsters.contains(monster);
    }

    public boolean haveBeenSetOrSummonACard() {
        return haveBeenSetOrSummonACardInPhase;
    }

    public void setHaveBeenSetOrSummonACardInPhase(boolean result) {
        haveBeenSetOrSummonACardInPhase = result;
    }

    public void addMonstersToChangedPositionMonsters(Monster monster) {
        alreadyChangedPositionMonsters.add(monster);
    }

    public boolean isCardPositionChangedAlready(Monster monster) {
        return alreadyChangedPositionMonsters.contains(monster);
    }

    public Game getGame() {
        return game;
    }

    public HashMap<UpdateEnum, Object> getAllUpdates() {
        return allUpdates;
    }

    public void flipCard(Card card) {
        allUpdates.put(CARD_FLIPPED, card);
        if (card.getCardName().equals("Man-Eater Bug"))
            AllMonsterEffects.getInstance().ManEaterEffect(game,GameController.getInstance().getTurn());
    }

    public void addMonsterToGraveyard(Card card) {
        allUpdates.put(CARD_DESTROYED, card);
        if (card.getCardName().equals("Yomi Ship"))
            AllMonsterEffects.getInstance().yomiShipEffect(game, GameController.getInstance().getTurn(), GameController.getInstance().getSelectedCard(), this);
    }
}
