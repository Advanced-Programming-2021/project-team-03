package control;

import model.card.Monster;
import model.game.Game;

import java.util.ArrayList;

public class Update {
    private final Game game;
    private final ArrayList<Monster> alreadyAttackedMonsters;

    public Update(Game game) {
        this.game = game;
        alreadyAttackedMonsters = new ArrayList<>();
    }

    public void addMonstersToAttackedMonsters(Monster monster) {
        alreadyAttackedMonsters.add(monster);
    }

    public boolean didMonsterAttack(Monster monster) {
        return alreadyAttackedMonsters.contains(monster);
    }
}
