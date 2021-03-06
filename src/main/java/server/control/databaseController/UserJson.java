package server.control.databaseController;

import server.model.card.Card;
import server.model.user.Deck;
import server.model.user.User;

import java.util.ArrayList;

public class UserJson {
    private final String username;
    private final String nickname;
    private final String passwordHash;
    private final int score;
    private final int balance;
    private final int level;
    private String activeDeck;
    private final ArrayList<String> decks;
    private final ArrayList<String> cards;
    private final int profileImageID;

    public UserJson(User user) {
        username = user.getUsername();
        nickname = user.getNickname();
        passwordHash = user.getPasswordHash();
        score = user.getScore();
        balance = user.getBalance();
        level = user.getLevel();
        profileImageID = user.getProfileImageID();
        if (user.getActiveDeck() != null) activeDeck = user.getActiveDeck().getDeckName();

        decks = new ArrayList<>();
        cards = new ArrayList<>();

        user.getDecks().forEach(deck -> {
            if (deck != null) decks.add(deck.getDeckName());
        });
        user.getCards().forEach(card -> {
            if (card != null) cards.add(card.getCardName());
        });
    }

    public User convert() throws DatabaseException {
        User user = new User(username, "temp", nickname);
        user.setPasswordHash(passwordHash);
        user.setScore(score);
        user.increaseBalance(balance);
        user.setLevel(level);
        user.setActiveDeck(Deck.getByDeckName(activeDeck));
        user.setProfileImageID(profileImageID);

        decks.forEach(deckName -> {
            try {
                user.addDeck(Deck.getByDeckName(deckName));
            } catch (DatabaseException ignored) {
            }
        });
        cards.forEach(cardName -> {
            try {
                user.addCard(Card.getCardByName(cardName));
            } catch (DatabaseException ignored) {
            }
        });

        return user;
    }
}
