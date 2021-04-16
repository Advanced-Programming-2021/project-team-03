package model.game;

import model.user.User;

public class Player {
    private final User user;
    private final Board board;
    private final int health;

    public Player(int initialHealth, Board board, User user) {
        this.user = user;
        this.health = initialHealth;
        this.board = board;
        // TODO
    }
}
