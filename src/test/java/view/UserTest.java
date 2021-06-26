package view;

import control.MainController;
import control.UserController;
import control.databaseController.DatabaseException;
import model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void signUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        View.getInstance().createNewUser("user create -u ali1337 -p myverystronpassword$$ -n alinick");

        assert User.getByUsername("ali1337") != null;
        assert userController.doesUsernameAndPasswordMatch("ali1337", "myverystronpassword$$");
        assert !userController.doesUsernameAndPasswordMatch("ali1337", "salam");
    }

    @Test
    void duplicateUsernameSignUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        View.getInstance().createNewUser("user create -u mamadAgha -p mammadmammad@@pass -n mammadnick");
        assert User.getByUsername("mamadAgha") != null;

        MainController.getInstance()
                .getRequest("{\"Type\":\"Register\",\"Value\":{\"Username\":\"mamadAgha\",\"Password\":\"salam\",\"Nickname\":\"mamadAgha123\"}}");
        assert !User.doesNicknameExists("mamadAgha123");
        assert !userController.doesUsernameAndPasswordMatch("mamadAgha", "salam");
    }

    @Test
    void login() {
        MainController mainController = MainController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u ali10login -p ali10loginpass -n ali10nick");
        view.loginUser("user login -u ali10login -p ali10loginpass");
        View.testing = false;

        assert mainController.getOnlineUsers().containsValue("ali10login");
        view.logoutUser();
    }

    @Test
    void failedLogin() {
        MainController mainController = MainController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u ali2login -p ali2loginPass -n ali2nick");

        view.loginUser("user login -u fail -p ali2loginPass");
        assert !mainController.getOnlineUsers().containsValue("ali2login");
        assert !mainController.getOnlineUsers().containsValue("fail");

        view.loginUser("user login -u ali2login -p wrong");
        assert !mainController.getOnlineUsers().containsValue("ali2login");

        view.loginUser("user login -u ali2login -p ali2loginPass");
        assert mainController.getOnlineUsers().containsValue("ali2login");

        view.logoutUser();
        View.testing = false;
    }

    @Test
    void duplicateSignUp() {
        MainController.getInstance();
        View view = View.getInstance();
        View.testing = true;
        view.createNewUser("user create -u ali2login -p ali2loginPass -n ali2nick");
        view.createNewUser("user create -u ali10login -p ali10loginpass -n ali10nick");
        view.createNewUser("user create -u mamadAgha -p mammadmammad@@pass -n mammadnick");
        view.createNewUser("user create -u ali1337 -p myverystronpassword$$ -n alinick");
        View.testing = false;
    }

    @Test
    void logout() {
        MainController mainController = MainController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u ali4logout -p ali2logoutPass -n logout4nick");

        view.loginUser("user login -u ali4logout -p ali2logoutPass");
        assert mainController.getOnlineUsers().containsValue("ali4logout");

        assert view.logoutUser();
        assert !mainController.getOnlineUsers().containsValue("ali4logout");

        View.testing = false;
    }

    @Test
    void databaseDuplicate() {
        MainController mainController = MainController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u databaseDuplicate -p pass -n databaseDuplicate");
        view.createNewUser("user create -u databaseDuplicate2 -p pass -n databaseDuplicate2");


        boolean expectedException = false;
        try {
            User.getByUsername("databaseDuplicate2").setNickname("databaseDuplicate");
        } catch (DatabaseException e) {
            e.printStackTrace();
            expectedException = true;
        }
        assert expectedException;

        Assertions.assertThrows(DatabaseException.class, () ->
                User.getByUsername("databaseDuplicate2").setNickname("databaseDuplicate"));

        View.testing = false;
    }
}