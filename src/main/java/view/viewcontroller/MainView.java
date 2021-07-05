package view.viewcontroller;

import control.MainController;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

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
        String username = answer.getString("value");
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
        String nickname = answer.getString("value");
        if (type.equals("Success")) return nickname;
        else return "Nickname not found";
    }
    //endregion
}
