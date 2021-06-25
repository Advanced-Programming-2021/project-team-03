package model.card;

import control.MainController;
import control.game.Update;
import control.game.UpdateEnum;
import model.enums.AttackingFormat;
import model.enums.FaceUpSituation;
import model.game.Board;
import model.game.Game;
import model.game.Player;
import model.game.PlayerTurn;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static control.game.UpdateEnum.*;
import static model.enums.FaceUpSituation.*;

public class AllMonsterEffects {
    private static AllMonsterEffects allMonsterEffects;

    private AllMonsterEffects() {
    }

    public static AllMonsterEffects getInstance() {
        if (allMonsterEffects == null)
            allMonsterEffects = new AllMonsterEffects();
        return allMonsterEffects;
    }

    public void commandKnightEffect(Game game, PlayerTurn turn) {
        HashMap<Integer, Monster> monstersInField = game.getPlayerByTurn(turn).getBoard().getMonstersInField();
        for (Map.Entry<Integer, Monster> monsterEntry : monstersInField.entrySet()) {
            Monster monster = monsterEntry.getValue();
            monster.addToAttackSupplier(400);
        }
    }


    //Yomi Ship effect
    public void yomiShipEffect(Game game, PlayerTurn turn, Card selectedCard, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().removeCardFromField(game.getPlayerByTurn(turn).getBoard().getMonsterPosition((Monster) selectedCard), true);
        game.getPlayerByTurn(turn).getBoard().addCardToGraveyard(selectedCard);
        gameUpdates.addCardToGraveyard(selectedCard);

        //TODO System.out.println(); in the view
    }

    //Suijin effect
    public String suijinEffect(Game game, Update gameUpdates, String attackingPlayerUsername, Board attackingPlayerBoard,
                               Monster attackingMonster, Monster opponentMonster, AttackingFormat opponentMonsterFormat,
                               FaceUpSituation opponentMonsterFaceUpSit) {
        StringBuilder answerString = new StringBuilder();
        gameUpdates.getAllUpdates().put(UpdateEnum.SUIJIN_ACTIVATED, opponentMonster);
        answerString.append("\nSuijin activated\n");
        switch (opponentMonsterFormat) {
            case ATTACKING -> {
                attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                gameUpdates.addCardToGraveyard(attackingMonster);
                game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(opponentMonster.getAttackingPower());
                answerString.append("Your monster card is destroyed and you received ").append(opponentMonster.getAttackingPower()).append(" battle damage");
                return answerString.toString();
            }
            case DEFENDING -> {
                if (opponentMonsterFaceUpSit == FaceUpSituation.FACE_DOWN) {
                    opponentMonster.setFaceUpSituation(FaceUpSituation.FACE_UP);
                    answerString.append("opponent’s monster card was ").append(opponentMonster.getCardName());
                }
                game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(opponentMonster.getDefensivePower());
                answerString.append("no card is destroyed and you received ").append(opponentMonster.getDefensivePower()).append(" battle damage!");
                return answerString.toString();
            }
        }
        return "Unknown Error";
    }


    //Man-Eater effect
    public void ManEaterEffect(Game game, PlayerTurn turn, Update update) {
        int position;
        try {
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Get one monster number");
            position = Integer.parseInt(MainController.getInstance().sendRequestToView(messageToSendToView));
        } catch (Exception e) {
            return;
        }
        Player defendingPlayer = game.getPlayerOpponentByTurn(turn);
        Monster opponentMonster = defendingPlayer.getBoard().getMonsterInFieldByPosition(position);
        StringBuilder answerString = new StringBuilder();
        if (opponentMonster == null)
            answerString.append("No card destroyed.");
        else {
            defendingPlayer.getBoard().removeCardFromField(defendingPlayer.getBoard().getMonsterPosition(opponentMonster), true);
            defendingPlayer.getBoard().addCardToGraveyard(opponentMonster);
            update.addCardToGraveyard(opponentMonster);
            answerString.append(opponentMonster.getCardName()).append("destroyed!");
        }
        MainController.getInstance().sendPrintRequestToView(answerString.toString());
    }

