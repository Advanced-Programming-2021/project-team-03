package control;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class MainController { // this class is responsible for view request and send the feedback to thee view via a Json string

    private static MainController mainControllerInstance;

    private MainController() {
        onlineUsers = new HashMap<>();
    }

    public static MainController getInstance() {
        if (mainControllerInstance == null)
            mainControllerInstance = new MainController();
        return mainControllerInstance;
    }

    private final HashMap<String, String> onlineUsers;

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
            case "Scoreboard" -> scoreBoardRequest();
            case "Show card" -> showCardRequest(valueObject);
            case "Create deck" -> createANewDeck(valueObject);
            case "Delete deck" -> deleteDeck(valueObject);
            case "Set active deck" -> setActiveDeck(valueObject);
            case "Add card to deck" -> addCardToDeck(valueObject);
            case "Remove card from deck" -> removeCardFromDeck(valueObject);
            case "Show all decks" -> showAllDecks(valueObject);
            case "Show deck" -> showDeck(valueObject);
            case "Show all player cards" -> showAllPlayerCards(valueObject);
            case "Buy card" -> buyCard(valueObject);
            case "Show all cards in shop" -> showAllCardsInShop(valueObject);
            case "Cheat code" -> cheatCodes(valueObject);
            case "New duel" -> newDuel(valueObject);
            case "Select Card" -> selectCardInGame(valueObject);
            case "Summon" -> summonACard(valueObject);
            case "Set in field" -> setACard(valueObject);
            case "Set position" -> setPosition(valueObject);
            case "Flip summon" -> flipSummon(valueObject);
            case "Attack" -> attack(valueObject);
            case "Direct attack" -> directAttack(valueObject);
            case "Active effect" -> activeEffect(valueObject);
            case "Show graveyard" -> showGraveyard(valueObject);
            case "Show selected card" -> showSelectedCard(valueObject);
            case "Surrender" -> surrender(valueObject);
            default -> error();
        };
    }

    private String surrender(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showSelectedCard(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showGraveyard(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String activeEffect(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String directAttack(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String attack(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String flipSummon(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String setPosition(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String setACard(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String summonACard(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String selectCardInGame(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String newDuel(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String cheatCodes(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showAllCardsInShop(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String buyCard(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showAllPlayerCards(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showDeck(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showAllDecks(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String removeCardFromDeck(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String addCardToDeck(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String setActiveDeck(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String deleteDeck(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String createANewDeck(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showCardRequest(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String error() {
        JSONObject answerObject = new JSONObject();

        answerObject.put("Type", "Error");
        answerObject.put("Value", "Invalid Request Type!!!");

        return answerObject.toString();
    }

    private String scoreBoardRequest() {
        // returning a json array as a value that holds the score board users information
        JSONObject answerObject = new JSONObject();

        JSONArray scoreBoard = ScoreBoardController.getInstance().getScoreBoard();
        answerObject.put("Value", scoreBoard);

        return answerObject.toString();
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
        } else if (UserController.getInstance().doesUsernameAndPasswordMatch(onlineUsers.get(token), currentPassword)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "current password is invalid!");
        } else if (newPassword.equals(currentPassword)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "please enter a new password!");
        } else {
            UserController.getInstance().changePassword(onlineUsers.get(token), newPassword);
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
            UserController.getInstance().changeNickname(onlineUsers.get(token), newNickname);
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

        // creating the json response object
        JSONObject answerObject = new JSONObject();
        if (isTokenValid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            logout(token);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "user logged out successfully!");
        }

        return answerObject.toString();
    }

    // removing the user from the online users hash map
    private void logout(String token) {
        onlineUsers.remove(token);
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
            String token = login(username);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", token);
        }

        return answerObject.toString();
    }

    // logging in the user and put it in the online users hashmap
    private String login(String username) {
        String token = createRandomStringToken(32);
        onlineUsers.put(token, username);
        return token;
    }

    // function to generate a random string of length n as a token
    private String createRandomStringToken(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz"
                + "!@#$%^&*+-/?";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private boolean isTokenValid(String token) {
        return onlineUsers.containsKey(token);
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

    public HashMap<String, String> getOnlineUsers() {
        return onlineUsers;
    }
}
