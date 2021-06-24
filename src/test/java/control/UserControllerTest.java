package control;

import org.junit.jupiter.api.Test;

class UserControllerTest {
    @Test
    void signUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        userController.registerUsername("ali1337", "myverystronpassword$$", "alinick");
        assert userController.doesUsernameExist("ali1337");
        assert userController.doesUsernameAndPasswordMatch("ali1337", "myverystronpassword$$");
        assert !userController.doesUsernameAndPasswordMatch("ali1337", "salam");
    }

    @Test
    void duplicateUsernameSignUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        userController.registerUsername("mamadAgha", "mammadmammad@@pass", "mammadnick");
        assert userController.doesUsernameExist("mamadAgha");

        MainController.getInstance()
                .getRequest("{\"Type\":\"Register\",\"Value\":{\"Username\":\"mamadAgha\",\"Password\":\"salam\",\"Nickname\":\"mamadAgha123\"}}");
        assert !userController.doesNicknameExist("mamadAgha123");
        assert !userController.doesUsernameAndPasswordMatch("mamadAgha", "salam");
    }

    @Test
    void duplicateNicknameSignUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        userController.registerUsername("salam", "salam1pass", "salamNick");
        assert userController.doesUsernameExist("salam");
        assert userController.doesNicknameExist("salamNick");

        MainController.getInstance()
                .getRequest("{\"Type\":\"Register\",\"Value\":{\"Username\":\"salam2\",\"Password\":\"salam2pass\",\"Nickname\":\"salamNick\"}}");

        assert !userController.doesUsernameExist("salam2");
    }
}