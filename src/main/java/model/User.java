package model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String username;
    private String nickName;
    private String passwordHash;
    private int score;
    private int balance;
    private ArrayList<Deck> Decks;
    private ArrayList<Card> Cards;

    private static HashMap<String, User> allUsers;

    public User(String username, String password, String nickName) {

    }

    public static void removeUser(String username) {
        allUsers.remove(username);
    }

    public static User getUserByUsername(String username) {
        return allUsers.get(username);
    }

    public static HashMap<String, User> getAllUsers() {
        return allUsers;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public ArrayList<Deck> getDecks() {
        return Decks;
    }

    public void setDecks(ArrayList<Deck> decks) {
        Decks = decks;
    }

    public ArrayList<Card> getCards() {
        return Cards;
    }

    public void setCards(ArrayList<Card> cards) {
        Cards = cards;
    }
}
