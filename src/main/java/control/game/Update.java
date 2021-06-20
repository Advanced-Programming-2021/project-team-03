package control.game;

import model.card.AllMonsterEffects;
import model.card.Card;
import model.card.Monster;
import model.game.Game;
import model.game.Player;
import model.user.User;

import java.util.ArrayList;
import java.util.HashMap;

import static control.game.UpdateEnum.*;

public class Update {
    private final Game game;
    private ArrayList<Monster> alreadyAttackedMonsters;
    private boolean haveBeenSetOrSummonACardInPhase;
    private ArrayList<Monster> alreadyChangedPositionMonsters;
    private final HashMap<UpdateEnum, Object> allUpdates;
    private boolean haveRitualSpellBeenActivated = false; //TODO: make this field true if ritual spell activated.
    private final ArrayList<Player> roundWinners;


    public Update(Game game) {
        this.game = game;
        alreadyAttackedMonsters = new ArrayList<>();
        haveBeenSetOrSummonACardInPhase = false;
        alreadyChangedPositionMonsters = new ArrayList<>();
        allUpdates = new HashMap<>();
        roundWinners = new ArrayList<>();
    }

    public void addMonstersToAttackedMonsters(Monster monster) {
        alreadyAttackedMonsters.add(monster);
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
            AllMonsterEffects.getInstance().ManEaterEffect(game, GameController.getInstance().getTurn(), this);
    }

    public void addMonsterToGraveyard(Card card) {
        allUpdates.put(CARD_DESTROYED, card);
        if (card.getCardName().equals("Yomi Ship"))
            AllMonsterEffects.getInstance().yomiShipEffect(game, GameController.getInstance().getTurn(), GameController.getInstance().getSelectedCard(), this);
    }

    public boolean haveRitualSpellBeenActivated() {
        return haveRitualSpellBeenActivated;
    }

    public void setHaveRitualSpellBeenActivated(boolean haveRitualSpellBeenActivated) {
        this.haveRitualSpellBeenActivated = haveRitualSpellBeenActivated;
    }

    public ArrayList<Player> getRoundWinners() {
        return roundWinners;
    }

    public void playerWins(Player winner) {
        roundWinners.add(winner);
    }

    public boolean isGameOver() {
        Player firstWinner = roundWinners.get(0);
        int count = 0;
        for (Player winner : roundWinners) {
            if (winner.getUser().getUsername().equals(firstWinner.getUser().getUsername()))
                count += 1;
        }
        return count >= 2;
    }

    public Player getWinner() {
        Player firstWinner = roundWinners.get(0);
        int count = 0;
        for (Player winner : roundWinners) {
            if (winner.getUser().getUsername().equals(firstWinner.getUser().getUsername()))
                count += 1;
        }
        if (count >= 2)
            return firstWinner;
        else {
            for (Player winner : roundWinners) {
                if (!winner.getUser().getUsername().equals(firstWinner.getUser().getUsername()))
                    return winner;
            }
        }
        return null;
    }

    public int getWins(Player gameWinner) {
        int count = 0;
        for (Player winner : roundWinners) {
            if (winner.getUser().getUsername().equals(gameWinner.getUser().getUsername()))
                count += 1;
        }
        return count;
    }

    public Player getLooser(Player gameWinner) {
        for (Player looser : roundWinners) {
            if (!looser.getUser().getUsername().equals(gameWinner.getUser().getUsername()))
                return looser;
        }
        return null;
    }

    public void reset() {
        haveBeenSetOrSummonACardInPhase = false;
        alreadyAttackedMonsters = new ArrayList<>();
        alreadyChangedPositionMonsters = new ArrayList<>();
    }
}
