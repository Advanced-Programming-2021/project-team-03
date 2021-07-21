package server.model.card;

import server.control.MainController;
import server.control.game.Update;
import server.model.enums.AttackingFormat;
import server.model.enums.FaceUpSituation;
import server.model.enums.MonsterModels;
import server.model.game.Board;
import server.model.game.Game;
import server.model.game.PlayerTurn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import static server.model.enums.MonsterModels.*;

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

    public void equipmentActivator(Board board, SpellAndTrap equipmentCard) {
        equipmentCard.setActive(true);

        Monster equippedMonster = board.getMonsterInFieldByPosition(board.getSpellPosition(equipmentCard));
        if (equippedMonster != null) {
            switch (equipmentCard.cardName) {
                case "Sword of dark destruction" -> swordOfDarkEffect(equippedMonster);
                case "Black Pendant" -> blackPendantEffect(equippedMonster);
                case "United We Stand" -> unitedWeStandEffect(board, equippedMonster);
                case "Magnum Shield" -> magnumShieldEffect(equippedMonster);
            }
        }
    }

    private void magnumShieldEffect(Monster equippedMonster) {
        if (equippedMonster.getModel() == WARRIOR) {
            if (equippedMonster.getAttackingFormat() == AttackingFormat.ATTACKING) {
                equippedMonster.addToAttackSupplier(equippedMonster.getBaseDefence());
            } else if (equippedMonster.getAttackingFormat() == AttackingFormat.DEFENDING) {
                equippedMonster.addToDefensiveSupply(equippedMonster.getBaseAttack());
            }
        }
        MainController.getInstance().sendPrintRequestToView("Magnum Shield effect activated!\n");
    }

    private void unitedWeStandEffect(Board board, Monster equippedMonster) {
        for (Monster monster : board.getMonstersInField().values()) {
            if (monster.getFaceUpSituation() == FaceUpSituation.FACE_UP) {
                equippedMonster.addToAttackSupplier(800);
                equippedMonster.addToDefensiveSupply(800);
            }
        }
        MainController.getInstance().sendPrintRequestToView("United We Stand effect activated!\n");
    }

    private void blackPendantEffect(Monster equippedMonster) {
        equippedMonster.addToAttackSupplier(500);
    }

    private void swordOfDarkEffect(Monster equippedMonster) {
        if (equippedMonster.getModel() == FIEND || equippedMonster.getModel() == SPELL_CASTER) {
            equippedMonster.addToAttackSupplier(400);
            equippedMonster.addToDefensiveSupply(-200);
        }
        MainController.getInstance().sendPrintRequestToView("Sword Of Dark effect activated!\n");
    }

    private void umiirukaEffect(Game game, PlayerTurn turn) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();

        addFieldEffectsToMonsters(opponentsBoard, AQUA, 500, -400);

        addFieldEffectsToMonsters(playerBoard, AQUA, 500, -400);

        MainController.getInstance().sendPrintRequestToView("Umiiruka effect activated!\n");
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

        MainController.getInstance().sendPrintRequestToView("Forest effect activated!\n");
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

        MainController.getInstance().sendPrintRequestToView("Closed Forest effect activated!\n");
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

        MainController.getInstance().sendPrintRequestToView("Yami effect activated!\n");
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
        MainController.getInstance().sendPrintRequestToView("Ring Of Defense effect activated!\n");
    }

    private void mysticalSpaceTyphoonEffect(Game game, PlayerTurn turn, Update gameUpdates) {
        Board opponentsBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        HashMap<Integer, SpellAndTrap> spells = opponentsBoard.getSpellAndTrapsInField();
        Optional<Integer> randomPosition = spells.keySet().stream().findAny();

        MainController.getInstance().sendPrintRequestToView("Mystical Typhoon effect activated!\n");
        if (randomPosition.isPresent()) {
            int position = randomPosition.get();
            SpellAndTrap spell = spells.get(position);

            if (spell != null) {
                opponentsBoard.removeCardFromField(position, false);
                opponentsBoard.addCardToGraveyard(spell);
                gameUpdates.addCardToGraveyard(spell);
                MainController.getInstance().sendPrintRequestToView(spell.getCardName() + " destroyed and moved to graveyard!\n");
            }
        }
    }

    private void twinTwisterEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board attackingPlayerBoard = game.getPlayerByTurn(turn).getBoard();
        MainController.getInstance().sendPrintRequestToView("Twin twist effect activated!\n");
        Random random = new Random();
        int randomIndex = random.nextInt(attackingPlayerBoard.getInHandCards().size());
        Card sacrificedCard = attackingPlayerBoard.getInHandCards().get(randomIndex);
        attackingPlayerBoard.removeCardFromHand(sacrificedCard);
        attackingPlayerBoard.addCardToGraveyard(sacrificedCard);
        gameUpdates.addCardToGraveyard(sacrificedCard);
        MainController.getInstance().sendPrintRequestToView(sacrificedCard.getCardName() + " destroyed and moved to graveyard!\n");
        mysticalSpaceTyphoonEffect(game, turn, gameUpdates);
        mysticalSpaceTyphoonEffect(game, turn, gameUpdates);
    }

    private void supplySquadEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        gameUpdates.setSupplySquadForPlayer(game.getPlayerByTurn(turn));
        MainController.getInstance().sendPrintRequestToView("Supply Squad effect activated!\n");
    }

    private void darkHoleEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        MainController.getInstance().sendPrintRequestToView("Dark Hole effect activated!\n");
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Monster> allOpponentsMonsters = new ArrayList<>(opponentBoard.getMonstersInField().values());
        for (Monster opponentMonster : allOpponentsMonsters) {
            opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
            opponentBoard.addCardToGraveyard(opponentMonster);
            gameUpdates.addCardToGraveyard(opponentMonster);
            MainController.getInstance().sendPrintRequestToView(opponentMonster.getCardName() + " destroyed and moved to graveyard!\n");
        }
        Board attackingPlayerBoard = game.getPlayerByTurn(turn).getBoard();
        ArrayList<Monster> allPlayersMonsters = new ArrayList<>(attackingPlayerBoard.getMonstersInField().values());
        for (Monster playerMonster : allPlayersMonsters) {
            attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(playerMonster), true);
            attackingPlayerBoard.addCardToGraveyard(playerMonster);
            gameUpdates.addCardToGraveyard(playerMonster);
            MainController.getInstance().sendPrintRequestToView(playerMonster.getCardName() + " destroyed and moved to graveyard!\n");
        }
    }

    private void harpiesFeatherDusterEffect(Game game, Update gameUpdates, PlayerTurn turn, SpellAndTrap spell) {
        MainController.getInstance().sendPrintRequestToView("Swords of Revealing Light effect activated!\n");
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<SpellAndTrap> allOpponentsSpellsAndTraps = new ArrayList<>(opponentBoard.getSpellAndTrapsInField().values());
        for (SpellAndTrap opponentSpell : allOpponentsSpellsAndTraps) {
            opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(opponentSpell), false);
            opponentBoard.addCardToGraveyard(opponentSpell);
            gameUpdates.addCardToGraveyard(opponentSpell);
            MainController.getInstance().sendPrintRequestToView(opponentSpell.getCardName() + " destroyed and moved to graveyard!\n");
        }

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
        MainController.getInstance().sendPrintRequestToView(spell.getCardName() + " destroyed and moved to graveyard!\n");
    }

    private void raigekiEffect(Game game, Update gameUpdates, PlayerTurn turn, SpellAndTrap spell) {
        MainController.getInstance().sendPrintRequestToView("Raigeki effect activated!\n");
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Monster> allOpponentsMonsters = new ArrayList<>(opponentBoard.getMonstersInField().values());
        for (Monster opponentMonster : allOpponentsMonsters) {
            opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
            opponentBoard.addCardToGraveyard(opponentMonster);
            gameUpdates.addCardToGraveyard(opponentMonster);
            MainController.getInstance().sendPrintRequestToView(opponentMonster.getCardName() + " destroyed and moved to graveyard!\n");
        }

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
        MainController.getInstance().sendPrintRequestToView(spell.getCardName() + " destroyed and moved to graveyard!\n");
    }

    private void potOfGreedEffect(Game game, PlayerTurn turn, SpellAndTrap spell, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        MainController.getInstance().sendPrintRequestToView("Pot of Greed effect activated!\n");

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
        MainController.getInstance().sendPrintRequestToView(spell.getCardName() + " destroyed and moved to graveyard!\n");
    }

    private void terraformingEffect(Game game, PlayerTurn turn, SpellAndTrap spell, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().addFieldSpellToHand();
        MainController.getInstance().sendPrintRequestToView("Terraforming effect activated!\n");

        //remove the card from the board
        Board playerBoard = game.getPlayerByTurn(turn).getBoard();
        playerBoard.removeCardFromField(playerBoard.getSpellPosition(spell), false);
        playerBoard.addCardToGraveyard(spell);
        gameUpdates.addCardToGraveyard(spell);
        MainController.getInstance().sendPrintRequestToView(spell.getCardName() + " destroyed and moved to graveyard!\n");
    }
}
