package view;

import control.MainController;
import control.UserController;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void signUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        View.getInstance().createNewUser("user create -u ali1337 -p myverystronpassword$$ -n alinick");

        assert userController.doesUsernameExist("ali1337");
        assert userController.doesUsernameAndPasswordMatch("ali1337", "myverystronpassword$$");
        assert !userController.doesUsernameAndPasswordMatch("ali1337", "salam");
    }

    @Test
    void duplicateUsernameSignUp() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        View.getInstance().createNewUser("user create -u mamadAgha -p mammadmammad@@pass -n mammadnick");
        assert userController.doesUsernameExist("mamadAgha");

        MainController.getInstance()
                .getRequest("{\"Type\":\"Register\",\"Value\":{\"Username\":\"mamadAgha\",\"Password\":\"salam\",\"Nickname\":\"mamadAgha123\"}}");
        assert !userController.doesNicknameExist("mamadAgha123");
        assert !userController.doesUsernameAndPasswordMatch("mamadAgha", "salam");
    }

    @Test
    void login() {
        MainController.getInstance();
        UserController userController = UserController.getInstance();
        View.getInstance().createNewUser("user create -u ali10login -p ali10loginpass -n ali10nick");

        //View.getInstance().loginUser();
    }
}