package model.user;

import control.databaseController.Database;
import control.databaseController.DatabaseException;
import model.card.Card;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;


public class User {
    private static final HashMap<String, User> allUsers;
    private final String username;
    private String nickname;
    private String passwordHash;
    private int score;
    private int balance;
    private int level;
    private final ArrayList<Deck> decks;
    private ArrayList<Card> cards;
    private Deck activeDeck;

    static {
        allUsers = Database.updateAllUsers();
    }

    public User(String username, String password, String nickname) throws DatabaseException {
        this.username = username;
        setNickname(nickname); // may throw an exception
        this.passwordHash = hashString(password);
        this.score = 0;
        this.balance = 1000;
        this.level = 1;

        decks = new ArrayList<>();
        cards = new ArrayList<>();
        allUsers.put(username, this);
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

    public void removeUser() throws DatabaseException {
        allUsers.remove(username);
        Database.remove(this);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) throws DatabaseException {
        if (doesNicknameExists(nickname)) {
            throw new DatabaseException("The nickname \"" + nickname + "\" is already taken.");
        }
        this.nickname = nickname;
        updateInDatabase();
    }

    public static boolean doesNicknameExists(String nickname) {
        return allUsers.values().stream()
                .anyMatch(user -> user.nickname.equals(nickname));
    }

    public void changePassword(String oldPassword, String newPassword) throws DatabaseException {
        if (doesMatchPassword(oldPassword)) {
            this.passwordHash = hashString(newPassword);
            updateInDatabase();
            return;
        }
        throw new DatabaseException("Entered password does not match the old password.");
    }

    public void changePassword(String newPassword) throws DatabaseException {
        this.passwordHash = hashString(newPassword);
        updateInDatabase();
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) throws DatabaseException {
        this.score = Math.max(0, score);
        updateInDatabase();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) throws DatabaseException {
        this.balance = balance;
        updateInDatabase();
    }

    public ArrayList<Deck> getDecks() {
        return decks;
    }

    public void addDeck(Deck deck) {
        decks.add(deck);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) throws DatabaseException {
        this.level = level;
        updateInDatabase();
    }

    public Deck getActiveDeck() {
        return activeDeck;
    }

    public void setActiveDeck(Deck activeDeck) {
        this.activeDeck = activeDeck;
    }

    public void updateInDatabase() throws DatabaseException {
        Database.save(this);
    }

    public static User getByUsername(String username) {
        return allUsers.get(username);
    }

    public static ArrayList<User> getScoreBoard() {
        ArrayList<User> scoreBoard = (new ArrayList<>(allUsers.values()));
        scoreBoard.sort((user1, user2) -> {
            Integer.compare(user1.score, user2.score);
            if (user1.score == user2.score) return user1.nickname.compareTo(user2.nickname);
            return user2.score - user1.score;
        });
        return scoreBoard;
    }

    public void increaseScore(int amount) {
        score += amount;
    }
}
