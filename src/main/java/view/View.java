package view;

import control.MainController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {
    //TODO show map for each command
    private static View instance;

    private final Scanner SCANNER = new Scanner(System.in);
    private Matcher regexMatcher;
    private String token;

    private boolean isGameOver;
    private boolean isRoundOver;

    private final String[] REGISTER_MENU_COMMANDS = new String[5];
    private final String[] MAIN_MENU_COMMANDS = new String[7];
    private final String[] SCOREBOARD_MENU_COMMANDS = new String[4];
    private final String[] PROFILE_MENU_COMMANDS = new String[7];
    private final String[] IMPORT_EXPORT_MENU_COMMANDS = new String[5];
    private final String[] SHOP_MENU_COMMANDS = new String[5];
    private final String[] DECK_MENU_COMMANDS = new String[19];
    private final String[] DUEL_MENU_COMMANDS = new String[12];
    private final String[] GAME_MENU_COMMANDS = new String[25];

    private final String CARD_SHOW_REGEX = "^card show (.+)$";


    //region Initialization block
    {
        token = "";

        //Register menu valid commands:
        REGISTER_MENU_COMMANDS[0] = "^menu exit$";
        REGISTER_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        REGISTER_MENU_COMMANDS[2] = "^menu show-current$";
        REGISTER_MENU_COMMANDS[3] = "^user create -(u|p|n|-username|-password|-nickname) ([\\S]+)" +
                " -(u|p|n|-username|-password|nickname) ([\\S]+)" +
                " -(u|p|n|-username|-password|nickname) ([\\S]+)$";
        REGISTER_MENU_COMMANDS[4] = "^user login -(u|p|-username|-password) ([\\S]+)" +
                " -(u|p|-username|-password) ([\\S]+)$";

        //Main menu valid commands:
        MAIN_MENU_COMMANDS[0] = "^user logout$";
        MAIN_MENU_COMMANDS[1] = "^menu enter Duel$";
        MAIN_MENU_COMMANDS[2] = "^menu enter Deck$";
        MAIN_MENU_COMMANDS[3] = "^menu enter Scoreboard$";
        MAIN_MENU_COMMANDS[4] = "^menu enter Profile$";
        MAIN_MENU_COMMANDS[5] = "^menu enter Shop$";
        MAIN_MENU_COMMANDS[6] = "^menu enter Import/Export$";

        //Scoreboard menu valid commands:
        SCOREBOARD_MENU_COMMANDS[0] = "^menu exit$";
        SCOREBOARD_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        SCOREBOARD_MENU_COMMANDS[2] = "^menu show-current$";
        SCOREBOARD_MENU_COMMANDS[3] = "^scoreboard show$";

        //Profile menu valid commands:
        PROFILE_MENU_COMMANDS[0] = "^menu exit$";
        PROFILE_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        PROFILE_MENU_COMMANDS[2] = "^menu show-current$";
        PROFILE_MENU_COMMANDS[3] = "^profile change -(n|-nickname) ([\\S]+)$";
        PROFILE_MENU_COMMANDS[4] = "^profile change -(?:p|-password)" +
                " -(c|n|-current|-new) ([\\S]+)" +
                " -(c|n|-current|-new) ([\\S]+)$";
        PROFILE_MENU_COMMANDS[5] = "^profile change -(c|n|-current|-new) ([\\S]+)" +
                " -(?:p|-password)" +
                " -(c|n|-current|-new) ([\\S]+)$";
        PROFILE_MENU_COMMANDS[6] = "^profile change -(c|n|-current|-new) ([\\S]+)" +
                " -(c|n|-current|-new) ([\\S]+)" +
                " -(?:p|-password)$";

        //Import/export menu valid commands:
        IMPORT_EXPORT_MENU_COMMANDS[0] = "^menu exit$";
        IMPORT_EXPORT_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        IMPORT_EXPORT_MENU_COMMANDS[2] = "^menu show-current$";
        IMPORT_EXPORT_MENU_COMMANDS[3] = "^import card (.+)$";
        IMPORT_EXPORT_MENU_COMMANDS[4] = "^export card (.+)$";

        //Shop menu valid commands
        SHOP_MENU_COMMANDS[0] = "^menu exit$";
        SHOP_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        SHOP_MENU_COMMANDS[2] = "^menu show-current$";
        SHOP_MENU_COMMANDS[3] = "^shop buy (.+)$";
        SHOP_MENU_COMMANDS[4] = "^shop show -(a|-all)$";

        //Deck menu valid commands
        DECK_MENU_COMMANDS[0] = "^menu exit$";
        DECK_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        DECK_MENU_COMMANDS[2] = "^menu show-current$";
        DECK_MENU_COMMANDS[3] = "^deck create (.+)$";
        DECK_MENU_COMMANDS[4] = "^deck delete (.+)$";
        DECK_MENU_COMMANDS[5] = "^deck set-activate (.+)$";
        DECK_MENU_COMMANDS[6] = "^deck add-card -(c|d|-card|-deck) (.+) -(c|d|-card|-deck) (.+)(?: -(?:s|-side))$";
        DECK_MENU_COMMANDS[7] = "^deck add-card -(c|d|-card|-deck) (.+)(?: -(?:s|-side)) -(c|d|-card|-deck) (.+)$";
        DECK_MENU_COMMANDS[8] = "^deck add-card(?: -(?:s|-side)) -(c|d|-card|-deck) (.+) -(c|d|-card|-deck) (.+)$";
        DECK_MENU_COMMANDS[17] = "^deck add-card -(c|d|-card|-deck) (.+) -(c|d|-card|-deck) (.+)$";
        DECK_MENU_COMMANDS[9] = "^deck rm-card -(c|d|-card|-deck) (.+) -(c|d|-card|-deck) (.+)(?: -(?:s|-side))$";
        DECK_MENU_COMMANDS[10] = "^deck rm-card -(c|d|-card|-deck) (.+)(?: -(?:s|-side)) -(c|d|-card|-deck) (.+)$";
        DECK_MENU_COMMANDS[11] = "^deck rm-card(?: -(?:s|-side)) -(c|d|-card|-deck) (.+) -(c|d|-card|-deck) (.+)$";
        DECK_MENU_COMMANDS[18] = "^deck rm-card -(c|d|-card|-deck) (.+) -(c|d|-card|-deck) (.+)$";
        DECK_MENU_COMMANDS[12] = "^deck show -(a|-all)$";
        DECK_MENU_COMMANDS[13] = "^deck show -(c|-cards)$";
        DECK_MENU_COMMANDS[14] = "^deck show -(d|-deck) (.+)(?: -(?:s|-side))$";
        DECK_MENU_COMMANDS[15] = "^deck show(?: -(?:s|-side)) -(d|-deck) (.+)$";
        DECK_MENU_COMMANDS[16] = "^deck show -(d|-deck) (.+)$";

        //Duel menu valid commands
        DUEL_MENU_COMMANDS[0] = "^menu exit$";
        DUEL_MENU_COMMANDS[1] = "^menu enter (?:Duel|Deck|Scoreboard|Profile|Shop|Import/Export)$";
        DUEL_MENU_COMMANDS[2] = "^menu show-current$";
        DUEL_MENU_COMMANDS[3] = "^duel -(n|-new)() -(s|-second-player) (\\S+) -(r|-rounds) (\\d+)$";
        DUEL_MENU_COMMANDS[4] = "^duel -(n|-new)() -(r|-rounds) (\\d+) -(s|-second-player) (\\S+)$";
        DUEL_MENU_COMMANDS[5] = "^duel -(s|-second-player) (\\S+) -(n|-new)() -(r|-rounds) (\\d+)$";
        DUEL_MENU_COMMANDS[6] = "^duel -(s|-second-player) -(r|-rounds) (\\d+) -(n|-new)()$";
        DUEL_MENU_COMMANDS[7] = "^duel -(r|-rounds) (\\d+) -(s|-second-player) (\\S+) -(n|-new)()$";
        DUEL_MENU_COMMANDS[8] = "^duel -(r|-rounds) (\\d+) -(n|-new)() -(s|-second-player)$";
        DUEL_MENU_COMMANDS[9] = "^duel -(n|a|-new|-ai)() -(n|a|-new|-ai)() -(r|-rounds) (\\d+)$";
        DUEL_MENU_COMMANDS[10] = "^duel -(n|a|-new|-ai)() -(r|-rounds) (\\d+) -(n|a|-new|-ai)()$";
        DUEL_MENU_COMMANDS[11] = "^duel -(r|-rounds) (\\d+) -(n|a|-new|-ai)() -(n|a|-new|-ai)()$";

        //Game menu valid commands
        GAME_MENU_COMMANDS[0] = "^select -(m|-monster) (\\d+)$";
        GAME_MENU_COMMANDS[1] = "^select -(s|-spell) (\\d+)$";
        GAME_MENU_COMMANDS[2] = "^select -(f|-field)()$";
        GAME_MENU_COMMANDS[3] = "^select -(h|-hand) (\\d+)$";
        GAME_MENU_COMMANDS[4] = "^select -(m|o|-monster|-opponent)() -(m|o|-monster|-opponent) (\\d+)$";
        GAME_MENU_COMMANDS[5] = "^select -(s|o|-spell|-opponent)() -(s|o|-spell|-opponent) (\\d+)$";
        GAME_MENU_COMMANDS[6] = "^select -(f|o|-field|-opponent)() -(f|o|-field|-opponent)()$";
        GAME_MENU_COMMANDS[7] = "^select -d$"; //for removing selection
        GAME_MENU_COMMANDS[8] = "^select .+$"; //for invalid selection
        GAME_MENU_COMMANDS[9] = "^summon$";
        GAME_MENU_COMMANDS[10] = "^set$"; //this command works for monsters and spells and traps
        GAME_MENU_COMMANDS[11] = "^set -(?:p|-position) (attack|defense)$"; //syntax in dock in not correct
        GAME_MENU_COMMANDS[12] = "^flip-summon$";
        GAME_MENU_COMMANDS[13] = "^attack (1|2|3|4|5)$";
        GAME_MENU_COMMANDS[14] = "^attack direct$";
        GAME_MENU_COMMANDS[15] = "^surrender$";
        GAME_MENU_COMMANDS[16] = "^card show -(?:s|-selected)$";
        GAME_MENU_COMMANDS[17] = "^show graveyard$";
        GAME_MENU_COMMANDS[18] = "^next phase$";
        GAME_MENU_COMMANDS[19] = "^activate effect$";
        //Cheat codes in game menu:
        GAME_MENU_COMMANDS[20] = "^select -(?:h|-hand) (.+) -(?:f|-force)$";
        GAME_MENU_COMMANDS[21] = "^select -(?:f|-force) -(?:h|-hand) (.+)$";
        GAME_MENU_COMMANDS[22] = "^increase -(?:l|-LP) (.+)$";
        GAME_MENU_COMMANDS[23] = "^duel set-winner (\\S+)$";
        GAME_MENU_COMMANDS[24] = "^increase --money (\\d+)$";
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

    public String getRequest(String input) {
        // parsing the json string request with JSONObject library
        JSONObject inputObject = new JSONObject(input);
        String requestType = inputObject.getString("Type");
        JSONObject valueObject = inputObject.getJSONObject("Value");

        return switch (requestType) {
            case "Get tribute cards" -> getTributeCards(valueObject);
            case "Get one monster number" -> getOneMonsterNumber();
            case "Print message" -> printMessage(valueObject);
            case "Game is over" -> gameIsOver(valueObject);
            case "Round is over" -> roundIsOver(valueObject);
            case "Ritual summon" -> ritualSummon();
            case "Get tribute cards for ritual summon" -> getTributeForRitualSummon();
            case "Gate Guardian" -> getTributeForGateGuardian();
            case "Terratiger, the Empowered Warrior" -> terratigerTheEmpoweredWarriorEffect();
            default -> error();
        };
    }

    private String terratigerTheEmpoweredWarriorEffect() {
        System.out.println("You can summon another monster card from your hand.\n" +
                "The level of the selected monster must be a maximum of 4.\n" +
                "Write your monster position number from your hand or write Cancel to cancel.");
        JSONObject answerObject = new JSONObject();
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches("Cancel")) {
                answerObject.put("Type", "Cancel");
                break;
            } else if (inputCommand.matches("\\d+")) {
                answerObject.put("Type", "Successful");
                answerObject.put("Position", inputCommand);
                break;
            } else System.out.println("invalid command!");
        }
        return answerObject.toString();
    }

    private String getTributeForGateGuardian() {
        ArrayList<String> positions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String position = getOneMonsterNumber();
            if (position.equals("Cancel")) break;
            positions.add(position);
        }
        JSONObject answerObject = new JSONObject();
        if (positions.size() != 3) {
            answerObject.put("Type", "Cancel");
            return answerObject.toString();
        }
        answerObject.put("Type", "Three card");
        JSONObject value = new JSONObject();
        value.put("First position", positions.get(0));
        value.put("Second position", positions.get(1));
        value.put("Third position", positions.get(2));
        answerObject.put("Value", value);
        return answerObject.toString();
    }

    private String ritualSummon() {
        System.out.println("Write your ritual monster card position in hand to summon");
        JSONObject answerObject = new JSONObject();
        int ritualMonsterNumber = -1;
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches("Cancel")) {
                answerObject.put("Type", "Cancel");
                return answerObject.toString();
            } else if (inputCommand.matches("\\d+") && Integer.parseInt(inputCommand) != 0 && ritualMonsterNumber == -1) {
                ritualMonsterNumber = Integer.parseInt(inputCommand);
            } else if (inputCommand.matches("summon") && ritualMonsterNumber != -1) {
                answerObject.put("Type", "Successful");
                JSONObject messageValue = new JSONObject();
                messageValue.put("Ritual monster number", String.valueOf(ritualMonsterNumber));
                return answerObject.toString();
            } else System.out.println("invalid command!\n" +
                    "you should special summon right now");
        }
    }

    private String getTributeForRitualSummon() {
        JSONObject answerObject = new JSONObject();
        System.out.println("Write tribute monster cards positions\n" +
                "Write Finish when its finished.");
        JSONObject messageValue = new JSONObject();
        ArrayList<Integer> tributeCardNumbers = new ArrayList<>();
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches("Cancel")) {
                answerObject.put("Type", "Cancel");
                return answerObject.toString();
            } else if (inputCommand.matches("Finish")) {
                answerObject.put("Type", "Successful");
                JSONArray tributeArray = new JSONArray();
                for (Integer number : tributeCardNumbers) {
                    tributeArray.put(number);
                }
                messageValue.put("Tribute card numbers", tributeArray);
                answerObject.put("Value", messageValue);
                return answerObject.toString();
            } else if (inputCommand.matches("\\d+")) {
                tributeCardNumbers.add(Integer.parseInt(inputCommand));
            } else System.out.println("invalid command!\n" +
                    "you should special summon right now");
        }
    }

    //region register menu methods
    public void registerMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(REGISTER_MENU_COMMANDS[0])) break; //Program will finish
            else if (inputCommand.matches(REGISTER_MENU_COMMANDS[1])) System.out.println("please login first");
            else if (inputCommand.matches(REGISTER_MENU_COMMANDS[2])) System.out.println("Login Menu");
            else if (inputCommand.matches(REGISTER_MENU_COMMANDS[3]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, REGISTER_MENU_COMMANDS[3], 3)) {
                createNewUser(inputCommand);
            } else if (inputCommand.matches(REGISTER_MENU_COMMANDS[4]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, REGISTER_MENU_COMMANDS[4], 2)) {
                loginUser(inputCommand);
            } else System.out.println("invalid command");
        }
    }

    void createNewUser(String inputCommand) {
        getRegexMatcher(inputCommand, REGISTER_MENU_COMMANDS[3], true);

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

    void loginUser(String inputCommand) {
        getRegexMatcher(inputCommand, REGISTER_MENU_COMMANDS[4], true);

        String username = "";
        String password = "";

        //Finding username and password from command:
        for (int i = 1; i < 5; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-username", "u" -> username = regexMatcher.group(i + 1);
                case "-password", "p" -> password = regexMatcher.group(i + 1);
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
            if (inputCommand.matches(MAIN_MENU_COMMANDS[0])) { //Main logout and back to register menu
                boolean canUserLogout = logoutUser();
                if (canUserLogout) break;
            } else if (inputCommand.matches(MAIN_MENU_COMMANDS[1])) duelMenu();
            else if (inputCommand.matches(MAIN_MENU_COMMANDS[2])) deckMenu();
            else if (inputCommand.matches(MAIN_MENU_COMMANDS[3])) scoreBoardMenu();
            else if (inputCommand.matches(MAIN_MENU_COMMANDS[4])) profileMenu();
            else if (inputCommand.matches(MAIN_MENU_COMMANDS[5])) shopMenu();
            else if (inputCommand.matches(MAIN_MENU_COMMANDS[6])) importExportMenu();
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
            if (inputCommand.matches(DECK_MENU_COMMANDS[0])) break; //Deck exit and back to main menu
            else if (inputCommand.matches(DECK_MENU_COMMANDS[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(DECK_MENU_COMMANDS[2])) System.out.println("Deck");
            else if (inputCommand.matches(DECK_MENU_COMMANDS[3])) preparatoryDeckWorks(inputCommand, "Create deck", 3);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[4])) preparatoryDeckWorks(inputCommand, "Delete deck", 4);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[5]))
                preparatoryDeckWorks(inputCommand, "Set active deck", 5);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[6]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[6], 2))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 6);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[7]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[7], 2))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 7);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[8]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[8], 2))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 8);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[17]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[17], 2))
                addOrDeleteCardFromDeck(inputCommand, "Add card to deck", 17);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[9]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[9], 2))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 9);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[10]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[10], 2))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 10);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[11]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[11], 2))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 11);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[18]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, DECK_MENU_COMMANDS[18], 2))
                addOrDeleteCardFromDeck(inputCommand, "Remove card from deck", 18);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[12])) showAllUserDecks();
            else if (inputCommand.matches(DECK_MENU_COMMANDS[13])) showAllUserCards();
            else if (inputCommand.matches(DECK_MENU_COMMANDS[14])) showDeck(inputCommand, 14);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[15])) showDeck(inputCommand, 15);
            else if (inputCommand.matches(DECK_MENU_COMMANDS[16])) showDeck(inputCommand, 16);
            else if (inputCommand.matches(CARD_SHOW_REGEX)) showCard(inputCommand, "Deck");
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
        getRegexMatcher(inputCommand, DECK_MENU_COMMANDS[commandRegexIndex], true);

        String deckName = regexMatcher.group(1);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", commandType); //Command type will be "Delete deck" or "Create deck" or "Set active deck"
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void addOrDeleteCardFromDeck(String inputCommand, String commandType, int commandRegexIndex) {
        getRegexMatcher(inputCommand, DECK_MENU_COMMANDS[commandRegexIndex], true);

        String deckName = "";
        String cardName = "";
        String deckType = "Main";

        //Finding card name and deck name and deck type from command:
        if ((6 <= commandRegexIndex && 8 >= commandRegexIndex) || (9 <= commandRegexIndex && 11 >= commandRegexIndex))
            deckType = "Side";
        for (int i = 1; i < 5; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-deck", "d" -> deckName = regexMatcher.group(i + 1);
                case "-card", "c" -> cardName = regexMatcher.group(i + 1);
            }
        }

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        value.put("Deck type", deckType);
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
        String activeDeck = (String) controlAnswer.get("Active deck");
        JSONArray otherDecks = (JSONArray) controlAnswer.get("Other deck");

        System.out.println("Deck:\n" +
                "Active deck:\n" +
                activeDeck +
                "Other decks:");
        otherDecks.forEach(System.out::println);
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

    private void showDeck(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, DECK_MENU_COMMANDS[commandRegexIndex], true);

        String deckName = regexMatcher.group(2);
        String deckType = "Main";

        //Finding deck name and deck type from command
        if (commandRegexIndex == 14 || commandRegexIndex == 15)
            deckType = "Side";

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Deck name", deckName);
        value.put("Deck type", deckType);
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
            if (inputCommand.matches(DUEL_MENU_COMMANDS[0])) break; //Duel exit and back to main menu
            else if (inputCommand.matches(DUEL_MENU_COMMANDS[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(DUEL_MENU_COMMANDS[2])) System.out.println("Duel");
            else if (inputCommand.matches(CARD_SHOW_REGEX)) showCard(inputCommand, "Duel");
            else if ((regexIndex = doesInputMatchWithStartDuelWithAnotherPlayerCommand(inputCommand)) != 0) {
                startDuelWithAnotherPlayer(inputCommand, regexIndex);
            } else if ((regexIndex = doesInputMatchWithStartDuelWithAiCommand(inputCommand)) != 0) {
                startDuelWithAi(inputCommand, regexIndex);
            } else System.out.println("invalid command");
        }
    }

    private int doesInputMatchWithStartDuelWithAnotherPlayerCommand(String inputCommand) {
        for (int i = 3; i <= 8; i++) {
            if (inputCommand.matches(DUEL_MENU_COMMANDS[i]))
                return i;
        }
        return 0;
    }

    private void startDuelWithAnotherPlayer(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, DUEL_MENU_COMMANDS[commandRegexIndex], true);

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
        String answerType = (String) controlAnswer.get("Type");
        if (answerType.equals("Successful")) {
            gameMenu(); //Will go to the game menu
        } else {
            String answerValue = (String) controlAnswer.get("Value");
            System.out.println(answerValue);
        }
    }

    private int doesInputMatchWithStartDuelWithAiCommand(String inputCommand) {
        for (int i = 9; i <= 11; i++) {
            if (inputCommand.matches(DUEL_MENU_COMMANDS[i]) && !doesInputCommandHaveRepeatedField(inputCommand, DUEL_MENU_COMMANDS[i], 3))
                return i;
        }
        return 0;
    }

    private void startDuelWithAi(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, DUEL_MENU_COMMANDS[commandRegexIndex], true);

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
        //TODO: go to game menu (maybe i should make new game menu for playing with ai)
        String answerType = (String) controlAnswer.get("Type");
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }
    //endregion

    //region game menu methods
    private void gameMenu() {
        isGameOver = false;
        isRoundOver = false;
        int regexIndex;
        while (true) {
            if (isRoundOver || isGameOver) {
                break;
            }
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(GAME_MENU_COMMANDS[20])) activeCheat(inputCommand, 20);
            else if (inputCommand.matches(GAME_MENU_COMMANDS[21])) activeCheat(inputCommand, 21);
            else if ((regexIndex = doesInputMatchWithSelectCardCommand(inputCommand)) != 0)
                selectCard(inputCommand, regexIndex);
            else if (inputCommand.matches(GAME_MENU_COMMANDS[7])) cancelCardSelection();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[8])) System.out.println("invalid selection");
            else if (inputCommand.matches(GAME_MENU_COMMANDS[9])) summonCard();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[10])) setCard();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[11])) setAttackingOrDefending(inputCommand);
            else if (inputCommand.matches(GAME_MENU_COMMANDS[12])) filipSummon();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[13])) attackToMonster(inputCommand);
            else if (inputCommand.matches(GAME_MENU_COMMANDS[14])) directAttack();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[15])) {
                if (surrender()) break; // means surrender request accepted and the game is over.
            } else if (inputCommand.matches(GAME_MENU_COMMANDS[16])) showSelectedCard();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[17])) showGraveyard();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[18])) goToTheNextPhase();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[19])) activateEffect();
            else if (inputCommand.matches(GAME_MENU_COMMANDS[22])) activeCheat(inputCommand, 22);
            else if (inputCommand.matches(GAME_MENU_COMMANDS[23])) activeCheat(inputCommand, 23);
            else if (inputCommand.matches(CARD_SHOW_REGEX)) showCard(inputCommand, "Game");
            else System.out.println("invalid command");
        }
        if (!isGameOver) {
            //TODO: run cardTransferMenu
            gameMenu();
        }
    }

    private String getOneMonsterNumber() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.equals("Cancel")) return "Cancel";
            else if (inputCommand.equals("1") ||
                    inputCommand.equals("2") ||
                    inputCommand.equals("3") ||
                    inputCommand.equals("4") ||
                    inputCommand.equals("5")) {
                return inputCommand;
            } else System.out.println("invalid command.\nTry again.");
        }
    }

    private int doesInputMatchWithSelectCardCommand(String inputCommand) {
        for (int i = 0; i <= 6; i++) {
            if (inputCommand.matches(GAME_MENU_COMMANDS[i])) {
                if (i < 4) return i;
                else if (!doesInputCommandHaveRepeatedField(inputCommand, GAME_MENU_COMMANDS[i], 2)) return i;
            }
        }
        return 0;
    }

    private void selectCard(String inputCommand, int commandRegexIndex) {
        getRegexMatcher(inputCommand, GAME_MENU_COMMANDS[commandRegexIndex], true);

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
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private String getTributeCards(JSONObject valueObject) {
        int numberOfRequiredCards = Integer.parseInt(valueObject.getString("Number of required cards"));
        JSONObject answerObject = getTributeCardFromUser(numberOfRequiredCards);
        return answerObject.toString();
    }

    private JSONObject getTributeCardFromUser(int numberOfNeededCards) {
        //Getting needed tribute cards
        int[] tributeCardsPosition = new int[2];
        int counter = 0;
        JSONObject value = new JSONObject();
        JSONObject messageToSendToControl = new JSONObject();
        value.put("Token", token);
        while (counter < numberOfNeededCards) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches("^\\d+$")) {
                tributeCardsPosition[counter] = Integer.parseInt(inputCommand);
                counter++;
            } else if (inputCommand.equals("cancel")) {
                System.out.println("The order was canceled");
                messageToSendToControl.put("Type", "Cancel");
                messageToSendToControl.put("Value", value);
                return messageToSendToControl;
            } else System.out.println("invalid command");
        }

        //Making message JSONObject and passing to sendControl function:
        if (numberOfNeededCards == 1) {
            value.put("Position", String.valueOf(tributeCardsPosition[0]));
            messageToSendToControl.put("Type", "One card");
        } else {
            value.put("First position", String.valueOf(tributeCardsPosition[0]));
            value.put("Second position", String.valueOf(tributeCardsPosition[1]));
            messageToSendToControl.put("Type", "Two card");
        }
        messageToSendToControl.put("Value", value);
        return messageToSendToControl;
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
        getRegexMatcher(inputCommand, GAME_MENU_COMMANDS[11], true);

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
        getRegexMatcher(inputCommand, GAME_MENU_COMMANDS[13], true);

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

    private boolean surrender() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Surrender");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        boolean isTheGameOver = false;
        //Survey control JSON message
        String answerType = (String) controlAnswer.get("Type");
        if (answerType.equals("Successful")) isTheGameOver = true;
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
        return isTheGameOver;
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

    private void goToTheNextPhase() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Next phase");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void activateEffect() {
        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Active effect");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

    private void activeCheat(String inputCommand, int regexCommandIndex) {
        getRegexMatcher(inputCommand, GAME_MENU_COMMANDS[regexCommandIndex], true);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Cheat code");

        //Finding cheat type
        //cheat type will be one of this types: 1-"Force increase" 2-"Increase LP" 3-"Set winner" 4-Increase money
        switch (regexCommandIndex) {
            case 20, 21 -> {
                value.put("Type", "Force increase");
                value.put("Card name", regexMatcher.group(1));
            }
            case 22 -> {
                value.put("Type", "Increase LP");
                value.put("Amount", regexMatcher.group(1));
            }
            case 23 -> {
                value.put("Type", "Set winner");
                value.put("Nickname", regexMatcher.group(1));
            }
            case 24 -> {
                value.put("Type", "Increase money");
                value.put("Amount", regexMatcher.group(1));
            }
        }

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
            if (inputCommand.matches(SHOP_MENU_COMMANDS[0])) break; //Shop exit and back to main menu
            else if (inputCommand.matches(SHOP_MENU_COMMANDS[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(SHOP_MENU_COMMANDS[2])) System.out.println("Shop");
            else if (inputCommand.matches(SHOP_MENU_COMMANDS[3])) buyCard(inputCommand);
            else if (inputCommand.matches(SHOP_MENU_COMMANDS[4])) showAllCards();
            else if (inputCommand.matches(CARD_SHOW_REGEX)) showCard(inputCommand, "Deck");
            else System.out.println("invalid command");
        }
    }

    private void buyCard(String inputCommand) {
        getRegexMatcher(inputCommand, SHOP_MENU_COMMANDS[3], true);

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
        JSONArray allCardsInShop = (JSONArray) controlAnswer.get("Value");
        allCardsInShop.forEach(System.out::println);
    }
    //endregion

    //region import/export menu methods
    private void importExportMenu() {
        while (true) {
            String inputCommand = SCANNER.nextLine().trim().replaceAll("(\\s)+", " ");
            if (inputCommand.matches(IMPORT_EXPORT_MENU_COMMANDS[0])) break; //Import/export exit and back to main menu
            else if (inputCommand.matches(IMPORT_EXPORT_MENU_COMMANDS[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(IMPORT_EXPORT_MENU_COMMANDS[2])) System.out.println("Import/Export");
            else if (inputCommand.matches(IMPORT_EXPORT_MENU_COMMANDS[3]))
                importOrExportCard(inputCommand, "Import card", 3);
            else if (inputCommand.matches(IMPORT_EXPORT_MENU_COMMANDS[4]))
                importOrExportCard(inputCommand, "Export card", 4);
            else System.out.println("invalid command");
        }
    }

    private void importOrExportCard(String inputCommand, String typeOfCommand, int regexIndex) {
        getRegexMatcher(inputCommand, IMPORT_EXPORT_MENU_COMMANDS[regexIndex], true);

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
            if (inputCommand.matches(PROFILE_MENU_COMMANDS[0])) break; //Profile exit and back to main menu
            else if (inputCommand.matches(PROFILE_MENU_COMMANDS[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(PROFILE_MENU_COMMANDS[2])) System.out.println("Profile");
            else if (inputCommand.matches(PROFILE_MENU_COMMANDS[3])) changeNickname(inputCommand);
            else if (inputCommand.matches(PROFILE_MENU_COMMANDS[4]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, PROFILE_MENU_COMMANDS[4], 2))
                changePassword(inputCommand, 4);
            else if (inputCommand.matches(PROFILE_MENU_COMMANDS[5]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, PROFILE_MENU_COMMANDS[5], 2))
                changePassword(inputCommand, 5);
            else if (inputCommand.matches(PROFILE_MENU_COMMANDS[6]) &&
                    !doesInputCommandHaveRepeatedField(inputCommand, PROFILE_MENU_COMMANDS[6], 2))
                changePassword(inputCommand, 6);
            else System.out.println("invalid command");
        }
    }

    private void changeNickname(String inputCommand) {
        getRegexMatcher(inputCommand, PROFILE_MENU_COMMANDS[3], true);

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
        getRegexMatcher(inputCommand, PROFILE_MENU_COMMANDS[regexIndex], true);

        String currentPassword = "";
        String newPassword = "";

        //Finding current and new password from command:
        for (int i = 1; i < 5; i += 2) {
            switch (regexMatcher.group(i)) {
                case "-current", "c" -> currentPassword = regexMatcher.group(i + 1);
                case "-new", "n" -> newPassword = regexMatcher.group(i + 1);
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
            if (inputCommand.matches(SCOREBOARD_MENU_COMMANDS[0])) break; //Scoreboard exit and back to main menu
            else if (inputCommand.matches(SCOREBOARD_MENU_COMMANDS[1]))
                System.out.println("menu navigation is not possible");
            else if (inputCommand.matches(SCOREBOARD_MENU_COMMANDS[2])) System.out.println("Scoreboard");
            else if (inputCommand.matches(SCOREBOARD_MENU_COMMANDS[3])) showScoreboard();
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

    //region Control Requests
    private String roundIsOver(JSONObject valueObject) {
        printMessage(valueObject);
        isRoundOver = true;
        return "Do not need request answer";
    }

    private String gameIsOver(JSONObject valueObject) {
        printMessage(valueObject);
        isGameOver = true;
        return "Do not need request answer";
    }

    private String printMessage(JSONObject valueObject) {
        String message = valueObject.getString("Value");
        System.out.println(message);
        return "Do not need request answer";
    }

    private String error() {
        JSONObject answerObject = new JSONObject();
        answerObject.put("Type", "Error");
        answerObject.put("Value", "Invalid Request Type!!!");
        return answerObject.toString();
    }
    //endregion

    /**
     * This method will use in deck, duel, game and shop menu
     * who Call Function String will be some of this:
     * 1- "Game"
     * 2- "Deck"
     * 3- "Shop"
     * 4- "Duel"
     */
    private void showCard(String inputCommand, String whoCallFunction) {
        getRegexMatcher(inputCommand, CARD_SHOW_REGEX, true);

        String cardName = regexMatcher.group(1);

        //Making message JSONObject and passing to sendControl function:
        JSONObject value = new JSONObject();
        value.put("Token", token);
        value.put("Card name", cardName);
        value.put("Request menu", whoCallFunction);
        JSONObject messageToSendToControl = new JSONObject();
        messageToSendToControl.put("Type", "Show card");
        messageToSendToControl.put("Value", value);
        JSONObject controlAnswer = sendRequestToControl(messageToSendToControl);

        //Survey control JSON message
        String answerValue = (String) controlAnswer.get("Value");
        System.out.println(answerValue);
    }

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

