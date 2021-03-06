package server.model.user;

import org.mindrot.jbcrypt.BCrypt;
import server.control.MainController;
import server.control.databaseController.Database;
import server.control.databaseController.DatabaseException;
import server.model.card.Card;
import server.model.card.Monster;
import server.model.card.SpellAndTrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;


public class User {
    private static final HashMap<String, User> allUsers = new HashMap<>();
    private final String username;
    private String nickname;
    private String passwordHash;
    private int score;
    private int balance;
    private int level;
    private final ArrayList<Deck> decks;
    private ArrayList<Card> cards;
    private Deck activeDeck;
    private int profileImageID;

    public static void initialize() {
        Database.updateAllUsers();
    }

    // Overrides the previous username if already exists
    public User(String username, String password, String nickname) throws DatabaseException {
        this.username = username;
        this.passwordHash = hashString(password);
        this.score = 0;
        this.level = 1;

        decks = new ArrayList<>();
        cards = new ArrayList<>();
        allUsers.put(username, this);
        setNickname(nickname); // may throw an exception
        // line above also updates the user in database
    }

    private String hashString(String rawString) {
        // Salt-hash the rawString using the BCrypt algorithm
        return BCrypt.hashpw(rawString, BCrypt.gensalt());
    }

    public boolean doesMatchPassword(String candidatePassword) {
        // Check if the candidate salted-hash matches the passwordHash
        return !BCrypt.checkpw(candidatePassword, this.passwordHash);
    }

    public String getNickname() {
        return nickname;
    }

    public synchronized void setNickname(String nickname) throws DatabaseException {
        if (doesNicknameExists(nickname)) {
            throw new DatabaseException("The nickname \"" + nickname + "\" is already taken.");
        }
        this.nickname = nickname;
        updateInDatabase();
    }

    public static boolean doesNicknameExists(String nickname) {
        return allUsers.values().stream()
                .anyMatch(user -> user.nickname != null && user.nickname.equals(nickname));
    }

    public void changePassword(String newPassword) throws DatabaseException {
        this.passwordHash = hashString(newPassword);
        updateInDatabase();
    }

    public String getUsername() {
        return username;
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized void setScore(int score) throws DatabaseException {
        this.score = Math.max(0, score);
        updateInDatabase();
    }

    public int getBalance() {
        return balance;
    }

    public synchronized void increaseBalance(int balance) throws DatabaseException {
        this.balance += balance;
        updateInDatabase();
    }

    public ArrayList<Deck> getDecks() {
        return decks;
    }

    public synchronized void addDeck(Deck deck) throws DatabaseException {
        decks.add(deck);
        updateInDatabase();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public synchronized void setCards(ArrayList<Card> cards) throws DatabaseException {
        this.cards = cards;
        updateInDatabase();
    }

    public int getLevel() {
        return level;
    }

    public synchronized void setLevel(int level) throws DatabaseException {
        this.level = level;
        updateInDatabase();
    }

    public Deck getActiveDeck() {
        return activeDeck;
    }

    public synchronized void setActiveDeck(Deck activeDeck) throws DatabaseException {
        this.activeDeck = activeDeck;
        updateInDatabase();
    }

    public void updateInDatabase() throws DatabaseException {
        if (!MainController.initializing) Database.save(this);
    }

    public static User getByUsername(String username) {
        return allUsers.get(username);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public static ArrayList<User> getScoreBoard() {
        ArrayList<User> scoreBoard = (new ArrayList<>(allUsers.values()));
        scoreBoard.sort((user1, user2) -> {
            if (user1.score == user2.score) return user1.nickname.compareTo(user2.nickname);
            return user2.score - user1.score;
        });
        return scoreBoard;
    }

    public synchronized void increaseScore(int amount) throws DatabaseException {
        score += amount;
        updateInDatabase();
    }

    public synchronized void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void addCard(Card card) throws DatabaseException {
        cards.add(card);
        updateInDatabase();
    }

    public void deleteDeck(Deck deck) throws DatabaseException {
        if (activeDeck == deck) activeDeck = null;
        decks.removeIf(userDeck -> userDeck == deck);
        updateInDatabase();
    }

    public synchronized void setStarterPack() throws DatabaseException {
        ArrayList<Monster> monsters = new ArrayList<>(Monster.getAllMonsters().values());
        ArrayList<SpellAndTrap> spellAndTraps = new ArrayList<>(SpellAndTrap.getAllSpellAndTraps().values());
        Collections.shuffle(monsters);
        Collections.shuffle(spellAndTraps);

        cards.addAll(monsters.stream().limit(30).collect(Collectors.toList()));
        cards.addAll(spellAndTraps.stream().limit(20).collect(Collectors.toList()));

        profileImageID = (new Random()).nextInt(20) + 1;
        balance = 10000;

        updateInDatabase();
    }

    public synchronized void setProfileImageID(int profileImageID) {
        this.profileImageID = profileImageID;
    }

    public int getProfileImageID() {
        return profileImageID;
    }

    public int getNumberOfCards(String cardName) {
        return (int) cards.stream().filter(card -> card.getCardName().equals(cardName)).count();
    }

    public void removeCard(Card card) throws DatabaseException {
        cards.remove(card);
        updateInDatabase();
    }
}
