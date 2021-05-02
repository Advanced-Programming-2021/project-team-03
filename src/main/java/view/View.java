package view;

import control.MainController;
import org.json.JSONObject;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {
    //TODO: check scoreboard input type
    //TODO: check deck and card input type
    private static View instance;

    private final Scanner SCANNER = new Scanner(System.in);
    private Matcher regexMatcher;
    private String token;

    private String[] registerMenuCommands = new String[5];
    private String[] mainMenuCommands = new String[7]; //TODO: change array size.
    private String[] scoreboardMenuCommands = new String[4];
    private String[] profileMenuCommands = new String[7];
    private String[] importExportMenuCommands = new String[5];
    private String[] shopMenuCommands = new String[5];
    private String[] deckMenuCommands = new String[16];

    //region Initialization block
    {
        token = "";

        //Register menu valid commands:
        registerMenuCommands[0] = "^menu exit$";
        registerMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        registerMenuCommands[2] = "^menu show-current$";
        registerMenuCommands[3] = "^user create -(u|p|n|-username|-password|-nickname) ([\\S]+)" +
                " -(u|p|n|-username|-password|nickname) ([\\S]+)" +
                " -(u|p|n|-username|-password|nickname) ([\\S]+)$";
        registerMenuCommands[4] = "^user login -(u|p|-username|-password) ([\\S]+)" +
                " -(u|p|-username|-password) ([\\S]+)$";

        //Main menu valid commands:
        mainMenuCommands[0] = "^user logout$";
        mainMenuCommands[1] = "^menu enter Duel$";
        mainMenuCommands[2] = "^menu enter Deck$";
        mainMenuCommands[3] = "^menu enter Scoreboard$";
        mainMenuCommands[4] = "^menu enter Profile$";
        mainMenuCommands[5] = "^menu enter Shop$";
        mainMenuCommands[6] = "^menu enter Import/Export$";

        //Scoreboard menu valid commands:
        scoreboardMenuCommands[0] = "^menu exit$";
        scoreboardMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        scoreboardMenuCommands[2] = "^menu show-current$";
        scoreboardMenuCommands[3] = "^scoreboard show$";

        //Profile menu valid commands:
        profileMenuCommands[0] = "^menu exit$";
        profileMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        profileMenuCommands[2] = "^menu show-current$";
        profileMenuCommands[3] = "^profile change -(n|-nickname) ([\\S]+)$";
        profileMenuCommands[4] = "^profile change -(?:p|-password)" +
                " -(c|n|-current|-new) ([\\S]+)" +
                " -(c|n|-current|-new) ([\\S]+)$";
        profileMenuCommands[5] = "^profile change -(c|n|-current|-new) ([\\S]+)" +
                " -(?:p|-password)" +
                " -(c|n|-current|-new) ([\\S]+)$";
        profileMenuCommands[6] = "^profile change -(c|n|-current|-new) ([\\S]+)" +
                " -(c|n|-current|-new) ([\\S]+)" +
                " -(?:p|-password)$";

        //Import/export menu valid commands:
        importExportMenuCommands[0] = "^menu exit$";
        importExportMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        importExportMenuCommands[2] = "^menu show-current$";
        importExportMenuCommands[3] = "^import card ([\\S]+)$"; //TODO: does card name have space?
        importExportMenuCommands[4] = "^export card ([\\S]+)$"; //TODO: does card name have space?

        //Shop menu valid commands
        shopMenuCommands[0] = "^menu exit$";
        shopMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        shopMenuCommands[2] = "^menu show-current$";
        shopMenuCommands[3] = "^shop buy ([\\S]+)$"; //TODO: does card name have space?
        shopMenuCommands[4] = "^shop show -(a|-all)$";

        //Deck menu valid commands
        deckMenuCommands[0] = "^menu exit$";
        deckMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        deckMenuCommands[2] = "^menu show-current$";
        deckMenuCommands[3] = "^deck create ([\\S]+)$"; //TODO: does deck name have space?
        deckMenuCommands[4] = "^deck delete ([\\S]+)$"; //TODO: does deck name have space?
        deckMenuCommands[5] = "^deck set-activate ([\\S]+)$"; //TODO: does deck name have space?
        deckMenuCommands[6] = "^deck add-card -(c|d|-card|-deck) ([\\S]+) -(c|d|-card|-deck) ([\\S]+)(?: -(s|-side)())?$"; //TODO: does deck or card name have space?
        deckMenuCommands[7] = "^deck add-card -(c|d|-card|-deck) ([\\S]+)(?: -(s|-side)())? -(c|d|-card|-deck) ([\\S]+)$"; //TODO: does deck or card name have space?
        deckMenuCommands[8] = "^deck add-card(?: -(s|-side)())? -(c|d|-card|-deck) ([\\S]+) -(c|d|-card|-deck) ([\\S]+)$"; //TODO: does deck or card name have space?
        deckMenuCommands[9] = "^deck rm-card -(c|d|-card|-deck) ([\\S]+) -(c|d|-card|-deck) ([\\S]+)(?: -(s|-side)())?$"; //TODO: does deck or card name have space?
        deckMenuCommands[10] = "^deck rm-card -(c|d|-card|-deck) ([\\S]+)(?: -(s|-side)())? -(c|d|-card|-deck) ([\\S]+)$"; //TODO: does deck or card name have space?
        deckMenuCommands[11] = "^deck rm-card(?: -(s|-side)())? -(c|d|-card|-deck) ([\\S]+) -(c|d|-card|-deck) ([\\S]+)$"; //TODO: does deck or card name have space?
        deckMenuCommands[12] = "^deck show -(a|-all)$";
        deckMenuCommands[13] = "^deck show -(c|-cards)$";
        deckMenuCommands[14] = "^deck show -(d|-deck) ([\\S]+)(?: -(?:s|-side))?$";
        deckMenuCommands[15] = "^deck show(?: -(?:s|-side))? -(d|-deck) ([\\S]+)$";
    }
    //endregion

    private View() {
    }

    public static View getInstance() {
        if (instance == null)
            instance = new View();
        return instance;
    }

    /**
     * This method will send message string to control and get answer string.
     */
    private JSONObject sendRequestToControl(JSONObject messageToSend) {
        String controlAnswerString = MainController.getInstance().getRequest(messageToSend.toString());
        return new JSONObject(controlAnswerString);
    }

    //region register menu methods
    public void registerMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(registerMenuCommands[0])) break; //Program will finish
            else if (inputCommand.matches(registerMenuCommands[1])) System.out.println("please login first");
            else if (inputCommand.matches(registerMenuCommands[2])) System.out.println("Login Menu");
            else if (inputCommand.matches(registerMenuCommands[3]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, registerMenuCommands[3], 3)) {
                createNewUser(inputCommand);
            } else if (inputCommand.matches(registerMenuCommands[4]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, registerMenuCommands[4], 2)) {
                loginUser(inputCommand);
            } else System.out.println("invalid command");
        }
    }

    private void createNewUser(String inputCommand) {
        getRegexMatcher(inputCommand, registerMenuCommands[3], true);

        String username = null;
        String password = null;
        String nickname = null;

        //Finding username, password and nickname from command:
        for (int i = 1; i < 7; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-username", "u" -> username = regexMatcher.group(i + 1);
                case "-password", "p" -> password = regexMatcher.group(i + 1);
                case "-nickname", "n" -> nickname = regexMatcher.group(i + 1);
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Username", username);
        value.put("Password", password);
        value.put("Nickname", nickname);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Register");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void loginUser(String inputCommand) {
        getRegexMatcher(inputCommand, registerMenuCommands[4], true);

        String username = "";
        String password = "";

        //Finding username and password from command:
        for (int i = 1; i < 7; i += 2) {
            if (regexMatcher.group(i).equals("-username") || regexMatcher.group(i).equals("u")) {
                username = regexMatcher.group(i + 1);
            } else if (regexMatcher.group(i).equals("-password") || regexMatcher.group(i).equals("p")) {
                password = regexMatcher.group(i + 1);
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Username", username);
        value.put("Password", password);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Login");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerType = (String) controlAnswer.get("Type");
        String answerValue = (String) controlAnswer.get("Value");
        if (answerType.equals("Successful")) {
            System.out.println("user logged in successfully!");
            token = answerValue;
            mainMenu(); //This line means user have logged in successfully and should go to the main menu.
        } else System.out.println(answerType);
    }
    //endregion

    //region main menu methods
    private void mainMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(mainMenuCommands[0])) { //Main logout and back to register menu
                boolean canUserLogout = logoutUser();
                if (canUserLogout) break;
            } else if (inputCommand.matches(mainMenuCommands[1])) duelMenu();
            else if (inputCommand.matches(mainMenuCommands[2])) deckMenu();
            else if (inputCommand.matches(mainMenuCommands[3])) scoreBoardMenu();
            else if (inputCommand.matches(mainMenuCommands[4])) profileMenu();
            else if (inputCommand.matches(mainMenuCommands[5])) shopMenu();
            else if (inputCommand.matches(mainMenuCommands[6])) importExportMenu();
                //TODO: some remaining methods
            else System.out.println("invalid command");
        }
    }

    private boolean logoutUser() {
        JSONObject messageToSendToControl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put("Token", token);
        messageToSendToControl.put("Type", "Logout");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);
        String answerType = (String) controlAnswer.get("Type");
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
        return answerType.equals("Successful");
    }
    //endregion

    //region deck menu methods
    private void deckMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(deckMenuCommands[0])) break; //Deck exit and back to main menu
            else if (inputCommand.matches(deckMenuCommands[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(deckMenuCommands[2])) System.out.println("Deck");
            else if (inputCommand.matches(deckMenuCommands[3])) preparatoryDeckWorks(inputCommand, "Crate deck", 3);
            else if (inputCommand.matches(deckMenuCommands[4])) preparatoryDeckWorks(inputCommand, "Delete deck", 4);
            else if (inputCommand.matches(deckMenuCommands[5]))
                preparatoryDeckWorks(inputCommand, "Set active deck", 5);
            else if (inputCommand.matches(deckMenuCommands[6]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, deckMenuCommands[6], 3))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 6);
            else if (inputCommand.matches(deckMenuCommands[7]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, deckMenuCommands[7], 3))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 7);
            else if (inputCommand.matches(deckMenuCommands[8]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, deckMenuCommands[8], 3))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 8);
            else if (inputCommand.matches(deckMenuCommands[9]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, deckMenuCommands[9], 3))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 9);
            else if (inputCommand.matches(deckMenuCommands[10]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, deckMenuCommands[10], 3))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 10);
            else if (inputCommand.matches(deckMenuCommands[11]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, deckMenuCommands[11], 3))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 11);
            else if (inputCommand.matches(deckMenuCommands[12])) showAllUserDecks();
            else if (inputCommand.matches(deckMenuCommands[13])) showAllUserCards();
            else if (inputCommand.matches(deckMenuCommands[14])) showDeck(inputCommand);
            else if (inputCommand.matches(deckMenuCommands[15])) showDeck(inputCommand);
            else System.out.println("invalid command");
        }
    }


    /**
     * This method will do three jobs:
     * 1- Create deck
     * 2- Delete deck
     * 3- Set activate deck
     */
    private void preparatoryDeckWorks(String inputCommand, String commandType, int commandRegexIndex) {
        getRegexMatcher(inputCommand, deckMenuCommands[commandRegexIndex], true);

        String deckName = regexMatcher.group(1);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", commandType); //Command type will be "Delete deck" or "Create deck" or "Set activate deck"
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void addOrDeleteCardFromDeck(String inputCommand, String commandType, int commandRegexIndex) {
        getRegexMatcher(inputCommand, deckMenuCommands[commandRegexIndex], true);

        String deckName = "";
        String cardName = "";
        boolean doesUserWantSideDeck = false;

        //Finding card name and deck name and deck type from command:
        for (int i = 1; i < 7; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-deck", "d" -> deckName = regexMatcher.group(i + 1);
                case "-card", "c" -> cardName = regexMatcher.group(i + 1);
                case "-side", "s" -> doesUserWantSideDeck = true;
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        if (doesUserWantSideDeck) value.put("Deck type", "Side");
        else value.put("Deck type", "Main");
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", commandType); //Command type will be "Add card to deck" or "Delete card from deck"
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void showAllUserDecks() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all decks");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void showAllUserCards() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all player cards");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void showDeck(String inputCommand) {
        getRegexMatcher(inputCommand, deckMenuCommands[14], true);

        String deckName = regexMatcher.group(1);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show deck");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    private void duelMenu() {

    }

    //region shop menu methods
    private void shopMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(shopMenuCommands[0])) break; //Shop exit and back to main menu
            else if (inputCommand.matches(shopMenuCommands[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(shopMenuCommands[2])) System.out.println("Shop");
            else if (inputCommand.matches(shopMenuCommands[3])) buyCard(inputCommand);
            else if (inputCommand.matches(shopMenuCommands[4])) showAllCards();
            else System.out.println("invalid command");
        }
    }

    private void buyCard(String inputCommand) {
        getRegexMatcher(inputCommand, shopMenuCommands[3], true);

        String cardName = regexMatcher.group(1);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Buy card");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void showAllCards() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show all cards in shop");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    //region import/export menu methods
    private void importExportMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(importExportMenuCommands[0])) break; //Import/export exit and back to main menu
            else if (inputCommand.matches(importExportMenuCommands[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(importExportMenuCommands[2])) System.out.println("Import/Export");
            else if (inputCommand.matches(importExportMenuCommands[3]))
                importOrExportCard(inputCommand, "Import card", 3);
            else if (inputCommand.matches(importExportMenuCommands[4]))
                importOrExportCard(inputCommand, "Export card", 4);
            else System.out.println("invalid command");
        }
    }

    private void importOrExportCard(String inputCommand, String typeOfCommand, int regexIndex) {
        getRegexMatcher(inputCommand, importExportMenuCommands[regexIndex], true);

        String cardName = regexMatcher.group(1);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", typeOfCommand);
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    //region profile menu methods
    private void profileMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(profileMenuCommands[0])) break; //Profile exit and back to main menu
            else if (inputCommand.matches(profileMenuCommands[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(profileMenuCommands[2])) System.out.println("Profile");
            else if (inputCommand.matches(profileMenuCommands[3])) changeNickname(inputCommand);
            else if (inputCommand.matches(profileMenuCommands[4]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, profileMenuCommands[4], 2))
                changePassword(inputCommand, 4);
            else if (inputCommand.matches(profileMenuCommands[5]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, profileMenuCommands[5], 2))
                changePassword(inputCommand, 5);
            else if (inputCommand.matches(profileMenuCommands[6]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, profileMenuCommands[6], 2))
                changePassword(inputCommand, 6);
            else System.out.println("invalid command");
        }
    }

    private void changeNickname(String inputCommand) {
        getRegexMatcher(inputCommand, profileMenuCommands[3], true);

        String nickname = regexMatcher.group(2);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Nickname", nickname);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Change nickname");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void changePassword(String inputCommand, int regexIndex) {
        getRegexMatcher(inputCommand, profileMenuCommands[regexIndex], true);

        String currentPassword = "";
        String newPassword = "";

        //Finding current and new password from command:
        for (int i = 1; i < 5; i += 2) {
            if (regexMatcher.group(i).equals("-current") || regexMatcher.group(i).equals("c")) {
                currentPassword = regexMatcher.group(i + 1);
            } else if (regexMatcher.group(i).equals("-new") || regexMatcher.group(i).equals("n")) {
                newPassword = regexMatcher.group(i + 1);
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Current password", currentPassword);
        value.put("New password", newPassword);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Change password");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    //region scoreboard menu methods
    private void scoreBoardMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(scoreboardMenuCommands[0])) break; //Scoreboard exit and back to main menu
            else if (inputCommand.matches(scoreboardMenuCommands[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(scoreboardMenuCommands[2])) System.out.println("Scoreboard");
            else if (inputCommand.matches(scoreboardMenuCommands[3])) showScoreboard();
            else System.out.println("invalid command");
        }
    }

    private void showScoreboard() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Scoreboard");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    /**
     * This method will return true if user input two or more repeated fields and return false if all filed be different.
     */
    private boolean doesInputCommandHaveRepeatedField(String inputCommand, String regex, int numberOfFields) {
        getRegexMatcher(inputCommand, regex, true);
        for (int i = 1; i < numberOfFields * 2 + 1; i = i + 2) {
            for (int j = i + 2; j < numberOfFields * 2 + 1; j = j + 2) {
                String fieldType = regexMatcher.group(i);
                if (!fieldType.equals("")) //For side filed in deck menu (it can be empty)
                    if (fieldType.charAt(0) == regexMatcher.group(j).charAt(0)) return true;
            }
        }
        return false;
    }

    private void getRegexMatcher(String command, String regex, boolean findMatches) {
        regexMatcher = Pattern.compile(regex).matcher(command);
        if (findMatches)
            regexMatcher.find();
    }
}

