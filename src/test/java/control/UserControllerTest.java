package control;

import model.user.User;
import org.junit.jupiter.api.Test;

class UserControllerTest {
    @Test
    void signUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        userController.registerUsername("ali1337", "myverystronpassword$$", "alinick");
        assert User.getByUsername("ali1337") != null;
        assert userController.doesUsernameAndPasswordMatch("ali1337", "myverystronpassword$$");
        assert !userController.doesUsernameAndPasswordMatch("ali1337", "salam");
    }

    @Test
    void duplicateUsernameSignUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        userController.registerUsername("mamadAgha", "mammadmammad@@pass", "mammadnick");
        assert User.getByUsername("mamadAgha") != null;

        MainController.getInstance()
                .getRequest("{\"Type\":\"Register\",\"Value\":{\"Username\":\"mamadAgha\",\"Password\":\"salam\",\"Nickname\":\"mamadAgha123\"}}");
        assert !User.doesNicknameExists("mamadAgha123");
        assert !userController.doesUsernameAndPasswordMatch("mamadAgha", "salam");
    }

    @Test
    void duplicateNicknameSignUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        userController.registerUsername("salam", "salam1pass", "salamNick");
        assert User.getByUsername("salam") != null;
        assert User.doesNicknameExists("salamNick");

        MainController.getInstance()
                .getRequest("{\"Type\":\"Register\",\"Value\":{\"Username\":\"salam2\",\"Password\":\"salam2pass\",\"Nickname\":\"salamNick\"}}");

        assert User.getByUsername("salam2") == null;
    }

    @Test
    void duplicateSignUpTest() {
        duplicateNicknameSignUp();
        duplicateUsernameSignUp();
        signUp();
    }
}