package control;

import model.user.User;
import model.user.UserException;
import org.json.JSONArray;

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
        return User.get(username) != null;
    }

    public boolean doesNicknameExist(String nickname) {// checking if a user with this nickname exists
        return User.doesNicknameExists(nickname);
    }

    public void registerUsername(String username, String password, String nickname) {
        try {
            new User(username, password, nickname);
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        return User.get(username).doesMatchPassword(password);
    }

    public void changeNickname(String username, String newNickname) {
        try {
            User.get(username).setNickname(newNickname);
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    public void changePassword(String username, String newPassword) {
        try {
            User.get(username).changePassword(newPassword);
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    public String getAllUsersForUsername() {
        ArrayList<User> allUsersSorted = User.getScoreBoard();
        StringBuilder scoreboard = new StringBuilder();
        for (int i = 1; i <= allUsersSorted.size(); i++) {
            User user = allUsersSorted.get(i - 1);
            scoreboard.append(i).append("- ")
                    .append(user.getNickname()).append(": ")
                    .append(user.getScore()).append("\n");
        }
        return scoreboard.toString();
    }
}
