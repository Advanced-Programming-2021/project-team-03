package view.viewcontroller;

import control.MainController;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.json.JSONObject;
import view.viewmodel.ScoreboardUser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    //endregion

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

    public JSONObject showDeck(String deckName) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);

        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Value", value);
        messageToSendToControl.put("Type", "Show deck graphic");
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject showDeckSummary(String deckName) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        value.put("Deck type", "Main");
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Value", value);
        messageToSendToControl.put("Type", "Show deck summary");
        return sendRequestToControl(messageToSendToControl);
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

    public JSONObject getUserCards() {
        JSONObject value = new JSONObject();
        value.put("Token", token);

        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all user cards");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject getUserDecks() {
        JSONObject value = new JSONObject();
        value.put("Token", token);

        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all decks graphic");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
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
        messageToSendToControl.put("Type", commandType);
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
    //endregion

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
    public JSONObject showCard(String cardName) {
        JSONObject value = new JSONObject()
                .put("Token", token)
                .put("Card name", cardName);

        return sendRequestToControl(new JSONObject()
                .put("Type", "Show card")
                .put("Value", value));
    }

    public JSONObject getCardJson(String cardName) {
        JSONObject value = new JSONObject()
                .put("Token", token)
                .put("Card name", cardName);

        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get card Json")
                .put("Value", value);

        return sendRequestToControl(messageToSendToControl);


    }
    //endregion

    public JSONObject importCardJson(String Json) {
        JSONObject value = new JSONObject()
                .put("Token", token)
                .put("Json", Json);

        return sendRequestToControl(new JSONObject()
                .put("Value", value)
                .put("Type", "Import card Json"));
    }

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
    //endregion

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
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get map for graphic");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        return answer;
    }

    public boolean isCoinOnStarFace() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get player turn");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String type = answer.getString("Type");
        String username = answer.getString("Value");
        if (type.equals("Success")) return username.equals(MainView.getInstance().getUsername());
        else return true;
    }

    public JSONObject surrender() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Surrender");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        System.out.println(answer.getString("Type"));
        System.out.println(answer.getString("Value"));
        return answer;
    }

    public JSONObject goToTheNextPhase() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Next phase");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public ArrayList<String> showPlayerGraveyard() {
        ArrayList<String> graveyard = new ArrayList<>();
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show graveyard");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);
        String type = controlAnswer.getString("Type");
        String answerValue = controlAnswer.getString("Value");
        if (!type.equals("Graveyard") || answerValue.equals("graveyard empty!")) {
            return graveyard;
        }
        String[] split = answerValue.split("\\n");
        for (String string : split) {
            int startIndex = string.indexOf(". ") + 2;
            int endIndex = string.indexOf(":") - 1;
            graveyard.add(string.substring(startIndex, endIndex));
        }
        return graveyard;
    }

    public ArrayList<String> showOpponentGraveyard() {
        ArrayList<String> graveyard = new ArrayList<>();
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show opponent graveyard");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);
        String type = controlAnswer.getString("Type");
        String answerValue = controlAnswer.getString("Value");
        if (!type.equals("Graveyard") || answerValue.equals("graveyard empty!")) {
            return graveyard;
        }
        String[] split = answerValue.split("\\n");
        for (String string : split) {
            int startIndex = string.indexOf(". ") + 2;
            int endIndex = string.indexOf(":") - 1;
            graveyard.add(string.substring(startIndex, endIndex));
        }
        return graveyard;
    }

    public JSONObject selectCard(String cardOwner, String type, int position) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Type", type);
        value.put("Position", String.valueOf(position));
        value.put("Owner", cardOwner);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Select Card");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject setCard() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Set in field");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject summonMonster() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Summon");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject activeSpell() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Active effect");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject filipSummonMonster() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Flip summon");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public JSONObject setPosition(String positionMode) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Mode", positionMode);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Set position");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
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
        if (type.equals("Success")) return answer.getInt("Value");
        else return 0;
    }

    public int getBalance() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get balance by token");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        String type = answer.getString("Type");
        if (type.equals("Success")) return answer.getInt("Value");
        return 0;
    }

    public JSONObject buyCard(String cardName) {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Buy card");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl);
    }

    public String getPhase() {
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get phase");
        messageToSendToControl.put("Value", value);
        return sendRequestToControl(messageToSendToControl).getString("Value");
    }
    //endregion

    //region Global Methods
    public boolean alertMaker(JSONObject controlAnswer) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        boolean success = false;
        if (controlAnswer.getString("Type").equals("Successful")) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            success = true;
        }

        alert.setContentText(controlAnswer.getString("Value"));
        alert.show();
        return success;
    }

    private String toEnumCase(String string) {
        return string.toUpperCase()
                .replace(' ', '_')
                .replace('-', '_')
                .replace("'", "")
                .replace(",", "");
    }

    public Image getCardImage(String cardName) {
        CardNames cardEnum;
        String url;
        try {
            cardEnum = CardNames.valueOf(toEnumCase(cardName));
            url = String.valueOf(getClass().getResource("/assets/cards/" + cardEnum.imageName + ".jpg"));
        } catch (IllegalArgumentException e) {
            url = String.valueOf(getClass().getResource("/assets/cards/Default.jpg"));
        }
        return new Image(url);
    }

    public Image getBackgroundImage(String fieldName) {
        BackgroundEnums background = BackgroundEnums.valueOf(toEnumCase(fieldName));
        String url = String.valueOf(getClass().getResource("/assets/field/" + background.backgroundName + ".jpg"));
        return new Image(url);
    }
    //endregion

    //region cheat
    public final String[] GAME_MENU_COMMANDS = new String[6];
    private Matcher regexMatcher;

    {
        GAME_MENU_COMMANDS[0] = "^select -(?:h|-hand) -(?:f|-force)$";
        GAME_MENU_COMMANDS[1] = "^select -(?:f|-force) -(?:h|-hand)$";
        GAME_MENU_COMMANDS[2] = "^increase -(?:l|-LP) (.+)$";
        GAME_MENU_COMMANDS[3] = "^duel set-winner$";
        GAME_MENU_COMMANDS[4] = "^increase --money (\\d+)$";
        GAME_MENU_COMMANDS[5] = "^hesoyam$|^HESOYAM$";
    }

    public String activeCheat(String inputCommand, int regexCommandIndex) {
        getRegexMatcher(inputCommand, GAME_MENU_COMMANDS[regexCommandIndex], true);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Cheat code");

        //Finding cheat type
        //cheat type will be one of this types: 1-"Force increase" 2-"Increase LP" 3-"Set winner" 4-Increase money
        switch (regexCommandIndex) {
            case 0, 1 -> {
                value.put("Type", "Force increase");
            }
            case 2 -> {
                value.put("Type", "Increase LP");
                value.put("Amount", regexMatcher.group(1));
            }
            case 3 -> {
                value.put("Type", "Set winner");
            }
            case 4 -> {
                value.put("Type", "Increase money");
                value.put("Amount", regexMatcher.group(1));
            }
            case 5 -> {
                value.put("Type", "hesoyam");
            }
        }

        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        return (String) controlAnswer.get("Value");
    }

    private void getRegexMatcher(String command, String regex, boolean findMatches) {
        regexMatcher = Pattern.compile(regex).matcher(command);
        if (findMatches) regexMatcher.find();
    }
    //end region

    public String getCardType(String cardName) throws Exception {
        JSONObject value = new JSONObject();
        value.put("Card Name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Get Card Type");
        messageToSendToControl.put("Value", value);
        JSONObject answer = sendRequestToControl(messageToSendToControl);
        if (answer.getString("Value").equals("Card not found"))
            throw new Exception();
        else
            return answer.getString("Value");
    }
}
