package control;

import model.card.Card;
import model.game.Game;
import model.user.User;

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
        game = new Game(User.getByUsername(firstPlayerName),)
    }

    public void newDuelWithAI(String username, int numberOfRound) {
        //TODO
    }

    public boolean isCardAddressValid(int cardPosition) {
        return cardPosition <= 6 && cardPosition >= 1;
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
        selectedCard = null;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public boolean canSummonSelectedCard(String username) {
        //TODO
        return false;
    }

    public boolean isCardFieldZoneFull(String username) {
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

    public String summonCard(String username) {
        //TODO
        /*This method should ask and get tribute cards from user*/
        return null;
    }

    public boolean canSetSelectedCard(String username) {
        //TODO
        return false;
    }

    public String setCard(String username) {
        //TODO
        return null;
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

    public boolean canAttackDirectly() {
        //TODO
        return false;
    }

    public int attackDirectlyToTheOpponent() {
        //TODO
        /*return the opponents receiving damage*/
        return 0;
    }

    public boolean isSpellCard() {
        //TODO
        return false;
    }

    public boolean cardAlreadyActivated() {
        //TODO
        return false;
    }

    public boolean doesFieldHaveSpaceForThisCard() {
        //TODO
        return false;
    }

    public boolean canCardActivate() {
        //TODO
        return false;
    }

    public void activateSpellCard() {
        //TODO
    }

    public String getGraveyard(String username) {
        //TODO
        return null;
    }

    public boolean canShowSelectedCardToPlayer(String username) {
        //TODO
        return false;
    }

    public String surrender(String username) {
        //TODO
        /*return the surrender message*/
        return null;
    }
}
