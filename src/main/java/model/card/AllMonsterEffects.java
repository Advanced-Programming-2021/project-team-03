package model.card;

import control.MainController;
import control.game.Update;
import control.game.UpdateEnum;
import model.enums.AttackingFormat;
import model.enums.FaceUpSituation;
import model.enums.MonsterEffectTypes;
import model.game.Board;
import model.game.Game;
import model.game.Player;
import model.game.PlayerTurn;
import org.json.JSONObject;

import java.util.HashMap;

import static control.game.UpdateEnum.*;
import static model.enums.FaceUpSituation.*;
import static model.enums.MonsterEffectTypes.*;

public class AllMonsterEffects {
    private static AllMonsterEffects allMonsterEffects;

    private AllMonsterEffects() {
        initialize();
    }

    public static AllMonsterEffects getInstance() {
        if (allMonsterEffects == null)
            allMonsterEffects = new AllMonsterEffects();
        return allMonsterEffects;
    }

    private HashMap<String, IMonsterEffect> effectID;

    public IMonsterEffect getEffectByID(String id) {
        return effectID.get(id);
    }

    private void initialize() { // define each monster effect and add it to the hashmap
        effectID = new HashMap<>();

        /* effect of the command knight card in the game */
        IMonsterEffect commandKnightEffect = new IMonsterEffect() {
            @Override
            public void activateMonsterEffect(Monster self, Update update) {
                for (Monster monster : update.getGame().getCardsInBoard(self).getMonstersInField().values()) { // getting all monsters in friendly board
                    monster.addToAttackSupplier(monster1 -> 400);
                }
            }

            @Override
            public boolean canActivate(Monster self, Update update) {
                return self.getFaceUpSituation().equals(FACE_UP);
            }

            @Override
            public MonsterEffectTypes getMonsterEffectType() {
                return CONTINUOUS;
            }
        };
        effectID.put("41", commandKnightEffect);
    }

    //Yomi Ship effect
    public void yomiShipEffect(Game game, PlayerTurn turn, Card selectedCard, Update gameUpdates) {
        game.getPlayerByTurn(turn).getBoard().removeCardFromField(game.getPlayerByTurn(turn).getBoard().getMonsterPosition((Monster) selectedCard), true);
        game.getPlayerByTurn(turn).getBoard().addCardToGraveyard(selectedCard);
        gameUpdates.addMonsterToGraveyard(selectedCard);

        //TODO System.out.println(); in the view
    }

    //Suijin effect
    public String suijinEffect(Game game, Update gameUpdates, String attackingPlayerUsername, Board attackingPlayerBoard,
                               Monster attackingMonster, Monster opponentMonster, AttackingFormat opponentMonsterFormat,
                               FaceUpSituation opponentMonsterFaceUpSit) {
        StringBuilder answerString = new StringBuilder();
        gameUpdates.getAllUpdates().put(UpdateEnum.SUIJIN_ACTIVATED, opponentMonster);
        switch (opponentMonsterFormat) {
            case ATTACKING -> {
                attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                gameUpdates.addMonsterToGraveyard(attackingMonster);
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
        Monster opponentMonster = defendingPlayer.getBoard().getMonsterByPosition(position);
        StringBuilder answerString = new StringBuilder();
        if (opponentMonster == null)
            answerString.append("No card destroyed.");
        else {
            defendingPlayer.getBoard().removeCardFromField(defendingPlayer.getBoard().getMonsterPosition(opponentMonster), true);
            defendingPlayer.getBoard().addCardToGraveyard(opponentMonster);
            update.addMonsterToGraveyard(opponentMonster);
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
                    gameUpdates.addMonsterToGraveyard(attackingMonster);
                    answerString.append("your monster card is destroyed!\n");
                } else if (attackingDef > 0) {
                    game.getPlayerOpponentByTurn(turn).decreaseHealthByAmount(attackingDef);
                    answerString.append("Your opponent receives ").append(attackingDef).append(" battle damage!\n");
                } else {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addMonsterToGraveyard(attackingMonster);
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
        return 300 * attackingPlayerBoard.getInHandCards().stream().filter(card ->
             ((card instanceof Monster) && ((Monster) card).getFaceUpSituation() == FACE_UP))
                .mapToInt(card -> ((Monster) card).getAttackingPower()).sum();
    }

    public void mirageDragonEffect(Update gameUpdates, PlayerTurn turn, Game game) {
        gameUpdates.getCanPlayerActiveATrap().put(game.getPlayerOpponentByTurn(turn), false);
    }
}
