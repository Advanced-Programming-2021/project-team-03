package control.databaseController;

import com.google.gson.Gson;
import com.opencsv.exceptions.CsvException;
import model.card.Card;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import model.user.Deck;
import model.user.User;

public class Database {
    private static final String USERS_PATH = "./database/users/";
    private static final String DECKS_PATH = "./database/decks/";
    private static final String CARDS_PATH = "./database/cards/";


    public static HashMap<String, Card> updateAllCards() throws IOException, CsvException {
        HashMap<String, Card> allCardsByName = new HashMap<>();

        Reader reader = Files.newBufferedReader(Path.of(CARDS_PATH + "Monster.csv"),  StandardCharsets.UTF_8);

        CSVReader csvReader = new CSVReader(reader);
        List<String[]> list = csvReader.readAll();
        reader.close();
        csvReader.close();

        List<String> header = Arrays.asList(list.get(0));
        HashMap<String, Integer> indexes = new HashMap<>();

        indexes.put("Name", header.indexOf("Name"));
        indexes.put("Level", header.indexOf("Level"));
        indexes.put("Attribute", header.indexOf("Attribute"));
        indexes.put("Monster Type", header.indexOf("Monster Type"));
        indexes.put("Card Type", header.indexOf("Card Type"));
        indexes.put("Level", header.indexOf("Level"));


        return allCardsByName; // TODO
    }


    // TODO: updateAllDecks and updateAllUsers can be merged or refactored
    public static HashMap<String, Deck> updateAllDecks() {
        Gson gson = new Gson();
        HashMap<String, Deck> allDecks = new HashMap<>();

        File[] listOfFiles = new File(DECKS_PATH).listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Don't use parallel without proper testing. (may cause crash)
                .filter(Database::isJsonFile)
                .forEach(file -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(DECKS_PATH + file.getName()));
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

        File[] listOfFiles = new File(USERS_PATH).listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Don't use parallel without proper testing. (may cause crash)
                .filter(Database::isJsonFile)
                .forEach(file -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(USERS_PATH + file.getName()));
                        User user = gson.fromJson(reader, User.class);
                        allUsers.put(user.getUsername(), user);

                    } catch (FileNotFoundException ignored) {
                    }
                });

        return allUsers;
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
            return USERS_PATH + ((User) object).getUsername() + ".json";
        } else if (object instanceof Deck) {
            return DECKS_PATH + ((Deck) object).getDeckName() + ".json";
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


    public static void main(String[] args) throws IOException, CsvException {
        updateAllCards();
    }
}
