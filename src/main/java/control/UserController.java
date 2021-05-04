package control;

import model.user.User;
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

    public boolean isUsernameExists(String username) { // checking if a user with this username exists
        // TODO check for the username
        return false;
    }

    public boolean isNicknameExists(String nickname) {// checking if a user with this nickname exists
        // TODO check for the nickname
        return false;
    }

    public void registerUsername(String username, String password, String nickname) {
        // register a new user with the given properties
        //TODO register
    }

    public boolean doesUsernameAndPasswordMatch(String username, String password) {
        // TODO
        return false;
    }

    public void changeNickname(String username, String newNickname) {
        //TODO
    }

    public void changePassword(String username, String newPassword) {
        //TODO
    }


    public JSONArray getAllUsersForUsername() {
        // TODO this method returns a JSON array of all users sorted by their scores
        return null;
    }
}
