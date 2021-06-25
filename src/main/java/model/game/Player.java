package model.game;

import control.game.GameController;
import model.user.User;

public class Player {
    private final User user;
    private final Board board;
    private int health;

    public Player(int initialHealth, Board board, User user) {
        this.user = user;
        this.health = initialHealth;
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public User getUser() {
        return user;
    }

    public int getHealth() {
        return health;
    }

    public void decreaseHealthByAmount(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            // TODO
            GameController.getInstance().roundIsOver();
        }

    }

    public boolean canPlayerDrawCard() {
        return (board.getRemainingCards().size() > 0);
    }
}
