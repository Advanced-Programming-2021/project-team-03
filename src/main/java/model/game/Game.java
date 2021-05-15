package model.game;

import model.card.Card;
import model.card.Monster;
import model.user.User;

import java.util.HashMap;

import static model.game.PlayerTurn.*;

public class Game {
    private Player player1;
    private Player player2;
    private PlayerTurn turn;
    private int numberOfRounds;
    private int currentRound;

    public Game(User user1, User user2, int numberOfRounds) { // TODO
        // TODO: Construct players with given users.
        // TODO: Construct boards with user decks.
        // TODO give each card in the main deck a "card in game" ID
        currentRound = 1;
        turn = PLAYER1;

    }

    public boolean isGameFinished() {
        // TODO
        return true;
    }

    public void changeTurn() {
        // TODO
    }

    public String showGameBoards() {
        StringBuilder board = new StringBuilder();
        if (turn == PLAYER1) {
            boardBuilder(board, player2, player1);
        } else {
            boardBuilder(board, player1, player2);
        }
        return board.toString();
    }

    private void boardBuilder(StringBuilder board, Player firstPlayer, Player secondPlayer) {
        board.append(firstPlayer.getUser().getNickname()).append(":").append(firstPlayer.getHealth()).append("\n");
        board.append(firstPlayer.getBoard().showAsEnemyBoard());
        board.append("\n--------------------------\n\n\n");
        board.append(secondPlayer.getBoard().toString());
        board.append(secondPlayer.getUser().getNickname()).append(":").append(secondPlayer.getHealth()).append("\n");
    }

    public Board getCardsInBoard(Card card) { /* monster effect */
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
