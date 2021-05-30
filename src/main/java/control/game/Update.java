package control.game;

import model.card.Card;
import model.card.Monster;
import model.game.Game;
import model.game.Player;

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

    public boolean haveBeenSetOrSummonACard(Player player){
        //TODO
        return false;
    }

    public boolean isCardPositionChangedAlready(Card card){
        //TODO
        return false;
    }
}
