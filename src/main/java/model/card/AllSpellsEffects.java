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

    public void fieldCardActivator(SpellAndTrap fieldCard, Game game, PlayerTurn turn) {
        fieldCard.setActive(true);

        switch (fieldCard.cardName) {
            case "Yami" -> yamiEffect(game, turn);
            case "Closed Forest" -> closedForestEffect(game, turn);
            case "Forest" -> forestEffect(game, turn);
            case "Umiiruka" -> umiirukaEffect(game, turn);
        }
    }

    public void equipmentActivator(Board board, SpellAndTrap equipmentCard, Game game, Update gameUpdates, PlayerTurn turn) {
        equipmentCard.setActive(true);

        Monster equippedMonster = board.getMonsterInFieldByPosition(board.getSpellPosition(equipmentCard));
        if (equippedMonster != null) {
            switch (equipmentCard.cardName) {
                case "Sword of dark destruction" -> swordOfDarkEffect(equippedMonster);
                case "Black Pendant" -> blackPendantEffect(equippedMonster);
                case "United We Stand" -> unitedWeStandEffect(board, equippedMonster, equipmentCard, game, gameUpdates, turn);
                case "Magnum Shield" -> magnumShieldEffect(board, equippedMonster, equipmentCard, game, gameUpdates, turn);
            }
        }
    }

    private void magnumShieldEffect(Board board, Monster equippedMonster, SpellAndTrap spell, Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void unitedWeStandEffect(Board board, Monster equippedMonster, SpellAndTrap spell, Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void blackPendantEffect(Monster equippedMonster) {

    }

    private void swordOfDarkEffect(Monster equippedMonster) {
        if (equippedMonster.getModel() == FIEND || equippedMonster.getModel() == SPELL_CASTER) {
            equippedMonster.addToAttackSupplier(400);
            equippedMonster.addToDefensiveSupply(-200);
        }
    }

    private void umiirukaEffect(Game game, PlayerTurn turn) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();

        addFieldEffectsToMonsters(opponentsBoard, AQUA, 500, -400);

        addFieldEffectsToMonsters(playerBoard, AQUA, 500, -400);
    }

    private void forestEffect(Game game, PlayerTurn turn) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();

        addFieldEffectsToMonsters(opponentsBoard, INSECT, 200, 200);
        addFieldEffectsToMonsters(opponentsBoard, BEAST, 200, 200);
        addFieldEffectsToMonsters(opponentsBoard, BEAST_WARRIOR, 200, 200);

        addFieldEffectsToMonsters(playerBoard, INSECT, 200, 200);
        addFieldEffectsToMonsters(playerBoard, BEAST, 200, 200);
        addFieldEffectsToMonsters(playerBoard, BEAST_WARRIOR, 200, 200);
    }

    private void closedForestEffect(Game game, PlayerTurn turn) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();

        for (int i = 0; i < opponentsBoard.getGraveyard().size(); i++) {
            addFieldEffectsToMonsters(opponentsBoard, BEAST, 100, 0);
            addFieldEffectsToMonsters(opponentsBoard, BEAST_WARRIOR, 100, 0);
        }

        for (int i = 0; i < playerBoard.getGraveyard().size(); i++) {
            addFieldEffectsToMonsters(playerBoard, BEAST, 100, 0);
            addFieldEffectsToMonsters(playerBoard, BEAST_WARRIOR, 100, 0);
        }
    }

    private void yamiEffect(Game game, PlayerTurn turn) {
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
