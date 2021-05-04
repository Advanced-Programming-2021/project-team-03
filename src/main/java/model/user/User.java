package model.user;

import model.card.Card;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;


public class User {
    private static HashMap<String, User> allUsers;
    private final String username;
    private String nickname;
    private String passwordHash;
    private int score;
    private int balance;
    private int level;
    private ArrayList<Deck> Decks;
    private ArrayList<Card> Cards;
    private Deck activeDeck;

    static {
        allUsers = UserDatabase.updateAllUsers();
    }

    public User(String username, String password, String nickName) throws UserException {
        this.username = username;
        setNickname(nickName); // may throw an exception
        this.passwordHash = hashString(password);
        this.score = 0;
        this.balance = 0;
        this.level = 1;

        updateInDatabase(); // may throw an exception
    }

    private String hashString(String rawString) {
        // Salt-hash the rawString using the BCrypt algorithm
        return BCrypt.hashpw(rawString, BCrypt.gensalt());
    }

    public boolean doesMatchPassword(String candidatePassword) {
        // Check if the candidate salted-hash matches the passwordHash
        return BCrypt.checkpw(candidatePassword, this.passwordHash);
    }

    public void removeUser() throws UserException {
        allUsers.remove(username);
        UserDatabase.removeUser(this);
    }

    public static User getUserByUsername(String username) {
        return allUsers.get(username);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) throws UserException {
        if (allUsers.values().stream()
                .anyMatch(user -> user.nickname.equals(nickname))) {
            throw new UserException("The nickname: \"" + nickname + "\" is already taken.");
        }
        this.nickname = nickname;
        updateInDatabase();
    }

    public void changePassword(String oldPassword, String newPassword) throws UserException {
        if (doesMatchPassword(oldPassword)) {
            this.passwordHash = hashString(newPassword);
            updateInDatabase();
            return;
        }
        throw new UserException("Entered password does not match the old password.");
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) throws UserException {
        this.score = score;
        updateInDatabase();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) throws UserException {
        this.balance = balance;
        updateInDatabase();
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) throws UserException {
        this.level = level;
        updateInDatabase();
    }

    public Deck getActiveDeck() {
        return activeDeck;
    }

    public void setActiveDeck(Deck activeDeck) {
        this.activeDeck = activeDeck;
    }

    public void updateInDatabase() throws UserException {
        UserDatabase.updateUser(this);
    }
}
