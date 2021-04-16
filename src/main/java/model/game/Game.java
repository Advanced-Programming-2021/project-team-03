package model.game;

import model.user.User;

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
        turn = PlayerTurn.PLAYER1;

    }

    public boolean isGameFinished() {
        // TODO
        return true;
    }

    public void changeTurn() {
        // TODO
    }
}
