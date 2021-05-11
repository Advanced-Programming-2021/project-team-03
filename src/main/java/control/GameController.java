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

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public boolean canSummonSelectedCard(String username) {
        //TODO
        return false;
    }

    public boolean isMonsterFieldZoneFull(String username) {
        //TODO
        return false;
    }

    public boolean canPlayerSummonOrSetAnotherCard(String username) {
        //TODO
        return false;
    }

    public boolean isThereEnoughCardToTribute(String username) {
        //TODO
        return false;
    }

    public void summonCard(String username) {
        //TODO
        /*This method should ask and get tribute cards from user*/
    }

    public boolean canSetSelectedCard(String username) {
        //TODO
        return false;
    }

    public void setCard(String username) {
        //TODO
    }

    public boolean canChangeCardPosition(String username) {
        //TODO
        return false;
    }

    public boolean isCardAlreadyInThisPosition(String position) {
        //TODO
        return false;
    }

    public boolean isCardPositionChangedAlready() {
        //TODO
        return false;
    }

    public void changeCardPosition(String position) {
        //TODO
    }

    public boolean canFlipSummon() {
        //TODO
        return false;
    }

    public void flipSummon() {
        //TODO
    }

    public boolean canAttackWithThisCard() {
        //TODO
        return false;
    }

    public boolean cardAlreadyAttacked() {
        //TODO
        return false;
    }

    public boolean canAttackThisPosition(String position) {
        //TODO
        return false;
    }


    public String attack(String attacker, String position) {
        //TODO
        /*return the result of attack in a string*/
        return null;
    }
}
