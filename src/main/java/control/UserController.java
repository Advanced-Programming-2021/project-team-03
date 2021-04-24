package control;

import model.user.User;

public class UserController {

    private static UserController userController;

    private UserController() {
    }

    public static UserController getInstance() {
        if (userController == null)
            userController = new UserController();
        return userController;
    }


    public String login(String username, String password) {
        //logging in and return a token for the user to identify the user for later requests
        //TODO
        return null;
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

    public void logout(String token) {
        //TODO logging out the user
    }

    public void changeNickname(String token, String newNickname) {
        //TODO
    }

    public boolean doesTokenAndPasswordMatch(String token, String currentPassword) {
        //TODO
        return false;
    }

    public void changePassword(String token, String newPassword) {
        //TODO
    }
}
