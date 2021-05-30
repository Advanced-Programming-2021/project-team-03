package model.game;

import control.game.GameController;
import model.card.Card;
import model.user.User;

import static model.game.PlayerTurn.*;

public class Game {
    private Player player1;
    private Player player2;

    private int numberOfRounds;

    public Game(User user1, User user2, int numberOfRounds) { // TODO
        // TODO: Construct players with given users.
        // TODO: Construct boards with user decks.
        // TODO give each card in the main deck a "card in game" ID
        this.numberOfRounds = numberOfRounds;
    }

    public Game(User user1, int numberOfRounds) {
        /*duel with AI*/
    }

    public Player getPlayerByName(String username) {
        if (player1.getUser().getUsername().equals(username))
            return player1;
        else
            return player2;
    }

    public Player getPlayerByTurn(PlayerTurn turn) {
        if (turn == PLAYER1)
            return player1;
        else
            return player2;
    }

    public Player getPlayerOpponentByTurn(PlayerTurn turn) {
        if (turn == PLAYER1)
            return player2;
        else
            return player1;
    }

    public String showGameBoards() {
        StringBuilder board = new StringBuilder();
        if (GameController.getInstance().getTurn() == PLAYER1) {
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

    public PlayerTurn getCardsOwner(Card card) { /* monster effect */
        if (player1.getBoard().doesContainCard(card.getCardIdInTheGame()))
            return PLAYER1;
        if (player2.getBoard().doesContainCard(card.getCardIdInTheGame()))
            return PLAYER2;
        return null;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }
}
