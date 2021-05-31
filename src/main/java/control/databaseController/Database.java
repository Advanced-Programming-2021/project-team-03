package control.databaseController;

import com.google.gson.Gson;
import com.opencsv.bean.CsvToBeanBuilder;
import model.card.Monster;
import model.user.Deck;
import model.user.User;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Database {
    private static final String USERS_PATH = "./database/users/";
    private static final String DECKS_PATH = "./database/decks/";
    private static final String CARDS_PATH = "./database/cards/";

    public static HashMap<String, Monster> updateMonsters() throws FileNotFoundException {
        HashMap<String, Monster> monstersByName = new HashMap<>();
        Reader reader = new BufferedReader(new FileReader(CARDS_PATH + "Monster.csv"));

        List<MonsterCSV> monsters =  new CsvToBeanBuilder(reader)
                .withType(MonsterCSV.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build()
                .parse();

        for (MonsterCSV monsterCSV : monsters) {
            try {
                Monster monster = monsterCSV.convert();
                monstersByName.put(monster.getCardName(), monster);
            } catch (IllegalArgumentException exception) {
                System.out.println("\n!!! Couldn't import monster card: " + monsterCSV.getName());
                System.out.println(exception.getMessage() + "\n");
            }
        }
        return monstersByName;
    }

    public static HashMap<String, Monster> updateSpellAndTraps() throws FileNotFoundException {
        // TODO
        return new HashMap<>();
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
}
