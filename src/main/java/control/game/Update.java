package control.game;

import model.card.Card;
import model.card.Monster;
import model.game.Game;
import model.game.Player;

import java.util.ArrayList;

public class Update {
    private final Game game;
    private final ArrayList<Monster> alreadyAttackedMonsters;
    private boolean haveBeenSetOrSummonACardInPhase; //TODO make this filed false for each turn
    private ArrayList<Monster> alreadyChangedPositionMonsters; //TODO initialize this filed for each turn

    public Update(Game game) {
        this.game = game;
        alreadyAttackedMonsters = new ArrayList<>();
        haveBeenSetOrSummonACardInPhase = false;
        alreadyChangedPositionMonsters = new ArrayList<>();
    }

    public void addMonstersToAttackedMonsters(Monster monster) {
        alreadyAttackedMonsters.add(monster);
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
}
