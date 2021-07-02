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
    private boolean haveRitualSpellBeenActivated = false;
    private final ArrayList<Player> roundWinners;
    private final HashMap<Player, Boolean> canPlayerActivateATrap;
    private final HashMap<Player, Boolean> playerRingOfDefenseActivator;
    private final HashMap<Player, Integer> isSupplySquadActivated;
    private boolean canPlayerDrawACard;

    public Update(Game game) {
        this.game = game;
        this.canPlayerDrawACard = true;
        alreadyAttackedMonsters = new ArrayList<>();
        haveBeenSetOrSummonACardInPhase = false;
        alreadyChangedPositionMonsters = new ArrayList<>();
        allUpdates = new HashMap<>();
        roundWinners = new ArrayList<>();
        canPlayerActivateATrap = new HashMap<>();
        canPlayerActivateATrap.put(game.getPlayer1(), true);
        canPlayerActivateATrap.put(game.getPlayer2(), true);
        isSupplySquadActivated = new HashMap<>();
        isSupplySquadActivated.put(game.getPlayer1(), 1);
        isSupplySquadActivated.put(game.getPlayer2(), 1);
        playerRingOfDefenseActivator = new HashMap<>();
        playerRingOfDefenseActivator.put(game.getPlayer1(), false);
        playerRingOfDefenseActivator.put(game.getPlayer2(), false);
    }

    public HashMap<Player, Boolean> getPlayerRingOfDefenseActivator() {
        return playerRingOfDefenseActivator;
    }

    public void addMonstersToAttackedMonsters(Monster monster) {
        alreadyAttackedMonsters.add(monster);
        allUpdates.put(ATTACKING_CARD, monster);
    }

    public HashMap<Player, Boolean> getCanPlayerActivateATrap() {
        return canPlayerActivateATrap;
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

    public void addCardToGraveyard(Card card) {
        allUpdates.put(CARD_DESTROYED, card);
        if (card instanceof Monster)
            GameController.getInstance().removeEquipment((Monster) card);
        if (card.getCardName().equals("Yomi Ship"))
            AllMonsterEffects.getInstance().yomiShipEffect(game, GameController.getInstance().getTurn(), GameController.getInstance().getSelectedCard(), this);
        if (card.getCardName().equals("Mirage Dragon"))
            canPlayerActivateATrap.put(game.getPlayerOpponentByTurn(GameController.getInstance().getTurn()), true);
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
        if (card.getCardName().equals("Ring of defense"))
            playerRingOfDefenseActivator.put(game.getPlayerByTurn(GameController.getInstance().getTurn()), false);
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

    public boolean isCanPlayerDrawACard() {
        return canPlayerDrawACard;
    }

    public void setCanPlayerDrawACard(boolean canPlayerDrawACard) {
        this.canPlayerDrawACard = canPlayerDrawACard;
    }
}
