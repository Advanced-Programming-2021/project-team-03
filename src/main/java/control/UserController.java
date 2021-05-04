package control;

import model.user.User;
import model.user.UserException;
import org.json.JSONArray;


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

    public JSONArray getAllUsersForUsername() {
        return new JSONArray(User.getScoreBoard());
    }
}
