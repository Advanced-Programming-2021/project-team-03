package control.databaseController;

import com.google.gson.Gson;
import com.opencsv.bean.CsvToBeanBuilder;
import control.MainController;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;
import model.user.Deck;
import model.user.User;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Database {
    public static final String USERS_PATH = "./database/users/";
    public static final String DECKS_PATH = "./database/decks/";
    public static final String CARDS_PATH = "./database/cards/";
    public static final String CARDS_EXPORT_PATH = "./database/cards/export/";
    public static final String CARDS_IMPORT_PATH = "./database/cards/import/";

    public static HashMap<String, Monster> updateMonsters() throws IOException {
        HashMap<String, Monster> monstersByName = new HashMap<>();
        Reader reader = new BufferedReader(new FileReader(CARDS_PATH + "Monster.csv"));

        List<MonsterCSV> monsters =  new CsvToBeanBuilder(reader)
                .withType(MonsterCSV.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build().parse();
        reader.close();

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

    public static HashMap<String, SpellAndTrap> updateSpellAndTraps() throws IOException {
        HashMap<String, SpellAndTrap> spellsByName = new HashMap<>();
        Reader reader = new BufferedReader(new FileReader(CARDS_PATH + "SpellTrap.csv"));

        List<SpellAndTrapCSV> spells =  new CsvToBeanBuilder(reader)
                .withType(SpellAndTrapCSV.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build().parse();
        reader.close();

        for (SpellAndTrapCSV spellCSV : spells) {
            try {
                SpellAndTrap spellAndTrap = spellCSV.convert();
                spellsByName.put(spellAndTrap.getCardName(), spellAndTrap);
            } catch (IllegalArgumentException exception) {
                System.out.println("\n!!! Couldn't import spell and trap card: " + spellCSV.getName());
                System.out.println(exception.getMessage() + "\n");
            }
        }
        return spellsByName;
    }

    public static void updateImportedCards() {
        File parentFile = new File(CARDS_IMPORT_PATH);
        parentFile.mkdirs();

        File[] listOfFiles = parentFile.listFiles();
        if (listOfFiles == null) return;

        for (File file : listOfFiles) {
            if (isJsonFile(file)) {
                String fileName = file.getName();
                try {
                    Card card = importCard(fileName.substring(0, fileName.lastIndexOf('.')));
                    if (card instanceof Monster) ((Monster) card).addToAllMonsters();
                    else ((SpellAndTrap) card).addToAllSpells();
                } catch (DatabaseException ignored) {
                }
            }
        }
    }

    public static Card importCard(String cardName) throws DatabaseException {
        Gson gson = new Gson();
        Gson gson2 = new Gson();

        File parentFile = new File(CARDS_IMPORT_PATH);
        parentFile.mkdirs();

        File[] listOfFiles = parentFile.listFiles();
        if (listOfFiles == null)
            throw new DatabaseException("Can not read path: " + CARDS_IMPORT_PATH + " to import card");

        for (File file : listOfFiles) {
            if (isJsonFile(file) && file.getName().equals(cardName + ".json")) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new FileReader(CARDS_IMPORT_PATH + file.getName()));
                    BufferedReader reader2 = new BufferedReader(
                            new FileReader(CARDS_IMPORT_PATH + file.getName()));

                    MonsterCSV monsterCSV = gson.fromJson(reader, MonsterCSV.class);
                    SpellAndTrapCSV spellAndTrapCSV = gson2.fromJson(reader2, SpellAndTrapCSV.class);
                    reader.close();
                    reader2.close();

                    if (monsterCSV.getAttribute() != null) return monsterCSV.convert().addToAllMonsters();
                    else return spellAndTrapCSV.convert().addToAllSpells();

                } catch (Exception e) {
                    throw new DatabaseException("Couldn't import card from: " + CARDS_IMPORT_PATH +
                            "\nError: " + e.getMessage());
                }
            }
        }
        throw new DatabaseException("No card found named \"" + cardName + "\" here: " + CARDS_IMPORT_PATH);
    }

    public static void updateAllDecks() {
        Gson gson = new Gson();

        File parentFile = new File(DECKS_PATH);
        parentFile.mkdirs();

        File[] listOfFiles = parentFile.listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Don't use parallel without proper testing. (may cause crash)
                .filter(Database::isJsonFile)
                .forEach(file -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(DECKS_PATH + file.getName()));
                        DeckJson deckJson = gson.fromJson(reader, DeckJson.class);
                        if (deckJson != null) deckJson.convert();
                        reader.close();

                    } catch (IOException | DatabaseException ignored) {}
                });
    }

    public static void updateAllUsers() {
        Gson gson = new Gson();

        File parentFile = new File(USERS_PATH);
        parentFile.mkdirs();

        File[] listOfFiles = parentFile.listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Don't use parallel without proper testing. (may cause crash)
                .filter(Database::isJsonFile)
                .forEach(file -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(USERS_PATH + file.getName()));
                        gson.fromJson(reader, UserJson.class).convert();
                        reader.close();

                    } catch (DatabaseException | IOException ignored) {
                    }
                });
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

    public static void save(User user) throws DatabaseException {
        UserJson userJson = new UserJson(user);
        writeToJson(userJson, pathFinder(user));
    }

    public static void save(Deck deck) throws DatabaseException {
        DeckJson deckJson = new DeckJson(deck);
        writeToJson(deckJson, pathFinder(deck));
    }

    public static void save(Card card) throws DatabaseException {
        Object object;
        if (card instanceof Monster) object = MonsterCSV.exportMonsterCSV((Monster) card);
        else object = SpellAndTrapCSV.exportSpellAndTrapCSV((SpellAndTrap) card);
        writeToJson(object, pathFinder(card));
    }

    private static String pathFinder(Object object) throws DatabaseException {
        if (object instanceof User) {
            return USERS_PATH + ((User) object).getUsername() + ".json";
        } else if (object instanceof Deck) {
            return DECKS_PATH + ((Deck) object).getDeckName() + ".json";
        } else if (object instanceof Card) {
            return CARDS_EXPORT_PATH + ((Card) object).getCardName() + ".json";
        } else {
            throw new DatabaseException("Unknown object for database to save: " + object.getClass());
        }
    }

    private static void writeToJson(Object object, String filePath) throws DatabaseException { // TODO pretty print JSON
        if (MainController.initializing) return;
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            Writer writer = new FileWriter(file);
            new Gson().toJson(object, writer);
            writer.close();
        } catch (IOException e) {
            throw new DatabaseException("Couldn't write to the database at path: " + filePath);
        }
    }

    public static String toEnumCase(String string) {
        return string.toUpperCase()
                .replace(' ', '_')
                .replace('-', '_');
    }
}
