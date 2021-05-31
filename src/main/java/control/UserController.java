package control;

import control.databaseController.DatabaseException;
import model.user.User;

import java.util.ArrayList;


public class UserController {
    private static UserController userController;

    private UserController() {
    }

    public static UserController getInstance() {
        if (userController == null)
            userController = new UserController();
        return userController;
    }

    public boolean doesUsernameExist(String username) { // checking if a user with this username exists
        return User.getByUsername(username) != null;
    }

    public boolean doesNicknameExist(String nickname) {// checking if a user with this nickname exists
        return User.doesNicknameExists(nickname);
    }

    public void registerUsername(String username, String password, String nickname) {
        try {
            new User(username, password, nickname);
        } catch (DatabaseException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        return User.getByUsername(username).doesMatchPassword(password);
    }

    public void changeNickname(String username, String newNickname) {
        try {
            User.getByUsername(username).setNickname(newNickname);
        } catch (DatabaseException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void changePassword(String username, String newPassword) {
        try {
            User.getByUsername(username).changePassword(newPassword);
        } catch (DatabaseException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public String getAllUsersForUsername() {
        ArrayList<User> allUsersSorted = User.getScoreBoard();
        StringBuilder scoreboard = new StringBuilder();

        int rank = 1;
        for (int i = 0; i < allUsersSorted.size(); i++) {
            User user = allUsersSorted.get(i);
            if (i != 0 && user.getScore() < allUsersSorted.get(i - 1).getScore()) rank++;

            scoreboard.append(rank).append("- ")
                    .append(user.getNickname()).append(": ")
                    .append(user.getScore()).append("\n");
        }
        return scoreboard.toString();
    }

    public boolean doesPlayerHaveActiveDeck(String username) {
        return User.getByUsername(username).getActiveDeck() != null;
    }

    public boolean isUserActiveDeckValid(String username) {
        return User.getByUsername(username).getActiveDeck().isDeckValid();
    }

    public String showCardToUser(String cardName) {
        //TODO Habib
        return null;
    }
}
