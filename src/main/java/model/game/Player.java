package model.game;

import model.user.User;

public class Player {
    private final User user;
    private final Board board;
    private int health;

    public Player(int initialHealth, Board board, User user) {
        this.user = user;
        this.health = initialHealth;
        this.board = board;
        // TODO
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

    public void setHealth(int health) {
        this.health = health;
    }
}
