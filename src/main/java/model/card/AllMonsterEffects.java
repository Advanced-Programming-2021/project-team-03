package model.card;

import control.MainController;
import control.game.GameController;
import control.game.Update;
import model.enums.AttackingFormat;
import model.enums.FaceUpSituation;
import model.enums.MonsterEffectTypes;
import model.game.Board;
import model.game.Game;
import model.game.PlayerTurn;

import java.util.HashMap;

import static model.enums.FaceUpSituation.FACE_UP;
import static model.enums.MonsterEffectTypes.CONTINUOUS;

public class AllMonsterEffects {
    private static AllMonsterEffects allMonsterEffects;

    private AllMonsterEffects() {
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
    public String attackSuijin(Game game, Update gameUpdates, String attackingPlayerUsername, Board attackingPlayerBoard, Monster attackingMonster, Monster opponentMonster, AttackingFormat opponentMonsterFormat, FaceUpSituation opponentMonsterFaceUpSit) {
        StringBuilder answerString = new StringBuilder();
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


    public void ManEaterEffect(Game game, PlayerTurn turn) {
        try {
            int position = Integer.parseInt(MainController.getInstance().sendRequestToView(null));
        } catch (Exception e) {
            return;
        }

    }
}
