package model.game;

import model.user.User;

public class Player {
    private User user;
    private Board board;
    private int health;

    public Player(int initialHealth, Board board, User user) {
        this.user = user;
        this.health = initialHealth;
        this.board = board;
        // TODO
    }
}
