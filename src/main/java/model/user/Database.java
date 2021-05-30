package model.user;

import com.google.gson.Gson;
import model.card.Card;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Database {
    private static final String USER_DATABASE_PATH = "./database/users/";
    private static final String DECK_DATABASE_PATH = "./database/decks/";


    // TODO: updateAllDecks and updateAllUsers can be merged or refactored
    public static HashMap<String, Deck> updateAllDecks() {
        Gson gson = new Gson();
        HashMap<String, Deck> allDecks = new HashMap<>();

        File[] listOfFiles = new File(DECK_DATABASE_PATH).listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Don't use parallel without proper testing. (may cause crash)
                .filter(Database::isJsonFile)
                .forEach(file -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(DECK_DATABASE_PATH + file.getName()));
                        Deck deck = gson.fromJson(reader, Deck.class);
                        allDecks.put(deck.getDeckName(), deck);

                    } catch (FileNotFoundException ignored) {
                    }
                });

        return allDecks;
    }

    public static HashMap<String, User> updateAllUsers() {
        Gson gson = new Gson();
        HashMap<String, User> allUsers = new HashMap<>();

        File[] listOfFiles = new File(USER_DATABASE_PATH).listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Don't use parallel without proper testing. (may cause crash)
                .filter(Database::isJsonFile)
                .forEach(file -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(USER_DATABASE_PATH + file.getName()));
                        User user = gson.fromJson(reader, User.class);
                        allUsers.put(user.getUsername(), user);

                    } catch (FileNotFoundException ignored) {
                    }
                });

        return allUsers;
    }

    public static HashMap<String, Card> updateAllCards() {
        return new HashMap<>(); // TODO
    }

    private static boolean isJsonFile(File file) {
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        return i > 0 && fileName.substring(i + 1).equals("json");
    }

    public static void remove(Object object) throws DatabaseException {
        String path = pathFinder(object);
        if (!new File(path).delete()) {
            throw new DatabaseException("Couldn't remove from the path: " + path);
        }
    }

    public static void save(Object object) throws DatabaseException {
        writeToJson(object, pathFinder(object));
    }

    private static String pathFinder(Object object) throws DatabaseException {
        if (object instanceof User) {
            return USER_DATABASE_PATH + ((User) object).getUsername() + ".json";
        } else if (object instanceof Deck) {
            return DECK_DATABASE_PATH + ((Deck) object).getDeckName() + ".json";
        } else {
            throw new DatabaseException("Unknown object for database to save: " + object.getClass());
        }
    }

    private static void writeToJson(Object object, String filePath) throws DatabaseException {
        try {
            Writer writer = new FileWriter(filePath);
            new Gson().toJson(object, writer);
            writer.close();
        } catch (IOException e) {
            throw new DatabaseException("Couldn't write to the database at path: " + filePath);
        }
    }
    // TODO: we need a garbage collector for decks to remove decks that users don't have any reference to
}
