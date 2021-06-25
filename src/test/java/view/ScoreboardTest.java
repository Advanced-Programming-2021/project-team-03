package view;

import control.MainController;
import control.UserController;
import control.databaseController.DatabaseException;
import model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardTest {
    @Test
    void scoreboardTest() throws DatabaseException {
        MainController.getInstance();
        View view = View.getInstance();

        View.testing = true;
        view.createNewUser("user create -u scoreboard -p pass -n scoreboard");
        view.createNewUser("user create -u scoreboard2 -p pass -n scoreboard2");
        view.createNewUser("user create -u scoreboard3 -p pass -n scoreboard3");
        view.createNewUser("user create -u scoreboard4 -p pass -n scoreboard4");
        view.createNewUser("user create -u scoreboard5 -p pass -n scoreboard5");
        view.createNewUser("user create -u scoreboard6 -p pass -n scoreboard6");

        User.getByUsername("scoreboard2").increaseScore(10);
        User.getByUsername("scoreboard3").increaseScore(10);
        User.getByUsername("scoreboard4").increaseScore(1000);
        User.getByUsername("scoreboard5").increaseScore(90);
        User.getByUsername("scoreboard6").increaseScore(10);

        view.loginUser("user login -u scoreboard -p pass");
        view.showScoreboard();
        view.logoutUser();
        View.testing = false;
    }

}