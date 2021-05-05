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
    private String[] duelMenuCommands = new String[16]; //TODO
    private String[] gameMenuCommands = new String[18]; //TODO

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

        //Duel menu valid commands
        duelMenuCommands[0] = "^menu exit$";
        duelMenuCommands[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        duelMenuCommands[2] = "^menu show-current$";
        duelMenuCommands[3] = "^duel -(n|-new)() -(s|-second-player) (\\S+) -(r|-rounds) (\\d+)$";
        duelMenuCommands[4] = "^duel -(n|-new)() -(r|-rounds) (\\d+) -(s|-second-player) (\\S+)$";
        duelMenuCommands[5] = "^duel -(s|-second-player) (\\S+) -(n|-new)() -(r|-rounds) (\\d+)$";
        duelMenuCommands[6] = "^duel -(s|-second-player) -(r|-rounds) (\\d+) -(n|-new)()$";
        duelMenuCommands[7] = "^duel -(r|-rounds) (\\d+) -(s|-second-player) (\\S+) -(n|-new)()$";
        duelMenuCommands[8] = "^duel -(r|-rounds) (\\d+) -(n|-new)() -(s|-second-player)$";
        duelMenuCommands[9] = "^duel -(n|a|-new|-ai)() -(n|a|-new|-ai)() -(r|-rounds) (\\d+)$";
        duelMenuCommands[10] = "^duel -(n|a|-new|-ai)() -(r|-rounds) (\\d+) -(n|a|-new|-ai)()$";
        duelMenuCommands[11] = "^duel -(r|-rounds) (\\d+) -(n|a|-new|-ai)() -(n|a|-new|-ai)()$";

        //Game menu valid commands
        gameMenuCommands[0] = "^select -(m|-monster) (\\d+)$";
        gameMenuCommands[1] = "^select -(s|-spell) (\\d+)$";
        gameMenuCommands[2] = "^select -(f|-field)()$";
        gameMenuCommands[3] = "^select -(h|-hand) (\\d+)$";
        gameMenuCommands[4] = "^select -(m|o|-monster|-opponent)() -(m|o|-monster|-opponent) (\\d+)$";
        gameMenuCommands[5] = "^select -(s|o|-spell|-opponent)() -(s|o|-spell|-opponent) (\\d+)$";
        gameMenuCommands[6] = "^select -(f|o|-field|-opponent)() -(f|o|-field|-opponent)()$";
        gameMenuCommands[7] = "^select -d$"; //for removing selection
        gameMenuCommands[8] = "^select .+$"; //for invalid selection
        gameMenuCommands[9] = "^summon$";
        gameMenuCommands[10] = "^set$";
        gameMenuCommands[11] = "^set -(?:p|-position) (attack|defense)$"; //syntax in dock in not correct
        gameMenuCommands[12] = "^flip-summon$";
        gameMenuCommands[13] = "^attack (1|2|3|4|5)$";
        gameMenuCommands[14] = "^attack direct$";
        gameMenuCommands[15] = "^surrender$";
        gameMenuCommands[16] = "^card show -(?:s|-selected)$";
        gameMenuCommands[17] = "^show graveyard$";
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
        for (int i = 1; i < 5; i += 2) {
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
        } else System.out.println(answerValue);
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
        // TODO add "Deck type" tag to the request
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

    //region duel menu methods
    private void duelMenu() {
        int regexIndex = 0;
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(duelMenuCommands[0])) break; //Duel exit and back to main menu
            else if (inputCommand.matches(duelMenuCommands[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(duelMenuCommands[2])) System.out.println("Duel");
            else if ((regexIndex = doesInputMatchWithStartDuelWithAnotherPlayerCommand(inputCommand)) != 0) {
                startDuelWithAnotherPlayer(inputCommand, regexIndex);
            } else if ((regexIndex = doesInputMatchWithStartDuelWithAiCommand(inputCommand)) != 0) {
                startDuelWithAi(inputCommand, regexIndex);
            } else System.out.println("invalid command");
        }
    }

    private int doesInputMatchWithStartDuelWithAnotherPlayerCommand(String inputCommand) {
        for (int i = 3; i <= 8; i++) {
            if (inputCommand.matches(duelMenuCommands[i]))
                return i;
        }
        return 0;
    }

    private void startDuelWithAnotherPlayer(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, duelMenuCommands[commandRegexIndex], true);

        String secondPlayerName = "";
        int roundCount = 0;

        //Finding card second player name and rounds number from command:
        for (int i = 1; i < 7; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-second-player", "s" -> secondPlayerName = regexMatcher.group(i + 1);
                case "-rounds", "r" -> roundCount = Integer.parseInt(regexMatcher.group(i + 1));
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Second player name", secondPlayerName);
        value.put("Rounds number", String.valueOf(roundCount));
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "New duel");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        //TODO: go to game menu
        String answerType = (String) controlAnswer.get("Type");
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private int doesInputMatchWithStartDuelWithAiCommand(String inputCommand) {
        for (int i = 9; i <= 11; i++) {
            if (inputCommand.matches(duelMenuCommands[i]) && !doesInputCommandHaveRepeatedField(inputCommand, duelMenuCommands[i], 3))
                return i;
        }
        return 0;
    }

    private void startDuelWithAi(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, duelMenuCommands[commandRegexIndex], true);

        int roundCount = 0;

        //Finding card second player name and rounds number from command:
        for (int i = 1; i < 7; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-rounds", "r" -> roundCount = Integer.parseInt(regexMatcher.group(i + 1));
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Rounds number", String.valueOf(roundCount));
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "New duel with ai");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        //TODO: go to game menu
        String answerType = (String) controlAnswer.get("Type");
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    //region game menu methods
    private void gameMenu() {
        int regexIndex;
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if ((regexIndex = doesInputMatchWithSelectCardCommand(inputCommand)) != 0)
                selectCard(inputCommand, regexIndex);
            else if (inputCommand.matches(gameMenuCommands[7])) cancelCardSelection();
            else if (inputCommand.matches(gameMenuCommands[8])) System.out.println("invalid selection");
            else if (inputCommand.matches(gameMenuCommands[9])) summonCard();
            else if (inputCommand.matches(gameMenuCommands[10])) setCard();
            else if (inputCommand.matches(gameMenuCommands[11])) setAttackingOrDefending(inputCommand);
            else if (inputCommand.matches(gameMenuCommands[12])) filipSummon();
            else if (inputCommand.matches(gameMenuCommands[13])) attackToMonster(inputCommand);
            else if (inputCommand.matches(gameMenuCommands[14])) directAttack();
            else if (inputCommand.matches(gameMenuCommands[15])) surrender();
            else if (inputCommand.matches(gameMenuCommands[16])) showSelectedCard();
            else if (inputCommand.matches(gameMenuCommands[17])) showGraveyard();
            else System.out.println("invalid command");
        }
    }

    private int doesInputMatchWithSelectCardCommand(String inputCommand) {
        for (int i = 0; i <= 6; i++) {
            if (inputCommand.matches(gameMenuCommands[i])) {
                if (i < 4) return i;
                else if (!doesInputCommandHaveRepeatedField(inputCommand, gameMenuCommands[i], 2)) return i;
            }
        }
        return 0;
    }

    private void selectCard(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, gameMenuCommands[commandRegexIndex], true);

        String selectedCardType = "";
        String cardOwner = "Myself";
        int cardPosition = 0;

        //Finding selection card type and card owner and card position from command:
        switch (commandRegexIndex) {
            case 0 -> {
                selectedCardType = "Monster";
                cardPosition = Integer.parseInt(regexMatcher.group(2));
            }
            case 1 -> {
                selectedCardType = "Spell";
                cardPosition = Integer.parseInt(regexMatcher.group(2));
            }
            case 2 -> {
                selectedCardType = "Field"; //card position is 0 in this part but it is not important for control
            }
            case 3 -> {
                selectedCardType = "Hand";
                cardPosition = Integer.parseInt(regexMatcher.group(2));
            }
            case 4 -> {
                selectedCardType = "Monster";
                cardPosition = Integer.parseInt(regexMatcher.group(3));
                cardOwner = "Opponent";
            }
            case 5 -> {
                selectedCardType = "Spell";
                cardPosition = Integer.parseInt(regexMatcher.group(3));
                cardOwner = "Opponent";
            }
            case 6 -> {
                selectedCardType = "Field"; //card position is 0 in this part but it is not important for control
                cardOwner = "Opponent";
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Type", selectedCardType);
        value.put("Position", String.valueOf(cardPosition));
        value.put("Owner", cardOwner);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Select Card");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void cancelCardSelection() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Cancel card selection");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void summonCard() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Summon");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerType = (String) controlAnswer.get("Type");
        String answerValue = (String) controlAnswer.get("Value");
        if (answerType.equals("Error")) System.out.println(answerValue);
        else if (answerValue.equals("Need one tribute")) {
            getTributeCard(1);
        } else if (answerValue.equals("Need two tribute")) {
            getTributeCard(2);
        } else System.out.println(answerValue);
    }

    private void getTributeCard(int numberOfNeededCards) {
        //Getting needed tribute cards
        int[] tributeCardsPosition = new int[2];
        int counter = 0;
        while (counter < numberOfNeededCards) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches("^\\d+$")) {
                tributeCardsPosition[counter] = Integer.parseInt(inputCommand);
                counter++;
            } else if (inputCommand.equals("cancel")) {
                System.out.println("The order was canceled");
                return;
            } else System.out.println("invalid command");
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        if (numberOfNeededCards == 1) {
            value.put("Position", String.valueOf(tributeCardsPosition[0]));
        } else {
            value.put("First position", String.valueOf(tributeCardsPosition[0]));
            value.put("Second position", String.valueOf(tributeCardsPosition[1]));
        }
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Tribute cards");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void setCard() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Set in field");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void setAttackingOrDefending(String inputCommand) {
        getRegexMatcher(inputCommand, gameMenuCommands[11], true);

        //Finding Position mode from command:
        String positionMode = switch (regexMatcher.group(1)) {
            case "attack" -> "Attack";
            case "defense" -> "Defense";
            default -> "";
        };

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Mode", positionMode);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Set position");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void filipSummon() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Flip summon");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void attackToMonster(String inputCommand) {
        getRegexMatcher(inputCommand, gameMenuCommands[13], true);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Position", regexMatcher.group(1));
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Attack");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void directAttack() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Direct attack");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void surrender() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Surrender");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void showSelectedCard() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show selected card");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void showGraveyard() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show graveyard");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

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

