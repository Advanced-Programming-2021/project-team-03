package control;

import model.card.Card;
import model.card.Monster;
import model.enums.AttackingFormat;
import model.game.Board;
import model.game.Game;
import model.game.PlayerTurn;
import model.user.User;

import java.util.Random;

import static control.Phase.*;
import static control.TypeOfSelectedCard.*;
import static model.game.PlayerTurn.*;

enum Phase {
    DRAW,
    STANDBY,
    FIRST_MAIN,
    BATTLE,
    SECOND_MAIN,
}

enum TypeOfSelectedCard {
    MONSTER,
    SPELL,
    FIELD_CARD,
    HAND_CARD,
}

public class GameController {
    private static GameController gameController;

    private GameController() {
        this.currentPhase = DRAW;
        this.currentRound = 1;

        // creating a random number to determine the starting player
        Random random = new Random();
        if (random.nextInt(2) % 2 == 0)
            turn = PLAYER1;
        else
            turn = PLAYER2;
    }

    public static GameController getInstance() {
        if (gameController == null)
            gameController = new GameController();
        return gameController;
    }

    private Update gameUpdates;
    private Card selectedCard;
    private TypeOfSelectedCard typeOfSelectedCard;
    private Phase currentPhase;
    private Game game;
    private PlayerTurn turn;
    private int currentRound;

    public void newDuel(String firstPlayerName, String secondPlayerName, int numberOfRound) {
        game = new Game(User.getByUsername(firstPlayerName), User.getByUsername(secondPlayerName), numberOfRound);
        gameUpdates = new Update(game);
    }

    public void newDuelWithAI(String username, int numberOfRound) {
        game = new Game(User.getByUsername(username), numberOfRound);
        gameUpdates = new Update(game);
    }

    public boolean isCardAddressValid(int cardPosition) {
        return cardPosition <= 6 && cardPosition >= 1;
    }

    public boolean isThereACardInGivenPosition(String cardType, int cardPosition, boolean isOpponentCard) {
        Board board;
        if ((turn == PlayerTurn.PLAYER1 && isOpponentCard)
                || (turn == PlayerTurn.PLAYER2 && !isOpponentCard)) {
            board = game.getPlayer2().getBoard();
        } else {
            board = game.getPlayer1().getBoard();
        }
        return checkTheBoard(board, cardType, cardPosition);
    }

    private boolean checkTheBoard(Board board, String cardType, int cardPosition) {
        // return true if the board contain a card in given position
        switch (cardType) {
            case "Monster" -> {
                if (board.getMonstersInField().containsKey(cardPosition))
                    return true;
            }
            case "Spell" -> {
                if (board.getSpellAndTrapsInField().containsKey(cardPosition))
                    return true;
            }
            case "Field" -> {
                if (board.getFieldCard() != null)
                    return true;
            }
            case "Hand" -> {
                if (board.getInHandCards().size() <= cardPosition)
                    return true;
            }
        }
        return false;
    }

    public void selectCard(String cardType, int cardPosition, boolean isOpponentCard) {
        Board board;
        if ((turn == PlayerTurn.PLAYER1 && isOpponentCard)
                || (turn == PlayerTurn.PLAYER2 && !isOpponentCard)) {
            board = game.getPlayer2().getBoard();
        } else {
            board = game.getPlayer1().getBoard();
        }
        switch (cardType) {
            case "Monster" -> {
                selectedCard = board.getMonsterByPosition(cardPosition);
                typeOfSelectedCard = MONSTER;
            }
            case "Spell" -> {
                selectedCard = board.getSpellAndTrapByPosition(cardPosition);
                typeOfSelectedCard = SPELL;
            }
            case "Field" -> {
                selectedCard = board.getFieldCard();
                typeOfSelectedCard = FIELD_CARD;
            }
            case "Hand" -> {
                selectedCard = board.getInHandCardByPosition(cardPosition);
                typeOfSelectedCard = HAND_CARD;
            }
        }
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void deselectCard() {
        selectedCard = null;
        typeOfSelectedCard = null;
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

    public boolean canAttackWithThisCard(String username) {
        if (typeOfSelectedCard == MONSTER) {
            Monster monster = (Monster) selectedCard;
            return monster.getAttackingFormat() == AttackingFormat.ATTACKING;
        }
        return false;
    }

    public boolean cardAlreadyAttacked() {
        if (selectedCard instanceof Monster)
            return gameUpdates.didMonsterAttack((Monster) selectedCard);
        else return false;
    }

    public boolean canAttackThisPosition(String position) {
        int positionNumber;
        try {
            positionNumber = Integer.parseInt(position);
        } catch (Exception e) {
            return false;
        }
        return isThereACardInGivenPosition("Monster", positionNumber, true);
    }


    public String attack(String attackingPlayerUsername, String position) {
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

    public void changeTurn() {
        if (turn == PLAYER1)
            turn = PLAYER2;
        else
            turn = PLAYER1;
    }

    public PlayerTurn getTurn() {
        return turn;
    }

    public boolean isGameFinished() {
        if (turn == PLAYER1 && !game.getPlayer1().canPlayerDrawCard())
            return true;
        if (turn == PLAYER2 && !game.getPlayer2().canPlayerDrawCard())
            return true;
        return game.getNumberOfRounds() < currentRound;
    }
}
