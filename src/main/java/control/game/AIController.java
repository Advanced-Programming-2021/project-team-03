package control.game;

import control.MainController;
import model.card.Card;
import model.card.Monster;
import model.game.Game;
import model.game.Player;

public class AIController {
    private static AIController AIController;

    private AIController() {
    }

    public static AIController getInstance() {
        if (AIController == null) {
            AIController = new AIController();
        }
        return AIController;
    }

    private Game game;
    private Update gameUpdate;
    private Player bot;
    private Player opponent;

    public void initialize(Game game, Update gameUpdates) {
        this.game = game;
        this.gameUpdate = gameUpdates;
        this.bot = game.getPlayer2();
        this.opponent = game.getPlayer1();
    }

    public void play() {
        StringBuilder AIPlayLog = new StringBuilder();
        AIPlayLog.append("Now it's AI turn\n");
        bot.getBoard().addCardFromRemainingToInHandCards();
        AIPlayLog.append("AI Draws a card\n");
        if (canSummonOrSet()) {
            boolean summon = summonOrSet();
            if (summon)
                AIPlayLog.append("AI Summons a monster in attacking position\n");
            else
                AIPlayLog.append("AI Set a monster in defending position\n");
        }

        while (canSetSpellsInHand()) {
            setSpell();
            AIPlayLog.append("AI Set a spell in field\n");
        }

        while (canAttack()) {
            attack();
            AIPlayLog.append("AI attacked!!!\n");
        }
        MainController.getInstance().sendPrintRequestToView(AIPlayLog.toString());
    }

    private void attack() {

    }

    private boolean canAttack() {

        return false;
    }

    private void setSpell() {

    }

    private boolean canSetSpellsInHand() {

        return false;
    }

    private boolean summonOrSet() {

        return false;
    }

    private void set() {

    }

    private void summon() {

    }

    private boolean canSummonOrSet() {
        for (Card card : bot.getBoard().getInHandCards()) {
            if (card instanceof Monster) {
                Monster monster = (Monster) card;
                if (monster.getLevel() < 5)
                    return true;
                else return checkForTribute(monster.getLevel());
            }
        }
        return false;
    }

    private boolean checkForTribute(int level) {
        if (level < 7) {
            return bot.getBoard().getMonstersInField().size() > 0;
        } else {
            return bot.getBoard().getMonstersInField().size() > 1;
        }
    }


}
