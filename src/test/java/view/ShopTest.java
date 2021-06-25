package view;

import control.MainController;
import control.databaseController.DatabaseException;
import model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShopTest {
    @Test
    void shop() throws DatabaseException {
        MainController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u shop -p pass -n shop");
        User.getByUsername("shop").increaseBalance(-10000);

        view.loginUser("user login -u shop -p pass");

        view.showAllCards();
        view.buyCard("shop buy chert");
        view.buyCard("shop buy Suijin");
        User.getByUsername("shop").increaseBalance(10000);
        view.buyCard("shop buy Suijin");

        view.logoutUser();
        View.testing = false;
    }
}