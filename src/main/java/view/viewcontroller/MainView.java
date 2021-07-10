package view.viewcontroller;

import control.MainController;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewmodel.ScoreboardUser;

import java.util.ArrayList;

public class MainView {
    private static MainView instance;
    private String token;

    private MainView() {
        token = "";
    }

    public static MainView getInstance() {
        if (instance == null) instance = new MainView();
        return instance;
    }

    private JSONObject sendRequestToControl(JSONObject messageToSend) {
        String controlAnswerString = MainController.getInstance().getRequest(messageToSend.toString());
        return new JSONObject(controlAnswerString);
    }

    //region RegisterPage
    public JSONObject register(String username, String password, String nickname) {
        JSONObject value = new JSONObject();
        value.put("Username", username);
        value.put("Password", password);
        value.put("Nickname", nickname);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Register");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }
    //endregion


    //region LoginPage
    public JSONObject loginUser(String username, String password) {
        JSONObject value = new JSONObject();
        value.put("Username", username);
        value.put("Password", password);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Login");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);
        String messageType = controlAnswer.getString("Type");
        if (messageType.equals("Successful")) token = controlAnswer.getString("Value");
        return controlAnswer;
    }
    //endregion


    //region MainPage
    public JSONObject logoutUser() {
        JSONObject messageToSendToControl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put("Token", token);
        messageToSendToControl.put("Type", "Logout");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }
    //endregion


    //region DeckMenuPage
    public JSONObject deleteDeck(String deckName) {
        return preparatoryDeckWorks(deckName, "Delete deck");
    }

    public JSONObject createNewDeck(String deckName) {
        return preparatoryDeckWorks(deckName, "Create deck");
    }

    public JSONObject setActiveDeck(String deckName) {
        return preparatoryDeckWorks(deckName, "Set active deck");
    }

    private JSONObject preparatoryDeckWorks(String deckName, String commandType) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", commandType); //Command type will be "Delete deck" or "Create deck" or "Set active deck"
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public void getUserDecks() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all decks");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String activeDeck = (String) controlAnswer.get("Active deck");
        JSONArray otherDecks = (JSONArray) controlAnswer.get("Other deck");

        System.out.println("Deck:\n" +
                "Active deck:\n" +
                activeDeck +
                "Other decks:");
        otherDecks.forEach(System.out::println);
    }
    //endregion

    public JSONObject getAllCards() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all cards in shop");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }
    //endregion

    public JSONObject addOrDeleteCardFromDeck(String deckName, String deckType, String cardName, String commandType) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        value.put("Deck type", deckType);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", commandType); //Command type will be "Add card to deck" or "Delete card from deck"
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    //region ProfileMenuPage
    public String getUsername() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get username by token");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String type = answer.getString("Type");
        String username = answer.getString("Value");
        if (type.equals("Success")) return username;
        else return "Username not found";
    }

    public String getNickname() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get nickname by token");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String type = answer.getString("Type");
        String nickname = answer.getString("Value");
        if (type.equals("Success")) return nickname;
        else return "Nickname not found";
    }

    public JSONObject changeNickname(String nickname) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Nickname", nickname);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Change nickname");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject changePassword(String currentPassword, String newPassword) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Current password", currentPassword);
        value.put("New password", newPassword);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Change password");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public int getUserProfileImageNumber() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get profile picture number by token");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String type = answer.getString("Type");
        int number = answer.getInt("Value");
        if (type.equals("Success")) return number;
        else return 1;
    }
    //endregion

    //region ScoreBoardPage
    public ArrayList<ScoreboardUser> getScoreboardUsers() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Scoreboard");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String scoreBoardString = answer.getString("Value");
        System.out.println(scoreBoardString);
        String[] users = scoreBoardString.split("\n");
        ArrayList<ScoreboardUser> scoreboard = new ArrayList<>();
        for (String user : users) {
            String rank = user.substring(0, user.indexOf("- "));
            String nickname = user.substring(user.indexOf("- ") + 2, user.indexOf(": "));
            String score = user.substring(user.indexOf(": ") + 2);
            ScoreboardUser scoreboardUser = new ScoreboardUser(Integer.parseInt(rank), nickname, Integer.parseInt(score));
            scoreboard.add(scoreboardUser);
        }
        return scoreboard;
    }
    //endregion

    //region ImportExportMenuPage
    public JSONObject getCardJson(String cardName) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get card Json");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject importExportCard(String cardName, String typeOfCommand) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", typeOfCommand);
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }
    //endregion

    //region DuelMenuPage
    public JSONObject startNewSingleMatch(int numberOfRounds) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Rounds number", String.valueOf(numberOfRounds));
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "New duel AI");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject startNewMultiMatch(int numberOfRounds, String opponentUsername) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Second player name", opponentUsername);
        value.put("Rounds number", String.valueOf(numberOfRounds));
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "New duel");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }
    //endregion

    //region GamePage
    public JSONObject getMap() {
        //TODO
//        JSONObject value = new JSONObject();
//        value.put("Token", token);
//        value.put("Second player name", opponentUsername);
//        value.put("Rounds number", String.valueOf(numberOfRounds));
//        JSONObject messageToSendToControl = new JSONObject();
//        messageToSendToControl.put("Type", "New duel");
//        messageToSendToControl.put("Value", value);
//        return sendRequestToControl(messageToSendToControl);
        return null;
    }
    //endregion

    //region ShopMenuPage
    public int getNumberOfBoughtCard(String cardName) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get number of bought card");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String type = answer.getString("Type");
        int number = answer.getInt("Value");
        if (type.equals("Success")) return number;
        else return 0;
    }

    public void alertMaker(TextField textField, JSONObject controlAnswer) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (controlAnswer.getString("Type").equals("Successful")) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            if (textField != null) textField.clear();
        }

        alert.setContentText(controlAnswer.getString("Value"));
        alert.show();
    }

    private String toEnumCase(String string) {
        return string.toUpperCase()
                .replace(' ', '_')
                .replace('-', '_')
                .replace("'", "")
                .replace(",", "");
    }

    public Image getCardImage(String cardName) throws Exception {
        CardNames cardEnum = CardNames.valueOf(toEnumCase(cardName));
        System.out.println("cardname:               " + cardName + "                " + cardEnum);
        String url = String.valueOf(getClass().getResource("/assets/cards/" + cardEnum.imageName + ".jpg"));
        return new Image(url);
    }

    public static void main(String[] args) {
        try {
            MainView.getInstance().getCardImage("Swords of Revealing Light");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
