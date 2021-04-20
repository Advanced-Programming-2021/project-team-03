package model.game;

import model.card.Card;
import model.card.Monster;
import model.user.User;

import java.util.HashMap;

enum PlayerTurn {
    PLAYER1,
    PLAYER2
}

public class Game {
    private Player player1;
    private Player player2;
    private PlayerTurn turn;

    public Game(User user1, User user2) { // TODO
        // TODO: Construct players with given users.
        // TODO: Construct boards with user decks.
        // TODO give each card in the main deck a "card in game" ID
        turn = PlayerTurn.PLAYER1;

    }

    public boolean isGameFinished() {
        // TODO
        return true;
    }

    public void changeTurn() {
        // TODO
    }

    public String makeGameBoardToShow() {
        //TODO
        return "Game Board";
    }

    public Board getCardBoard(Card card) {
        if (player1.getBoard().doesContainCard(card.getCardIdInTheGame()))
            return player1.getBoard();

        if (player2.getBoard().doesContainCard(card.getCardIdInTheGame()))
            return player2.getBoard();

        return null;
    }

    public PlayerTurn getTurn() {
        return turn;
    }

    public void setTurn(PlayerTurn turn) {
        this.turn = turn;
    }
}
