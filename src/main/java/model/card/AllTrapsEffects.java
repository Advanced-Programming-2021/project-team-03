package model.card;

import control.MainController;
import control.game.Update;
import model.enums.FaceUpSituation;
import model.enums.TrapNames;
import model.game.Board;
import model.game.Game;
import model.game.PlayerTurn;
import org.json.JSONObject;

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
        opponentBoard.addCardToGraveyard(trap);
        gameUpdates.addCardToGraveyard(trap);
        opponentBoard.removeCardFromField(opponentBoard.getSpellPosition(trap), false);
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
}
