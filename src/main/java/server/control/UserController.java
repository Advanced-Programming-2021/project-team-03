package server.control;

import server.control.databaseController.DatabaseException;
import server.model.user.User;

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

    public void changeNickname(String username, String newNickname) {
        try {
            User.getByUsername(username).setNickname(newNickname);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public String getScoreBoard() {
        ArrayList<User> allUsersSorted = User.getScoreBoard();
        StringBuilder scoreboard = new StringBuilder();

        int rank = 1;
        for (int i = 0; i < allUsersSorted.size(); i++) {
            User user = allUsersSorted.get(i);
            if (i != 0 && user.getScore() < allUsersSorted.get(i - 1).getScore()) rank++;

            scoreboard.append(rank).append("- ")
                    .append(user.getNickname()).append(": ")
                    .append(user.getScore()).append("\n");
        }
        return scoreboard.toString();
    }
}
