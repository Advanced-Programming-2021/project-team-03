package model.card;

import control.MainController;
import control.game.Update;
import model.game.Board;
import model.game.Game;
import model.game.PlayerTurn;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Optional;

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
        for (PlayerTurn playerTurn : PlayerTurn.values()) {
            SpellAndTrap spellAbsorption = game.getPlayerByTurn(playerTurn).getBoard().getSpellInField("Spell Absorption");
            if (spellAbsorption != null && spellAbsorption.isActive()) {
                game.getPlayerByTurn(playerTurn).decreaseHealthByAmount(-500);
            }
        }

        switch (spell.cardName) {
            case "Terraforming" -> terraformingEffect(game, turn);
            case "Pot of Greed" -> potOfGreedEffect(game, turn);
            case "Raigeki" -> raigekiEffect(game, gameUpdates, turn);
            case "Harpie's Feather Duster" -> harpiesFeatherDusterEffect(game, gameUpdates, turn);
            case "Dark Hole" -> darkHoleEffect(game, gameUpdates, turn);
            case "Supply Squad" -> supplySquadEffect(game, gameUpdates, turn);
            case "Twin Twisters" -> twinTwisterEffect(game, gameUpdates, turn);
            case "Mystical space typhoon" -> mysticalSpaceTyphoonEffect(game, turn, gameUpdates);
            case "Ring of defense" -> ringOfDefenceEffect(game, turn, gameUpdates);
        }
    }

    private void ringOfDefenceEffect(Game game, PlayerTurn turn, Update gameUpdates) {
        gameUpdates.getPlayerRingOfDefenceActivator().replace(game.getPlayerByTurn(turn), true);
    }

    private void mysticalSpaceTyphoonEffect(Game game, PlayerTurn turn, Update gameUpdates) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        HashMap<Integer, SpellAndTrap> spells = opponentsBoard.getSpellAndTrapsInField();

        Optional<Integer> randomPosition = spells.keySet().stream().findAny();
        if (randomPosition.isPresent()) {
            int position = randomPosition.get();
            SpellAndTrap spell = spells.get(position);

            if (spell != null) {
                opponentsBoard.removeCardFromField(position, false);
                opponentsBoard.addCardToGraveyard(spell);
                gameUpdates.addCardToGraveyard(spell);
            }
        }
    }

    private void twinTwisterEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board attackingPlayerBoard = game.getPlayerByTurn(turn).getBoard();
        Random random = new Random();
        int randomIndex = random.nextInt(attackingPlayerBoard.getInHandCards().size());
        Card sacrificedCard = attackingPlayerBoard.getInHandCards().get(randomIndex);
        attackingPlayerBoard.removeCardFromHand(sacrificedCard);
        attackingPlayerBoard.addCardToGraveyard(sacrificedCard);
        gameUpdates.addCardToGraveyard(sacrificedCard);
        mysticalSpaceTyphoonEffect(game, turn, gameUpdates);
        mysticalSpaceTyphoonEffect(game, turn, gameUpdates);
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
            gameUpdates.addCardToGraveyard(playerMonster);
        }
    }

    private void harpiesFeatherDusterEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<SpellAndTrap> allOpponentsSpellsAndTraps = new ArrayList<>(opponentBoard.getSpellAndTrapsInField().values());
        for (SpellAndTrap opponentSpell : allOpponentsSpellsAndTraps) {
            opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(opponentSpell), true);
            opponentBoard.addCardToGraveyard(opponentSpell);
            gameUpdates.addCardToGraveyard(opponentSpell);
        }
    }

    private void raigekiEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Monster> allOpponentsMonsters = new ArrayList<>(opponentBoard.getMonstersInField().values());
        for (Monster opponentMonster : allOpponentsMonsters) {
            opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
            opponentBoard.addCardToGraveyard(opponentMonster);
            gameUpdates.addCardToGraveyard(opponentMonster);
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
