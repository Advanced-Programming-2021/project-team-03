package control;

import org.json.JSONObject;


public class MainController { // this class is responsible for view request and send the feedback to thee view via a Json string

    private static MainController mainControllerInstance;

    private MainController() {
    }

    public static MainController getInstance() {
        if (mainControllerInstance == null)
            mainControllerInstance = new MainController();
        return mainControllerInstance;
    }

    public String getRequest(String input) { //this method receives a input string and return a string as an answer
        /* note that this strings are in Json format */
        // TODO parsing analysing and answering the request of view menu for some more requests

        // parsing the json string request with JSONObject library
        JSONObject inputObject = new JSONObject(input);
        String requestType = inputObject.getString("Type");
        JSONObject valueObject = inputObject.getJSONObject("Value");

        return switch (requestType) { // Switch cases for responding the request
            case "Register" -> registerRequest(valueObject);
            case "Login" -> loginRequest(valueObject);
            case "Logout" -> logoutRequest(valueObject);
            case "Import card" -> importCardRequest(valueObject);
            case "Export card" -> exportCardRequest(valueObject);
            case "Change nickname" -> changeNicknameRequest(valueObject);
            case "Change password" -> changePasswordRequest(valueObject);
            case "Scoreboard" -> scoreBoardRequest(valueObject);
            default -> null;
        };
    }

    private String scoreBoardRequest(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String changePasswordRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String currentPassword = valueObject.getString("Current password");
        String newPassword = valueObject.getString("New password");

        // creating the json response object
        JSONObject answerObject = new JSONObject();
        if (isTokenValid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (UserController.getInstance().doesTokenAndPasswordMatch(token, currentPassword)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "current password is invalid!");
        } else if (newPassword.equals(currentPassword)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "please enter a new password!");
        } else {
            UserController.getInstance().changePassword(token, newPassword);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "password changed successfully!");
        }
        return answerObject.toString();
    }

    private String changeNicknameRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String newNickname = valueObject.getString("Nickname");
        // creating the json response object
        JSONObject answerObject = new JSONObject();

        // check possible errors
        if (isTokenValid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (UserController.getInstance().isNicknameExists(newNickname)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "user with nickname " + newNickname + " already exists");
        } else {
            UserController.getInstance().changeNickname(token, newNickname);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "nickname changed successfully!");
        }

        return answerObject.toString();
    }

    private String exportCardRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String cardName = valueObject.getString("Card name");

        // creating the json response object
        JSONObject answerObject = new JSONObject();

        if (isTokenValid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!ImportExportController.getInstance().canExportThisCard(cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "can't export this card!");
        } else {
            ImportExportController.getInstance().exportCard(cardName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "card exports successfully!");
        }

        return answerObject.toString();
    }

    private String importCardRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String cardName = valueObject.getString("Card name");

        // creating the json response object
        JSONObject answerObject = new JSONObject();

        if (isTokenValid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!ImportExportController.getInstance().canImportThisCard(cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "can't import this card!");
        } else {
            ImportExportController.getInstance().importCard(cardName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "card imports successfully!");
        }
        return answerObject.toString();
    }

    private String logoutRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        UserController.getInstance().logout(token);

        // creating the json response object
        JSONObject answerObject = new JSONObject();
        if (isTokenValid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "user logged out successfully!");
        }
        return answerObject.toString();
    }

    private boolean isTokenValid(String token) {
        // TODO
        return false;
    }

    private String loginRequest(JSONObject valueObject) {
        String username = valueObject.getString("Username");
        String password = valueObject.getString("Password");
        // creating the json response object
        JSONObject answerObject = new JSONObject();

        // check possible errors
        if (!UserController.getInstance().isUsernameExists(username) ||
                UserController.getInstance().doesUsernameAndPasswordMatch(username, password)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "Username or password is wrong!");
        } else {
            String token = UserController.getInstance().login(username, password);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", token);
        }

        return answerObject.toString();
    }

    private String registerRequest(JSONObject valueObject) {
        String username = valueObject.getString("Username");
        String password = valueObject.getString("Password");
        String nickname = valueObject.getString("Nickname");
        // creating the json response object
        JSONObject answerObject = new JSONObject();

        // check possible errors
        if (UserController.getInstance().isUsernameExists(username)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "user with username " + username + " already exists");
        } else if (UserController.getInstance().isNicknameExists(nickname)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "user with nickname " + nickname + " already exists");
        } else {
            UserController.getInstance().registerUsername(username, password, nickname);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "user created successfully!");
        }

        return answerObject.toString();
    }
}
