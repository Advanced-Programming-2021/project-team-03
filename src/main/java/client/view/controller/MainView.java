package client.view.controller;

import client.clientconfig.Client;
import client.view.model.ClientUser;
import client.view.model.Message;
import client.view.model.ScoreboardUser;
import client.view.pages.GamePage;
import client.view.pages.GameResultPage;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        String controlAnswerString = Client.getInstance().sendRequestToServer(messageToSend.toString());
        return new JSONObject(controlAnswerString);
    }
    //endregion

    //region RegisterPage
    public JSONObject register(String username, String password, String nickname) {
        return sendRequestToControl(jsonWithType("Register", jsonWithToken()
                .put("Username", username)
                .put("Password", password)
                .put("Nickname", nickname)));
    }
    //endregion


    //region LoginPage
    public JSONObject loginUser(String username, String password) {
        JSONObject controlAnswer = sendRequestToControl(jsonWithType("Login", jsonWithToken()
                .put("Username", username)
                .put("Password", password)));

        String messageType = controlAnswer.getString("Type");
        if (messageType.equals("Successful")) token = controlAnswer.getString("Value");
        return controlAnswer;
    }
    //endregion


    //region MainPage
    public JSONObject logoutUser() {
        return sendRequestToControl(jsonWithType("Logout", jsonWithToken()));
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
        return sendRequestToControl(jsonWithType("Show deck graphic",
                jsonWithToken().put("Deck name", deckName)));
    }

    public JSONObject showDeckSummary(String deckName) {
        return sendRequestToControl(jsonWithType("Show deck summary", jsonWithToken()
                .put("Deck name", deckName)
                .put("Deck type", "Main")));
    }

    private JSONObject preparatoryDeckWorks(String deckName, String commandType) {
        return sendRequestToControl(jsonWithType(commandType, jsonWithToken().put("Deck name", deckName)));
    }

    public JSONObject getUserCards() {
        return sendRequestToControl(jsonWithType("Show all user cards", jsonWithToken()));
    }

    public JSONObject getUserDecks() {
        return sendRequestToControl(jsonWithType("Show all decks graphic", jsonWithToken()));
    }
    //endregion

    public JSONObject getAllCards() {
        return sendRequestToControl(jsonWithType("Show all cards in shop", jsonWithToken()));
    }
    //endregion

    public JSONObject addOrDeleteCardFromDeck(String deckName, String deckType, String cardName, String commandType) {
        return sendRequestToControl(jsonWithType(commandType, jsonWithToken()
                .put("Deck name", deckName)
                .put("Deck type", deckType)
                .put("Card name", cardName)));
    }

    //region ProfileMenuPage
    public String getUsername() {
        JSONObject answer = sendRequestToControl(
                jsonWithType("Get username by token", jsonWithToken()));

        String type = answer.getString("Type");
        String username = answer.getString("Value");
        if (type.equals("Success")) return username;
        else return "Username not found";
    }

    public String getNickname() {
        JSONObject answer = sendRequestToControl(jsonWithType("Get nickname by token", jsonWithToken()));

        String type = answer.getString("Type");
        String nickname = answer.getString("Value");
        if (type.equals("Success")) return nickname;
        else return "Nickname not found";
    }

    public JSONObject changeNickname(String nickname) {
        return sendRequestToControl(jsonWithType("Change nickname", jsonWithToken()
                .put("Nickname", nickname)));
    }

    public JSONObject changePassword(String currentPassword, String newPassword) {
        return sendRequestToControl(jsonWithType("Change password", jsonWithToken()
                .put("Current password", currentPassword)
                .put("New password", newPassword)));
    }
    //endregion

    public int getUserProfileImageNumber() {
        JSONObject answer = sendRequestToControl(
                jsonWithType("Get profile picture number by token", jsonWithToken()));
        try {
            return answer.getInt("Value");
        } catch (Exception e) {
            return 1;
        }
    }

    private boolean isSuccessful(JSONObject answer) {
        return answer.has("Type") && answer.getString("Type").equals("Successful");
    }

    public ClientUser getUserInfo(String username) {
        JSONObject answer = sendRequestToControl(jsonWithType("Get profile public info",
                jsonWithToken().put("Username", username)));
        if (!isSuccessful(answer)) return null;
        answer = answer.getJSONObject("Value");
        ClientUser user = new ClientUser();
        user.nickname = answer.getString("Nickname");
        user.score = answer.getInt("Score");
        user.level = answer.getInt("Level");
        user.username = answer.getString("Username");
        user.profileImageID = answer.getInt("Profile");
        return user;
    }

    public JSONObject replyMessage(String messageText, int repliedMessageID) {
        return sendRequestToControl(jsonWithType("Reply message", jsonWithToken()
                .put("Text", messageText)
                .put("RepliedID", repliedMessageID)));
    }

    public int getProfileImageNumber(String username) {
        JSONObject answer = sendRequestToControl(
                jsonWithType("Get profile picture number by username",
                        jsonWithToken().put("Username", username)));
        try {
            return answer.getInt("Value");
        } catch (Exception e) {
            return 1;
        }
    }
    //endregion

    //region ScoreBoardPage
    public ArrayList<ScoreboardUser> getScoreboardUsers() {
        JSONObject answer = sendRequestToControl(jsonWithType("Scoreboard", jsonWithToken()));

        String scoreBoardString = answer.getString("Value");
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
        return sendRequestToControl(
                jsonWithType("Show card", jsonWithToken().put("Card name", cardName)));
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
        return sendRequestToControl(jsonWithType("Import card Json", jsonWithToken().put("Json", Json)));
    }

    //region DuelMenuPage
    public JSONObject startNewSingleMatch(int numberOfRounds) {
        return sendRequestToControl(jsonWithType("New duel AI", jsonWithToken()
                .put("Rounds number", String.valueOf(numberOfRounds))));
    }
    //endregion

    public JSONObject startNewMultiMatch(int numberOfRounds, String opponentUsername) {
        return sendRequestToControl(jsonWithType("New duel", jsonWithToken()
                .put("Second player name", opponentUsername)
                .put("Rounds number", String.valueOf(numberOfRounds))));
    }
    //endregion

    //region GamePage
    public JSONObject getMap() {
        return sendRequestToControl(jsonWithType("Get map for graphic", jsonWithToken()));
    }

    public boolean isCoinOnStarFace() {
        JSONObject answer = sendRequestToControl(jsonWithType("Get player turn", jsonWithToken()));

        String type = answer.getString("Type");
        String username = answer.getString("Value");
        if (type.equals("Success")) return username.equals(MainView.getInstance().getUsername());
        else return true;
    }

    public JSONObject surrender() {
        return sendRequestToControl(jsonWithType("Surrender", jsonWithToken()));
    }

    public JSONObject goToTheNextPhase() {
        return sendRequestToControl(jsonWithType("Next phase", jsonWithToken()));
    }

    public ArrayList<String> showPlayerGraveyard() {
        JSONObject answer = sendRequestToControl(jsonWithType("Show graveyard", jsonWithToken()));

        ArrayList<String> graveyard = new ArrayList<>();
        String type = answer.getString("Type");
        String answerValue = answer.getString("Value");
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
        JSONObject controlAnswer = sendRequestToControl(
                jsonWithType("Show opponent graveyard", jsonWithToken()));

        String type = controlAnswer.getString("Type");
        ArrayList<String> graveyard = new ArrayList<>();
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
        return sendRequestToControl(jsonWithType("Select Card", jsonWithToken()
                .put("Type", type)
                .put("Position", String.valueOf(position))
                .put("Owner", cardOwner)));
    }

    public JSONObject setCard() {
        return sendRequestToControl(jsonWithType("Set in field", jsonWithToken()));
    }

    public JSONObject summonMonster() {
        return sendRequestToControl(jsonWithType("Summon", jsonWithToken()));
    }

    public JSONObject activeSpell() {
        return sendRequestToControl(jsonWithType("Activate effect", jsonWithToken()));
    }

    public JSONObject filipSummonMonster() {
        return sendRequestToControl(jsonWithType("Flip summon", jsonWithToken()));
    }

    public JSONObject setPosition(String positionMode) {
        return sendRequestToControl(jsonWithType("Set position", jsonWithToken().put("Mode", positionMode)));
    }

    public JSONObject attackToMonster(int position) {
        return sendRequestToControl(jsonWithType("Attack",
                jsonWithToken().put("Position", String.valueOf(position))));
    }

    public JSONObject directAttack() {
        return sendRequestToControl(jsonWithType("Direct attack", jsonWithToken()));
    }
    //endregion

    //region ShopMenuPage
    public int getNumberOfBoughtCard(String cardName) {
        JSONObject answer = sendRequestToControl(jsonWithType("Get number of bought card",
                jsonWithToken().put("Card name", cardName)));
        String type = answer.getString("Type");
        if (type.equals("Success")) return answer.getInt("Value");
        else return 0;
    }

    public int getBalance() {
        JSONObject answer = sendRequestToControl(jsonWithType("Get balance by token", jsonWithToken()));
        String type = answer.getString("Type");
        if (type.equals("Success")) return answer.getInt("Value");
        return 0;
    }

    public JSONObject reduceBalance(int amount) {
        return sendRequestToControl(jsonWithType("Reduce balance", jsonWithToken().put("Amount", amount)));
    }

    public JSONObject buyCard(String cardName) {
        return sendRequestToControl(jsonWithType("Buy card", jsonWithToken().put("Card name", cardName)));
    }

    public String getPhase() {
        return sendRequestToControl(jsonWithType("Get phase", jsonWithToken())).getString("Value");
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

    public String toEnumCase(String string) {
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
        JSONObject value = jsonWithToken();
        //Finding cheat type
        //cheat type will be one of this types: 1-"Force increase" 2-"Increase LP" 3-"Set winner" 4-Increase money
        switch (regexCommandIndex) {
            case 0, 1 -> value.put("Type", "Force increase");
            case 2 -> {
                value.put("Type", "Increase LP");
                value.put("Amount", regexMatcher.group(1));
            }
            case 3 -> value.put("Type", "Set winner");
            case 4 -> {
                value.put("Type", "Increase money");
                value.put("Amount", regexMatcher.group(1));
            }
            case 5 -> value.put("Type", "hesoyam");
        }

        return sendRequestToControl(jsonWithType("Cheat code", value)).get("Value").toString();
    }

    private void getRegexMatcher(String command, String regex, boolean findMatches) {
        regexMatcher = Pattern.compile(regex).matcher(command);
        if (findMatches) regexMatcher.find();
    }
    //end region

    public String getCardType(String cardName) throws Exception {
        JSONObject answer = sendRequestToControl(
                jsonWithType("Get Card Type", jsonWithToken().put("Card Name", cardName)));
        if (answer.getString("Type").equals("Error")) throw new Exception();
        else return answer.getString("Value");
    }


    //region requests
    public String getRequest(String input) {
        JSONObject inputObject = new JSONObject(input);
        String requestType = inputObject.getString("Type");
        JSONObject valueObject = new JSONObject();
        try {
            valueObject = inputObject.getJSONObject("Value");
        } catch (Exception ignored) {
        }

        return switch (requestType) {
            case "Print message" -> printMessage(valueObject);
            case "Game is over" -> gameIsOver(valueObject);
            default -> error();
        };
    }

    private boolean isGameOver;
    private GamePage gamePage;

    private String printMessage(JSONObject valueObject) {
        String message = valueObject.getString("Message");
        if (gamePage != null)
            gamePage.printMessage(message);
        return "Do not need request answer";
    }

    private String gameIsOver(JSONObject valueObject) {
        String message = valueObject.getString("Message");
        GameResultPage.setMessageString(message);
        isGameOver = true;
        return "Do not need request answer";
    }

    private String error() {
        return jsonWithType("Error", "Invalid Request Type!!!").toString();
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGamePage(GamePage gamePage) {
        this.gamePage = gamePage;
    }

    private JSONObject jsonWithType(String type, Object value) {
        return new JSONObject().put("Type", type).put("Value", value);
    }

    private JSONObject jsonWithToken() {
        return new JSONObject().put("Token", token);
    }
    //end region

    public List<Message> getAllMessages() {
        JSONObject answer = sendRequestToControl(jsonWithType("Show all messages", jsonWithToken()));
        Gson gson = new Gson();
        return answer.getJSONArray("Value").toList().stream().map(Object::toString)
                .map(message -> gson.fromJson(message, Message.class)).collect(Collectors.toList());
    }

    public Message getPinnedMessage() {
        JSONObject answer = sendRequestToControl(jsonWithType("Show pinned message", jsonWithToken()));
        if (answer.getString("Type").equals("Successful"))
            return new Gson().fromJson(answer.get("Value").toString(), Message.class);
        return null;
    }

    public JSONObject sendMessage(String text) {
        return sendRequestToControl(jsonWithType("Send message", jsonWithToken().put("Text", text)));
    }

    public JSONObject pinMessage(int messageID) { // enter ID = 0 to unpin the pinned message
        return sendRequestToControl(jsonWithType("Pin message", jsonWithToken().put("ID", messageID)));
    }

    public int onlineUsersNum() {
        return sendRequestToControl(jsonWithType("Get number of online users", jsonWithToken())).getInt("Value");
    }

    public JSONObject deleteMessage(int messageID) {
        return sendRequestToControl(jsonWithType("Delete message", jsonWithToken().put("ID", messageID)));
    }

    public JSONObject editMessage(int messageID, String newText) {
        return sendRequestToControl(jsonWithType("Edit message", jsonWithToken()
                .put("ID", messageID)
                .put("Text", newText)));
    }

    public Image getProfileImageByID(int ID) {
        try {
            return new Image(String.valueOf(getClass().getResource("/assets/profilepictures/"
                    + ID + ".png")));
        } catch (Exception e) {
            return new Image(String.valueOf(getClass().getResource("/assets/profilepictures/1.png")));
        }
    }

    public JSONObject getAllAuctions() {
        return sendRequestToControl(jsonWithType("See all auctions", jsonWithToken()));
    }

    public boolean newAuction(String cardName, String price) {
        JSONObject answer = sendRequestToControl(jsonWithType("Create an auction",
                jsonWithToken().put("Card Name", cardName).put("Price", price)));
        return answer.getString("Type").equals("Success");
    }

    public JSONObject newPrice(String id, String price) {
        return sendRequestToControl(jsonWithType("Bid for auction",
                jsonWithToken().put("ID", id).put("Price", price)));
    }

    public List<String> getOnlineUsers() {
        JSONObject answer =  sendRequestToControl(jsonWithType("Get online users", jsonWithToken()));
        if (!isSuccessful(answer)) return null;
        return answer.getJSONArray("Value").toList().stream().map(Object::toString).collect(Collectors.toList());
    }
}