    //Marshmallon effect
    public String marshmallonEffect(Game game, Monster opponentMonster, FaceUpSituation opponentMonsterFaceUpSit, Player attackingPlayer,
                                    AttackingFormat opponentMonsterFormat, Monster attackingMonster, Board attackingPlayerBoard, Board opponentBoard,
                                    int attackingDef, int defendingDef, Update gameUpdates, PlayerTurn turn) {
        StringBuilder answerString = new StringBuilder();
        answerString.append("Marshmallon effect activated!\n");
        switch (opponentMonsterFormat) {
            case ATTACKING -> {
                if (attackingDef == 0) {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addCardToGraveyard(attackingMonster);
                    answerString.append("your monster card is destroyed!\n");
                } else if (attackingDef > 0) {
                    game.getPlayerOpponentByTurn(turn).decreaseHealthByAmount(attackingDef);
                    answerString.append("Your opponent receives ").append(attackingDef).append(" battle damage!\n");
                } else {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addCardToGraveyard(attackingMonster);
                    attackingPlayer.decreaseHealthByAmount(attackingDef);
                    answerString.append("Your monster card is destroyed and you received ").append(attackingDef).append(" battle damage\n");
                }
            }
            case DEFENDING -> {
                if (opponentMonsterFaceUpSit == FACE_DOWN) {
                    opponentMonster.setFaceUpSituation(FaceUpSituation.FACE_UP);
                    gameUpdates.flipCard(opponentMonster);
                    attackingPlayer.decreaseHealthByAmount(1000);
                    answerString.append("you received 1000 battle damage!\n");
                }
                if (defendingDef == 0) {
                    answerString.append("no card is destroyed!\n");
                } else if (defendingDef < 0) {
                    attackingPlayer.decreaseHealthByAmount(defendingDef);
                    answerString.append("no card is destroyed and you received ").append(defendingDef).append(" battle damage!\n");
                }
            }
        }
        return answerString.toString();
    }

    public boolean canCommandKnightActivate(Monster monster) {
        return monster.getFaceUpSituation().equals(FACE_UP);
    }

    public boolean isSuijinActivatedBefore(Update gameUpdates) {
        for (UpdateEnum updateEnum : gameUpdates.getAllUpdates().keySet()) {
            if (updateEnum.equals(SUIJIN_ACTIVATED)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTexChangerActivatedBefore(Update gameUpdates) {
        for (UpdateEnum updateEnum : gameUpdates.getAllUpdates().keySet()) {
            if (updateEnum.equals(TEXCHANGER_ACTIVATED)) {
                return true;
            }
        }
        return false;
    }

    public int theCalculatorAtkPower(Board attackingPlayerBoard) {
        return 300 * attackingPlayerBoard.getMonstersInField().values().stream()
                .filter(monster -> monster.getFaceUpSituation() == FACE_UP)
                .mapToInt(Monster::getAttackingPower).sum();
    }

    public void mirageDragonEffect(Update gameUpdates, PlayerTurn turn, Game game) {
        gameUpdates.getCanPlayerActivateATrap().replace(game.getPlayerOpponentByTurn(turn), false);
    }

    public void beastKingBarbarosEffect(Update gameUpdates, PlayerTurn turn, Game game) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        HashMap<Integer, Monster> monstersInField = opponentBoard.getMonstersInField();
        HashMap<Integer, SpellAndTrap> spellAndTrapsInField = opponentBoard.getSpellAndTrapsInField();
        SpellAndTrap fieldCard = (SpellAndTrap) opponentBoard.getFieldCard();
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            Monster monster = entry.getValue();
            int position = entry.getKey();
            opponentBoard.addCardToGraveyard(monster);
            opponentBoard.removeCardFromField(position, true);
            if (monster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(monster);
        }
        for (Map.Entry<Integer, SpellAndTrap> entry : spellAndTrapsInField.entrySet()) {
            SpellAndTrap spellAndTrap = entry.getValue();
            int position = entry.getKey();
            opponentBoard.addCardToGraveyard(spellAndTrap);
            opponentBoard.removeCardFromField(position, false);
            if (spellAndTrap.isActive()) gameUpdates.addCardToGraveyard(spellAndTrap);
        }
        opponentBoard.addCardToGraveyard(fieldCard);
        opponentBoard.removeFieldCard(game,turn);
        if (fieldCard.isActive()) gameUpdates.addCardToGraveyard(fieldCard);
    }

}
