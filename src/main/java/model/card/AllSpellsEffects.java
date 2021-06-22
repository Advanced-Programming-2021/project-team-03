package model.card;

import control.game.Update;
import model.game.Board;
import model.game.Game;
import model.game.PlayerTurn;

import java.util.ArrayList;

public class AllSpellsEffects {
    private static AllSpellsEffects allSpellsEffects;

    private AllSpellsEffects() {
    }

    public static AllSpellsEffects getInstance() {
        if (allSpellsEffects == null)
            allSpellsEffects = new AllSpellsEffects();
        return allSpellsEffects;
    }

    public void cardActivator(SpellAndTrap spell, Game game, Update gameUpdates, PlayerTurn turn) {
        spell.setActive(true);

        // Spell Absorption effect
        SpellAndTrap spellAbsorption = game.getPlayerByTurn(turn).getBoard().getSpellInField("Spell Absorption");
        if (spellAbsorption != null && spellAbsorption.isActive()) {
            game.getPlayerByTurn(turn).decreaseHealthByAmount(-500);
        }

        switch (spell.cardName) {
            case "Terraforming" -> terraformingEffect(game, turn);
            case "Pot of Greed" -> potOfGreedEffect(game, turn);
            case "Raigeki" -> raigekiEffect(game, gameUpdates, turn);
            case "Harpie's Feather Duster" -> harpiesFeatherDusterEffect(game, gameUpdates, turn);
            case "Dark Hole" -> darkHoleEffect(game, gameUpdates, turn);
            case "Supply Squad" -> supplySquadEffect(game, gameUpdates, turn);
        }
    }

    private void supplySquadEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        gameUpdates.setSupplySquadForPlayer(game.getPlayerByTurn(turn));
    }

    private void darkHoleEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        raigekiEffect(game, gameUpdates, turn);
        Board attackingPlayerBoard = game.getPlayerByTurn(turn).getBoard();
        ArrayList<Monster> allPlayersMonsters = new ArrayList<>(attackingPlayerBoard.getMonstersInField().values());
        for (Monster playerMonster : allPlayersMonsters) {
            attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(playerMonster), true);
            attackingPlayerBoard.addCardToGraveyard(playerMonster);
            gameUpdates.addMonsterToGraveyard(playerMonster);
        }
    }

    private void harpiesFeatherDusterEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<SpellAndTrap> allOpponentsSpellsAndTraps = new ArrayList<>(opponentBoard.getSpellAndTrapsInField().values());
        for (SpellAndTrap opponentSpell : allOpponentsSpellsAndTraps) {
            opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(opponentSpell), true);
            opponentBoard.addCardToGraveyard(opponentSpell);
            gameUpdates.addMonsterToGraveyard(opponentSpell);
        }
    }

    private void raigekiEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Monster> allOpponentsMonsters = new ArrayList<>(opponentBoard.getMonstersInField().values());
        for (Monster opponentMonster : allOpponentsMonsters) {
            opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
            opponentBoard.addCardToGraveyard(opponentMonster);
            gameUpdates.addMonsterToGraveyard(opponentMonster);
        }
    }

    private void potOfGreedEffect(Game game, PlayerTurn turn) {
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
    }

    private void terraformingEffect(Game game, PlayerTurn turn) {
        game.getPlayerByTurn(turn).getBoard().addFieldSpellToHand();
    }
}
