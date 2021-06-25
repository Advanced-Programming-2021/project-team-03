package control.game;

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

    }
}
