package control.game;

import model.card.AllMonsterEffects;
import model.card.Card;
import model.card.Monster;
import model.game.Game;
import model.game.Player;

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
    private final HashMap<Player, Boolean> canPlayerActiveATrap;

    private final HashMap<Player, Integer> isSupplySquadActivated;

    public Update(Game game) {
        this.game = game;
        alreadyAttackedMonsters = new ArrayList<>();
        haveBeenSetOrSummonACardInPhase = false;
        alreadyChangedPositionMonsters = new ArrayList<>();
        allUpdates = new HashMap<>();
        roundWinners = new ArrayList<>();
        canPlayerActiveATrap = new HashMap<>();
        canPlayerActiveATrap.put(game.getPlayer1(), true);
        canPlayerActiveATrap.put(game.getPlayer2(), true);
        isSupplySquadActivated = new HashMap<>();
        isSupplySquadActivated.put(game.getPlayer1(), 1);
        isSupplySquadActivated.put(game.getPlayer2(), 1);
    }

    public void addMonstersToAttackedMonsters(Monster monster) {
        alreadyAttackedMonsters.add(monster);
        allUpdates.put(ATTACKING_CARD, monster);
    }

    public HashMap<Player, Boolean> getCanPlayerActiveATrap() {
        return canPlayerActiveATrap;
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
        if (card.getCardName().equals("Mirage Dragon"))
            AllMonsterEffects.getInstance().mirageDragonEffect(this, GameController.getInstance().getTurn(), game);
    }

    public void addMonsterToGraveyard(Card card) {
        allUpdates.put(CARD_DESTROYED, card);
        if (card.getCardName().equals("Yomi Ship"))
            AllMonsterEffects.getInstance().yomiShipEffect(game, GameController.getInstance().getTurn(), GameController.getInstance().getSelectedCard(), this);
        if (card.getCardName().equals("Mirage Dragon"))
            canPlayerActiveATrap.put(game.getPlayerOpponentByTurn(GameController.getInstance().getTurn()), true);
        if (card.getCardName().equals("Supply Squad") && isSupplySquadActivated.get(game.getPlayer1()) != 1)
            isSupplySquadActivated.replace(game.getPlayer1(), 1);
        if (card.getCardName().equals("Supply Squad") && isSupplySquadActivated.get(game.getPlayer2()) != 1)
            isSupplySquadActivated.replace(game.getPlayer2(), 1);
        if (isSupplySquadActivated.get(game.getPlayer1()) == 2) {
            isSupplySquadActivated.replace(game.getPlayer1(), 3);
            game.getPlayer1().getBoard().addCardFromRemainingToInHandCards();
        }
        if (isSupplySquadActivated.get(game.getPlayer2()) == 2) {
            isSupplySquadActivated.replace(game.getPlayer2(), 3);
            game.getPlayer2().getBoard().addCardFromRemainingToInHandCards();
        }
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
        if (isSupplySquadActivated.get(game.getPlayer1()) == 3) {
            isSupplySquadActivated.replace(game.getPlayer1(), 2);
        }
        if (isSupplySquadActivated.get(game.getPlayer2()) == 3) {
            isSupplySquadActivated.replace(game.getPlayer2(), 2);
        }
    }

    public void setSupplySquadForPlayer(Player playerByTurn) {
        isSupplySquadActivated.replace(playerByTurn, 2);
    }

    public HashMap<Player, Integer> getIsSupplySquadActivated() {
        return isSupplySquadActivated;
    }
}
