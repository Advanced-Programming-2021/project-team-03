package model.card;

import control.game.Update;
import model.enums.MonsterModels;
import model.game.Board;
import model.game.Game;
import model.game.PlayerTurn;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Optional;

import static model.enums.MonsterModels.*;

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
            case "Terraforming" -> terraformingEffect(game, turn, spell, gameUpdates);
            case "Pot of Greed" -> potOfGreedEffect(game, turn, spell, gameUpdates);
            case "Raigeki" -> raigekiEffect(game, gameUpdates, turn, spell);
            case "Harpie's Feather Duster" -> harpiesFeatherDusterEffect(game, gameUpdates, turn, spell);
            case "Dark Hole" -> darkHoleEffect(game, gameUpdates, turn);
            case "Supply Squad" -> supplySquadEffect(game, gameUpdates, turn);
            case "Twin Twisters" -> twinTwisterEffect(game, gameUpdates, turn);
            case "Mystical space typhoon" -> mysticalSpaceTyphoonEffect(game, turn, gameUpdates);
            case "Ring of defense" -> ringOfDefenseEffect(game, turn, gameUpdates);
        }
    }

    public void fieldCardActivator(SpellAndTrap fieldCard, Game game, Update gameUpdates, PlayerTurn turn) {
        fieldCard.setActive(true);

        switch (fieldCard.cardName) {
            case "Yami" -> yamiEffect(fieldCard, game, gameUpdates, turn);
            case "Closed Forest" -> closedForestEffect(fieldCard, game, gameUpdates, turn);
            case "Forest" -> forestEffect(fieldCard, game, gameUpdates, turn);
            case "Umiiruka" -> umiirukaEffect(fieldCard, game, gameUpdates, turn);
        }
    }

    private void umiirukaEffect(SpellAndTrap fieldCard, Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void forestEffect(SpellAndTrap fieldCard, Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void closedForestEffect(SpellAndTrap fieldCard, Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void yamiEffect(SpellAndTrap fieldCard, Game game, Update gameUpdates, PlayerTurn turn) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();

        addFieldEffectsToMonsters(opponentsBoard, FIEND, 200, 200);
        addFieldEffectsToMonsters(opponentsBoard, SPELL_CASTER, 200, 200);
        addFieldEffectsToMonsters(opponentsBoard, FAIRY, -200, -200);

        addFieldEffectsToMonsters(playerBoard, FIEND, 200, 200);
        addFieldEffectsToMonsters(playerBoard, SPELL_CASTER, 200, 200);
        addFieldEffectsToMonsters(playerBoard, FAIRY, -200, -200);
    }

    private void addFieldEffectsToMonsters(Board board, MonsterModels monsterModels, int attackingSupplier, int defensiveSupplier) {
        for (Monster monster : board.getMonstersInField().values()) {
            if (monster.getModel() == monsterModels) {
                monster.addToAttackSupplier(attackingSupplier);
                monster.addToDefensiveSupply(defensiveSupplier);
            }
        }
    }

    private void ringOfDefenseEffect(Game game, PlayerTurn turn, Update gameUpdates) {
        gameUpdates.getPlayerRingOfDefenseActivator().replace(game.getPlayerByTurn(turn), true);
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
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Monster> allOpponentsMonsters = new ArrayList<>(opponentBoard.getMonstersInField().values());
        for (Monster opponentMonster : allOpponentsMonsters) {
            opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
            opponentBoard.addCardToGraveyard(opponentMonster);
            gameUpdates.addCardToGraveyard(opponentMonster);
        }
        Board attackingPlayerBoard = game.getPlayerByTurn(turn).getBoard();
        ArrayList<Monster> allPlayersMonsters = new ArrayList<>(attackingPlayerBoard.getMonstersInField().values());
        for (Monster playerMonster : allPlayersMonsters) {
            attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(playerMonster), true);
            attackingPlayerBoard.addCardToGraveyard(playerMonster);
            gameUpdates.addCardToGraveyard(playerMonster);
        }
    }

    private void harpiesFeatherDusterEffect(Game game, Update gameUpdates, PlayerTurn turn, SpellAndTrap spell) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<SpellAndTrap> allOpponentsSpellsAndTraps = new ArrayList<>(opponentBoard.getSpellAndTrapsInField().values());
        for (SpellAndTrap opponentSpell : allOpponentsSpellsAndTraps) {
            opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(opponentSpell), false);
            opponentBoard.addCardToGraveyard(opponentSpell);
            gameUpdates.addCardToGraveyard(opponentSpell);
        }

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
    }

    private void raigekiEffect(Game game, Update gameUpdates, PlayerTurn turn, SpellAndTrap spell) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Monster> allOpponentsMonsters = new ArrayList<>(opponentBoard.getMonstersInField().values());
        for (Monster opponentMonster : allOpponentsMonsters) {
            opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
            opponentBoard.addCardToGraveyard(opponentMonster);
            gameUpdates.addCardToGraveyard(opponentMonster);
        }

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
    }

    private void potOfGreedEffect(Game game, PlayerTurn turn, SpellAndTrap spell, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
    }

    private void terraformingEffect(Game game, PlayerTurn turn, SpellAndTrap spell, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().addFieldSpellToHand();

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
    }
}
