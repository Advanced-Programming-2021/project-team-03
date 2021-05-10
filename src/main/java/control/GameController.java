package control;

import model.card.Card;
import model.game.Game;

enum Phase {
    DRAW,
    STANDBY,
    FIRST_MAIN,
    BATTLE,
    SECOND_MAIN,
}

public class GameController {
    private static GameController gameController;

    private GameController() {
    }

    public static GameController getInstance() {
        if (gameController == null)
            gameController = new GameController();
        return gameController;
    }


    private Card selectedCard;
    private Phase currentPhase;
    private Game game;

    public void newDuel(String firstPlayerName, String secondPlayerName, int numberOfRound) {
        //TODO
    }

    public void newDuelWithAI(String username, int numberOfRound) {
        //TODO
    }

    public boolean isCardAddressValid(String cardType, int cardPosition) {
        //TODO
        return false;
    }

    public boolean isThereACardInGivenPosition(String cardType, int cardPosition, boolean isOpponentCard) {
        //TODO
        return false;
    }

    public void selectCard(String cardType, int cardPosition, boolean isOpponentCard) {
        //TODO
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void deselectCard() {
        //TODO
    }
}
