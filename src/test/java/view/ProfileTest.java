package view;

import control.MainController;
import control.UserController;
import model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {
    @Test
    void changeNickname() {
        MainController mainController = MainController.getInstance();
        UserController userController = UserController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u aliNickname -p ali2logoutPass -n NicknameNickname");
        view.loginUser("user login -u aliNickname -p ali2logoutPass");
        assert mainController.getOnlineUsers().containsValue("aliNickname");
        assert userController.doesNicknameExist("NicknameNickname");

        view.changeNickname("profile change -n NicknameNewNickname");
        assert !userController.doesNicknameExist("NicknameNickname");
        assert userController.doesNicknameExist("NicknameNewNickname");

        View.testing = false;
    }

    @Test
    void failedChangeNickname() {
        MainController mainController = MainController.getInstance();
        UserController userController = UserController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u failedNickname -p pass -n failedNickname");
        view.createNewUser("user create -u failedNickname2 -p pass -n failedNickname2");
        view.loginUser("user login -u failedNickname -p pass");
        assert mainController.getOnlineUsers().containsValue("failedNickname");
        assert userController.doesNicknameExist("failedNickname");

        view.changeNickname("profile change -n failedNickname2");
        Assertions.assertEquals("failedNickname", User.getByUsername("failedNickname").getNickname());

        View.testing = false;
    }

    @Test
    void changePassword() {
        MainController mainController = MainController.getInstance();
        UserController userController = UserController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        userController.registerUsername("changePassword", "pass", "changePassword");
        view.loginUser("user login -u changePassword -p pass");
        assert mainController.getOnlineUsers().containsValue("changePassword");

        view.changePassword("profile change -p -c wrong -n newWrongPass", 4);
        assert userController.doesUsernameAndPasswordMatch("changePassword", "pass");
        assert !userController.doesUsernameAndPasswordMatch("changePassword", "newWrongPass");

        view.changePassword("profile change -p -c pass -n newPass", 4);
        assert !userController.doesUsernameAndPasswordMatch("changePassword", "pass");
        assert userController.doesUsernameAndPasswordMatch("changePassword", "newPass");

        view.logoutUser();
        View.testing = false;
    }
}