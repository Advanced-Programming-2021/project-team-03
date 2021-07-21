package server.model.card;

import org.json.JSONObject;
import server.control.MainController;
import server.control.game.GamePhases;
import server.control.game.Update;
import server.model.enums.AttackingFormat;
import server.model.enums.FaceUpSituation;
import server.model.enums.TrapNames;
import server.model.game.Board;
import server.model.game.Game;
import server.model.game.Player;
import server.model.game.PlayerTurn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllTrapsEffects {
    private static AllTrapsEffects instance;

    private AllTrapsEffects() {
    }

    public static AllTrapsEffects getInstance() {
        if (instance == null) instance = new AllTrapsEffects();
        return instance;
    }

    private SpellAndTrap getTheDesiredTrapFormBoard(Board board, TrapNames trapNames) {
        HashMap<Integer, SpellAndTrap> spellAndTrapsInField = board.getSpellAndTrapsInField();
        for (Map.Entry<Integer, SpellAndTrap> entry : spellAndTrapsInField.entrySet()) {
            SpellAndTrap spellAndTrap = entry.getValue();
            if (spellAndTrap.getCardName().equals(trapNames.getName())) return spellAndTrap;
        }
        return null;
    }

    private boolean doesTheOpponentHaveTheDesiredTrap(Game game, PlayerTurn turn, TrapNames trapName) {
        Board board = game.getPlayerOpponentByTurn(turn).getBoard();
        return getTheDesiredTrapFormBoard(board, trapName) != null;
    }

    public boolean canTrapHoleActivate(Card selectedCard, Game game, PlayerTurn playerTurn, TrapNames trapName) {
        if (selectedCard instanceof Monster) {
            Monster monster = (Monster) selectedCard;
            if (monster.getAttackingPower() >= 1000) {
                return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                        && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
            }
        }
        return false;
    }

    public void trapHoleEffect(Card selectedCard, Game game, Update gameUpdates, PlayerTurn turn) {
        Monster monster = (Monster) selectedCard;
        Board board = game.getPlayerByTurn(turn).getBoard();
        board.getMonsterPosition(monster);
        board.addCardToGraveyard(monster);
        if (monster.getFaceUpSituation() == FaceUpSituation.FACE_UP) gameUpdates.addCardToGraveyard(monster);
        board.removeCardFromField(board.getMonsterPosition(monster), true);
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        SpellAndTrap trap = getTheDesiredTrapFormBoard(opponentBoard, TrapNames.TRAP_HOLE);
        trap.setActive(true);
        opponentBoard.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(trap), false);
    }

    public boolean canTorrentialTributeActivate(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void torrentialTributeEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board board = game.getPlayerByTurn(turn).getBoard();
        HashMap<Integer, Monster> monstersInField = board.getMonstersInField();
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            int position = entry.getKey();
            Monster monster = entry.getValue();
            board.addCardToGraveyard(monster);
            if (monster.getFaceUpSituation() == FaceUpSituation.FACE_UP) gameUpdates.addCardToGraveyard(monster);
            board.removeCardFromField(position, true);
        }
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        HashMap<Integer, Monster> monstersInOpponentField = opponentBoard.getMonstersInField();
        for (Map.Entry<Integer, Monster> entry : monstersInOpponentField.entrySet()) {
            int position = entry.getKey();
            Monster monster = entry.getValue();
            opponentBoard.addCardToGraveyard(monster);
            if (monster.getFaceUpSituation() == FaceUpSituation.FACE_UP) gameUpdates.addCardToGraveyard(monster);
            opponentBoard.removeCardFromField(position, true);
        }
        SpellAndTrap trap = getTheDesiredTrapFormBoard(opponentBoard, TrapNames.TORRENTIAL_TRIBUTE);
        trap.setActive(true);
        opponentBoard.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(trap), false);
    }

    public boolean canMagicCylinderActivate(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void magicCylinderEffect(Card attackingCard, Game game, Update gameUpdates, PlayerTurn turn) {
        Monster attackingMonster = (Monster) attackingCard;
        int attackPower = attackingMonster.getAttackingPower();
        Board board = game.getPlayerByTurn(turn).getBoard();
        board.addCardToGraveyard(attackingMonster);
        if (attackingMonster.getFaceUpSituation() == FaceUpSituation.FACE_UP)
            gameUpdates.addCardToGraveyard(attackingMonster);
        board.removeCardFromField(board.getMonsterPosition(attackingMonster), true);
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();

        Player player = game.getPlayerByTurn(turn);
        player.decreaseHealthByAmount(attackPower);

        SpellAndTrap trap = getTheDesiredTrapFormBoard(opponentBoard, TrapNames.MAGIC_CYLINDER);
        trap.setActive(true);
        opponentBoard.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(trap), false);
    }

    public boolean canMirrorForceActivate(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void mirrorForceEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Board board = game.getPlayerByTurn(turn).getBoard();
        HashMap<Integer, Monster> monstersInField = board.getMonstersInField();
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            int position = entry.getKey();
            Monster monster = entry.getValue();
            if (monster.getAttackingFormat() == AttackingFormat.ATTACKING) {
                board.addCardToGraveyard(monster);
                if (monster.getFaceUpSituation() == FaceUpSituation.FACE_UP) gameUpdates.addCardToGraveyard(monster);
                board.removeCardFromField(position, true);
            }
        }
    }

    public boolean canNegateAttackActivate(Game game, PlayerTurn playerTurn, TrapNames trapName, GamePhases currentPhase) {
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && currentPhase == GamePhases.BATTLE
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void negateAttackEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        MainController.getInstance().sendPrintRequestToView("Negate Attack trap activated and battle phase will be finish");
    }

    public boolean canTimeSealActivate(GamePhases currentPhase, Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && currentPhase == GamePhases.DRAW
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void timeSealEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        gameUpdates.setCanPlayerDrawACard(false);
    }

    public boolean canMagicJammerActivate(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        Board board = game.getPlayerOpponentByTurn(playerTurn).getBoard();
        if (board.getInHandCards().size() < 1) return false;
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void magicJammerEffect(Card selectedSpell, Game game, Update gameUpdates, PlayerTurn turn) {
        //remove spell card
        Board board = game.getPlayerByTurn(turn).getBoard();
        SpellAndTrap activatedSpell = (SpellAndTrap) selectedSpell;
        board.addCardToGraveyard(activatedSpell);
        gameUpdates.addCardToGraveyard(activatedSpell);
        board.removeCardFromField(board.getSpellPosition(activatedSpell), false);

        //remove card from hand
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Card> inHandCards = opponentBoard.getInHandCards();
        Card card = inHandCards.get(0);
        opponentBoard.addCardToGraveyard(card);
        opponentBoard.removeCardFromHand(card);

        //remove trap
        SpellAndTrap trap = getTheDesiredTrapFormBoard(opponentBoard, TrapNames.MAGIC_JAMMER);
        trap.setActive(true);
        opponentBoard.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(trap), false);
    }

    public boolean canCallOfTheHauntedActivate(GamePhases currentPhase, Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesThePlayerHaveTheDesiredTrap(game, playerTurn, trapName)
                && (currentPhase == GamePhases.FIRST_MAIN || currentPhase == GamePhases.SECOND_MAIN)
                && getThePlayerLastDeletedMonsterInGraveYard(game, playerTurn) != null
                && game.getPlayerByTurn(playerTurn).getBoard().getMonstersInField().size() < 5
                && doesTheUserWantToEnableTheTrapInOwnTurn(game, playerTurn, trapName);
    }

    private boolean doesThePlayerHaveTheDesiredTrap(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        Board board = game.getPlayerByTurn(playerTurn).getBoard();
        return getTheDesiredTrapFormBoard(board, trapName) != null;
    }

    private Monster getThePlayerLastDeletedMonsterInGraveYard(Game game, PlayerTurn playerTurn) {
        Board board = game.getPlayerByTurn(playerTurn).getBoard();
        ArrayList<Card> graveyard = board.getGraveyard();
        for (Card card : graveyard) {
            if (card instanceof Monster) {
                Monster monster = (Monster) card;
                return monster;
            }
        }
        return null;
    }

    public void callOfTheHauntedEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        Monster monster = getThePlayerLastDeletedMonsterInGraveYard(game, turn);
        Board board = game.getPlayerByTurn(turn).getBoard();
        board.addMonsterFromGraveYardToFiled(monster);

        SpellAndTrap trap = getTheDesiredTrapFormBoard(board, TrapNames.CALL_OF_THE_HAUNTED);
        trap.setActive(true);
        board.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        board.removeCardFromField(board.getSpellPosition(trap), false);
    }

    public boolean canSolemnWarningActivate(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesTheOpponentHaveTheDesiredTrap(game, playerTurn, trapName)
                && game.getPlayerByTurn(playerTurn).getHealth() > 2000
                && doesTheUserWantToEnableTheTrap(game, playerTurn, trapName);
    }

    public void solemnWarningEffect(Card selectedMonster, Game game, Update gameUpdates, PlayerTurn turn) {
        Player opponentPlayer = game.getPlayerOpponentByTurn(turn);
        Monster monster = (Monster) selectedMonster;
        opponentPlayer.decreaseHealthByAmount(2000);

        Board board = game.getPlayerByTurn(turn).getBoard();
        board.addCardToGraveyard(monster);
        if (monster.getFaceUpSituation() == FaceUpSituation.FACE_UP) gameUpdates.addCardToGraveyard(monster);
        board.removeCardFromField(board.getMonsterPosition(monster), true);

        Board opponentBoard = opponentPlayer.getBoard();
        SpellAndTrap trap = getTheDesiredTrapFormBoard(opponentBoard, TrapNames.SOLEMN_WARNING);
        trap.setActive(true);
        opponentBoard.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(trap), false);
    }

    public boolean canMindCrushActivate(GamePhases currentPhase, Game game, PlayerTurn playerTurn, TrapNames trapName) {
        return doesThePlayerHaveTheDesiredTrap(game, playerTurn, trapName)
                && (currentPhase == GamePhases.FIRST_MAIN || currentPhase == GamePhases.SECOND_MAIN)
                && game.getPlayerByTurn(playerTurn).getBoard().getInHandCards().size() > 0
                && doesTheUserWantToEnableTheTrapInOwnTurn(game, playerTurn, trapName);
    }

    public void mindCrushEffect(Game game, Update gameUpdates, PlayerTurn turn) {
        JSONObject messageToSendToView = new JSONObject();
        messageToSendToView.put("Type", "Get one card name");
        JSONObject value = new JSONObject();
        messageToSendToView.put("Value", value);
        JSONObject answer = new JSONObject(MainController.getInstance().sendRequestToView(messageToSendToView));
        String cardName = answer.getString("Name");

        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        ArrayList<Card> opponentInHandCards = opponentBoard.getInHandCards();
        int numberOfRemovedCards = 0;
        for (int i = 0; i < opponentInHandCards.size(); i++) {
            Card card = opponentInHandCards.get(0);
            if (card.getCardName().equals(cardName)) {
                opponentBoard.addCardToGraveyard(card);
                opponentInHandCards.remove(card);
                numberOfRemovedCards++;
                i--;
            }
        }

        Board board = game.getPlayerByTurn(turn).getBoard();
        ArrayList<Card> inHandCards = board.getInHandCards();
        if (numberOfRemovedCards == 0) {
            int randomIndex = (int) Math.floor(Math.random() * (inHandCards.size()));
            try {
                Card removeCard = inHandCards.get(randomIndex);
                board.addCardToGraveyard(removeCard);
                inHandCards.remove(removeCard);
                MainController.getInstance().sendPrintRequestToView("1 of your in hand cards was removed");
            } catch (Exception ignored) {
            }
        } else {
            MainController.getInstance().sendPrintRequestToView(numberOfRemovedCards + " of your in hand cards was removed");
        }

        SpellAndTrap trap = getTheDesiredTrapFormBoard(board, TrapNames.MIND_CRUSH);
        trap.setActive(true);
        board.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        board.removeCardFromField(board.getSpellPosition(trap), false);
    }

    public boolean doesTheUserWantToEnableTheTrap(Game game, PlayerTurn turn, TrapNames trapName) {
        JSONObject messageToSendToView = new JSONObject();
        messageToSendToView.put("Type", "Trap activate request");
        JSONObject value = new JSONObject();
        StringBuilder firstMessage = new StringBuilder();
        firstMessage.append("now it will be")
                .append(game.getPlayerOpponentByTurn(turn).getUser().getUsername())
                .append("’s turn\n")
                .append(game.showGameBoards())
                .append("\n")
                .append("do you want to activate your ")
                .append(trapName.getName())
                .append("?");
        value.put("First message", firstMessage);
        StringBuilder secondMessage = new StringBuilder();
        secondMessage.append("now it will be")
                .append(game.getPlayerByTurn(turn).getUser().getUsername())
                .append("’s turn\n")
                .append(game.showGameBoards())
                .append("\n");
        value.put("Second message", secondMessage);
        messageToSendToView.put("Value", value);
        JSONObject answer = new JSONObject(MainController.getInstance().sendRequestToView(messageToSendToView));
        String type = answer.getString("Type");
        return type.equals("Yes");
    }

    private boolean doesTheUserWantToEnableTheTrapInOwnTurn(Game game, PlayerTurn playerTurn, TrapNames trapName) {
        JSONObject messageToSendToView = new JSONObject();
        messageToSendToView.put("Type", "Trap activate request");
        JSONObject value = new JSONObject();
        StringBuilder firstMessage = new StringBuilder();
        firstMessage.append("You can Active your ").append(trapName.getName()).append(" trap in your turn.");
        value.put("First message", firstMessage);
        StringBuilder secondMessage = new StringBuilder();
        secondMessage.append("Trap not activated");
        value.put("Second message", secondMessage);
        messageToSendToView.put("Value", value);
        JSONObject answer = new JSONObject(MainController.getInstance().sendRequestToView(messageToSendToView));
        String type = answer.getString("Type");
        return type.equals("Yes");
    }
}
