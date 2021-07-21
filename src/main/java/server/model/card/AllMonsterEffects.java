package server.model.card;

import org.json.JSONObject;
import server.control.MainController;
import server.control.game.Update;
import server.control.game.UpdateEnum;
import server.model.enums.AttackingFormat;
import server.model.enums.FaceUpSituation;
import server.model.game.Board;
import server.model.game.Game;
import server.model.game.Player;
import server.model.game.PlayerTurn;

import java.util.HashMap;
import java.util.Map;

import static server.control.game.UpdateEnum.SUIJIN_ACTIVATED;
import static server.control.game.UpdateEnum.TEXCHANGER_ACTIVATED;
import static server.model.enums.FaceUpSituation.FACE_DOWN;
import static server.model.enums.FaceUpSituation.FACE_UP;

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
        MainController.getInstance().sendPrintRequestToView("Command Knight effect Activated!");
    }


    //Yomi Ship effect
    public void yomiShipEffect(Game game, PlayerTurn turn, Card selectedCard, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().removeCardFromField(game.getPlayerByTurn(turn).getBoard().getMonsterPosition((Monster) selectedCard), true);
        game.getPlayerByTurn(turn).getBoard().addCardToGraveyard(selectedCard);
        gameUpdates.addCardToGraveyard(selectedCard);
        MainController.getInstance().sendPrintRequestToView("Yomi Ship effect activated!\n" + selectedCard.getCardName() + " Destroyed and moved to graveyard!\n");
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
                                    AttackingFormat opponentMonsterFormat, Monster attackingMonster, Board attackingPlayerBoard,
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
        MainController.getInstance().sendPrintRequestToView("Calculator is calculating its attacking power!\n");
        return 300 * attackingPlayerBoard.getMonstersInField().values().stream()
                .filter(monster -> monster.getFaceUpSituation() == FACE_UP)
                .mapToInt(Monster::getAttackingPower).sum();
    }

    public void mirageDragonEffect(Update gameUpdates, PlayerTurn turn, Game game) {
        gameUpdates.getCanPlayerActivateATrap().replace(game.getPlayerOpponentByTurn(turn), false);
        MainController.getInstance().sendPrintRequestToView("Mirage dragon effect activated!\n");
    }

    public void beastKingBarbarosEffect(Update gameUpdates, PlayerTurn turn, Game game) {
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        HashMap<Integer, Monster> monstersInField = opponentBoard.getMonstersInField();
        HashMap<Integer, SpellAndTrap> spellAndTrapsInField = opponentBoard.getSpellAndTrapsInField();
        SpellAndTrap fieldCard = (SpellAndTrap) opponentBoard.getFieldCard();
        MainController.getInstance().sendPrintRequestToView("Beast Barbaros effect activated!\n");
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            Monster monster = entry.getValue();
            int position = entry.getKey();
            opponentBoard.addCardToGraveyard(monster);
            opponentBoard.removeCardFromField(position, true);
            MainController.getInstance().sendPrintRequestToView(monster.getCardName() + " Destroyed and moved to graveyard!\n");
            if (monster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(monster);
        }
        for (Map.Entry<Integer, SpellAndTrap> entry : spellAndTrapsInField.entrySet()) {
            SpellAndTrap spellAndTrap = entry.getValue();
            int position = entry.getKey();
            opponentBoard.addCardToGraveyard(spellAndTrap);
            opponentBoard.removeCardFromField(position, false);
            MainController.getInstance().sendPrintRequestToView(spellAndTrap.getCardName() + " Destroyed and moved to graveyard!\n");
            if (spellAndTrap.isActive()) gameUpdates.addCardToGraveyard(spellAndTrap);
        }
        opponentBoard.addCardToGraveyard(fieldCard);
        opponentBoard.removeFieldCard(game, turn);
        MainController.getInstance().sendPrintRequestToView(fieldCard.getCardName() + " Destroyed and moved to graveyard!\n");
        if (fieldCard.isActive()) gameUpdates.addCardToGraveyard(fieldCard);
    }

}
