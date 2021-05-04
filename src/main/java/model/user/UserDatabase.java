package model.user;

import com.google.gson.Gson;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

class UserDatabase {
    private static final String USER_DATABASE_PATH = "./database/users/";

    static HashMap<String, User> updateAllUsers() {
        Gson gson = new Gson();
        HashMap<String, User> allUsers = new HashMap<>();

        File[] listOfFiles = new File(USER_DATABASE_PATH).listFiles();
        assert listOfFiles != null;

        Arrays.stream(listOfFiles)   // Using parallel may cause a crash
                .filter(UserDatabase::isJsonFile)
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

    private static boolean isJsonFile(File file) {
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        return i > 0 && fileName.substring(i + 1).equals("json");
    }

    static void removeUser(User user) throws UserException {
        if (!new File(USER_DATABASE_PATH + user.getUsername()).delete()) { // TODO remove decks with user
            throw new UserException("Couldn't remove the user in database.");
        }
    }

    static void updateUser(User user) throws UserException {
        try {
            Writer writer = new FileWriter(USER_DATABASE_PATH + user.getUsername() + ".json");
            new Gson().toJson(user, writer);
            writer.close();
        } catch (IOException e) {
            throw new UserException("Couldn't reach the database.");
        }
    }
}
