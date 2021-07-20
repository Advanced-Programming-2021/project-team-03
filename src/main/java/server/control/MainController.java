package server.control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import server.control.databaseController.Database;
import server.control.databaseController.DatabaseException;
import server.control.databaseController.MonsterCSV;
import server.control.databaseController.SpellAndTrapCSV;
import server.control.game.GameController;
import server.model.card.Card;
import server.model.card.Monster;
import server.model.card.SpellAndTrap;
import server.model.enums.TrapNames;
import server.model.user.Deck;
import server.model.user.DeckType;
import server.model.user.Message;
import server.model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static server.control.game.GamePhases.*;

public class MainController {
    // this class is responsible for view request and send the feedback to thee view via a Json string

    public static boolean initializing;
    private static MainController mainControllerInstance;
    private final HashMap<String, String> onlineUsers;
    private static final String TOKEN_ERROR = new JSONObject()
            .put("Type", "Error")
            .put("Value", "invalid token!").toString();

    private MainController() {
        onlineUsers = new HashMap<>();

        initializing = true;
        Monster.initialize();
        SpellAndTrap.initialize();
        Database.updateImportedCards();
        Deck.initialize();
        User.initialize();
        initializing = false;
    }

    public static MainController getInstance() {
        if (mainControllerInstance == null)
            mainControllerInstance = new MainController();
        return mainControllerInstance;
    }

    public String getRequest(String input) {
        //this method receives a input string and return a string as an answer
        /* note that the strings are in Json format */

        // parsing the json string request with JSONObject library
        JSONObject inputObject = new JSONObject(input);
        String requestType = inputObject.getString("Type");
        JSONObject valueObject = inputObject.getJSONObject("Value");

        try {
            return switch (requestType) { // Switch cases for responding the request
                case "Register" -> registerRequest(valueObject);
                case "Login" -> loginRequest(valueObject);
                case "Logout" -> logoutRequest(valueObject);
                case "Import card" -> importCardRequest(valueObject);
                case "Export card" -> exportCardRequest(valueObject);
                case "Change nickname" -> changeNicknameRequest(valueObject);
                case "Change password" -> changePasswordRequest(valueObject);
                case "Scoreboard" -> scoreboardRequest();
                case "Create deck" -> createANewDeck(valueObject);
                case "Delete deck" -> deleteDeck(valueObject);
                case "Set active deck" -> setActiveDeck(valueObject);
                case "Add card to deck" -> addCardToDeck(valueObject);
                case "Remove card from deck" -> removeCardFromDeck(valueObject);
                case "Show all decks" -> showAllDecks(valueObject);
                case "Show deck" -> showDeck(valueObject);
                case "Show all player cards" -> showAllPlayerCards(valueObject);
                case "Buy card" -> buyCard(valueObject);
                case "Show card" -> showCard(valueObject);
                case "Show all cards in shop" -> showAllCardsInShop(valueObject);
                case "Cheat code" -> cheatCodes(valueObject);
                case "New duel" -> newDuel(valueObject);
                case "New duel AI" -> newDuelWithAI(valueObject);
                case "Select Card" -> selectCardInGame(valueObject);
                case "Cancel card selection" -> deselectCardInGame(valueObject);
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
                case "Next phase" -> endPhaseCommand(valueObject);
                case "Show map" -> showMap(valueObject);

                //region Graphic requests
                case "Get username by token" -> getUsernameByToken(valueObject);
                case "Get nickname by token" -> getNicknameByToken(valueObject);
                case "Get profile picture number by token" -> getProfileImageNumberByToken(valueObject);
                case "Get card Json" -> getCardJson(valueObject);
                case "Get number of bought card" -> getNumberOfBoughtCard(valueObject);
                case "Import card Json" -> importCardJsonRequest(valueObject);
                case "Get balance by token" -> getBalanceByToken(valueObject);
                case "Get map for graphic" -> getMapForGraphic(valueObject);
                case "Get player turn" -> getPlayerTurn(valueObject);
                case "Show all decks graphic" -> showAllDecksGraphic(valueObject);
                case "Show deck graphic" -> showDeckGraphic(valueObject);
                case "Show deck summary" -> showDeckSummary(valueObject);
                case "Show all user cards" -> showAllUserCards(valueObject);
                case "Get phase" -> getPhase(valueObject);
                case "Show opponent graveyard" -> showOpponentGraveyard(valueObject);
                case "Get Card Type" -> getCardType(valueObject);
                case "Reduce balance" -> reduceBalance(valueObject);
                //endregion

                //region Client requests
                case "Show all messages" -> showAllMessages(valueObject);
                case "Show pinned message" -> showPinnedMessage(valueObject);
                case "Send message" -> sendMessage(valueObject);
                case "Pin message" -> pinMessage(valueObject);
                //endregion

                default -> error();
            };
        } catch (Exception e) {
            return errorAnswer("Error occurred: " + e.getMessage());
        }
    }

    private String pinMessage(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        int messageID = valueObject.getInt("ID");
        if (messageID == 0) {
            Message.pinned = null;
            return successfulAnswer("Message unpinned successfully");
        }
        if (Message.getByID(messageID) == null) return errorAnswer("No message found with this name");
        Message.pinned = Message.getByID(messageID);
        return successfulAnswer("Message pinned successfully");
    }

    private String showPinnedMessage(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        if (Message.pinned == null) return errorAnswer("No pinned message");
        return successfulAnswer(new Gson().toJson(Message.pinned));
    }

    private String sendMessage(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String text = valueObject.getString("Text");
        if (invalidToken(token)) return TOKEN_ERROR;
        new Message(text, onlineUsers.get(token));
        return successfulAnswer("Message sent successfully");
    }

    private String showAllMessages(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        Gson gson = new Gson();
        return successfulAnswer(Message.allMessages.values().stream().map(gson::toJson).toArray());
    }

    private String getCardType(JSONObject valueObject) {
        String cardName = valueObject.getString("Card Name");
        Card card = Card.getCardByName(cardName);
        if (card instanceof Monster)
            return successfulAnswer("Monster");
        if (card instanceof SpellAndTrap)
            return successfulAnswer("Spell");
        return errorAnswer("Unknown card");
    }

    private String showOpponentGraveyard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();

        answerObject.put("Type", "Graveyard")
                .put("Value", GameController.getInstance().getOpponentGraveyard());

        return answerObject.toString();
    }

    private String getPhase(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        return successAnswer(GameController.getInstance().getCurrentPhase().name);
    }

    private String getPlayerTurn(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        return successAnswer(GameController.getInstance().getPlayerByTurn().getUser().getUsername());
    }

    private String getBalanceByToken(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();

        try {
            answerObject.put("Value", User.getByUsername(onlineUsers.get(token)).getBalance());
            answerObject.put("Type", "Success");
        } catch (NullPointerException exception) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "User not found");
        }

        return answerObject.toString();
    }

    private String getMapForGraphic(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();
        try {
            answerObject.put("Map", GameController.getInstance().getMapForGraphic());
            answerObject.put("Type", "Success");
        } catch (NullPointerException exception) {
            return errorAnswer("Unknown Error");
        }

        return answerObject.toString();
    }

    private String getNumberOfBoughtCard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String cardName = valueObject.getString("Card name");

        try {
            return successAnswer(User.getByUsername(onlineUsers.get(token)).getNumberOfCards(cardName));
        } catch (NullPointerException exception) {
            return errorAnswer("User not found");
        }
    }

    private String getProfileImageNumberByToken(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        try {
            return successAnswer(User.getByUsername(onlineUsers.get(token)).getProfileImageID());
        } catch (NullPointerException exception) {
            return errorAnswer("User not found");
        }
    }

    private String getNicknameByToken(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        try {
            return successAnswer(User.getByUsername(onlineUsers.get(token)).getNickname());
        } catch (NullPointerException exception) {
            return errorAnswer("User not found");
        }
    }

    private String getUsernameByToken(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        return successAnswer(onlineUsers.get(token));
    }

    private String showMap(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        return successAnswer(GameController.getInstance().getMap());
    }

    private String endPhaseCommand(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        return successAnswer(GameController.getInstance().endPhase());
    }

    public String sendRequestToView(JSONObject messageToSend) {
        System.out.println(messageToSend);
        /*send the statements of the game to view
         * such as phase name, players' turn, ask for card activation and etc*/

        //TODO
        return null;
    }

    public void sendPrintRequestToView(String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Type", "Print message");

        JSONObject value = new JSONObject();
        value.put("Message", message);
        jsonObject.put("Value", value);
        sendRequestToView(jsonObject);
    }

    private String cheatCodes(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String type = valueObject.getString("Type");

        GameController.getInstance().cheatCode(type, valueObject);
        return successfulAnswer("");
    }

    private String showCard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        Card card = Card.getCardByName(valueObject.getString("Card name"));
        if (card == null) return errorAnswer("Unknown card");
        return successfulAnswer(card.toString());
    }

    private String surrender(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        return successfulAnswer(GameController.getInstance().surrender());
    }

    private String showSelectedCard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("no card is selected yet!");
        if (!GameController.getInstance().canShowSelectedCardToPlayer())
            return errorAnswer("card is not visible!");
        return successfulAnswer(GameController.getInstance().getSelectedCard().toString());
    }

    private String showGraveyard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        return new JSONObject().put("Type", "Graveyard")
                .put("Value", GameController.getInstance().getGraveyard()).toString();
    }

    private String activeEffect(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("no card is selected yet!");
        if (!GameController.getInstance().isSpellCard())
            return errorAnswer("activate effect is only for spell cards!");
        if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN)
            return errorAnswer("you can’t activate an effect on this turn!");
        if (GameController.getInstance().isCardInField()) {
            if (GameController.getInstance().cardAlreadyActivated())
                return errorAnswer("you have already activated this card!");
            if (!GameController.getInstance().canCardActivate())
                return errorAnswer("preparations of this spell are not done yet!");
            if (GameController.getInstance().activateSpellCard())
                return successfulAnswer("spell activated!");
            return new JSONObject().put("Type", "Cancel").put("Value", "Command canceled.").toString();
        }
        if (!GameController.getInstance().canSetSelectedCard())
            return errorAnswer("selected card is not in your filed And you can not set and activate it.");
        if (!GameController.getInstance().doesFieldHaveSpaceForThisCard())
            return errorAnswer("spell card zone is full!");
        if (!GameController.getInstance().canCardActivate())
            return errorAnswer("preparations of this spell are not done yet!");

        GameController.getInstance().setCard();
        if (GameController.getInstance().activateSpellCard())
            return successfulAnswer("spell activated!");
        return new JSONObject().put("Type", "Cancel").put("Value", "Command canceled.").toString();
    }

    private String directAttack(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("no card is selected yet!");
        if (GameController.getInstance().getCurrentPhase() != BATTLE)
            return errorAnswer("can't direct attack in this phase!");
        if (!GameController.getInstance().canAttackWithThisCard())
            return errorAnswer("you can’t attack with this card!");
        if (GameController.getInstance().cardAlreadyAttacked())
            return errorAnswer("This card is already under attack!");
        if (!GameController.getInstance().canAttackDirectly())
            return errorAnswer("You can't attack the opponent directly!");

        int damage = GameController.getInstance().attackDirectlyToTheOpponent();
        return successfulAnswer("You opponent received " + damage + " battle damage");
    }

    private String attack(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String position = valueObject.getString("Position");

        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("No card is selected yet!");
        else if (GameController.getInstance().getCurrentPhase() != BATTLE)
            return errorAnswer("You can't attack in this phase!");
        if (!GameController.getInstance().canAttackWithThisCard())
            return errorAnswer("You can't attack with this card!");
        if (GameController.getInstance().cardAlreadyAttacked())
            return errorAnswer("This card is already under attack!");
        if (!GameController.getInstance().canAttackThisPosition(position))
            return errorAnswer("There is no card to attack here!");

        return successfulAnswer(GameController.getInstance().attack(Integer.parseInt(position)));
    }

    private String flipSummon(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("No card is selected yet!");
        if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN)
            return errorAnswer("Can't flip summon in this phase!");
        if (!GameController.getInstance().canChangeCardPosition())
            return errorAnswer("You can't change the position of this card!");
        if (!GameController.getInstance().canFlipSummon())
            return errorAnswer("You can't flip summon this card!");

        String result = GameController.getInstance().flipSummon();
        if (result.equals("flip summoned successfully!"))
            GameController.getInstance().activeTraps(TrapNames.TRAP_HOLE);
        return successfulAnswer(result);
    }

    private String setPosition(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String position = valueObject.getString("Mode");

        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("No card is selected yet!");
        if (!GameController.getInstance().canChangeCardPosition())
            return errorAnswer("You can't change the position of this card!");
        else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN)
            return errorAnswer("Can't change position in this phase!");
        if (GameController.getInstance().isCardAlreadyInThisPosition(position))
            return errorAnswer("The card is already in the position!");
        if (GameController.getInstance().isCardPositionChangedAlready())
            return errorAnswer("You already changed this card's position this turn!");

        GameController.getInstance().changeCardPosition(position);
        return successfulAnswer("monster card position changed successfully!");
    }

    private String setACard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();
        if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error").put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().canSetSelectedCard()) {
            answerObject.put("Type", "Error").put("Value", "you can’t set this card!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error").put("Value", "action not allowed in this phase!");
        } else if (GameController.getInstance().isCardFieldZoneFull()) {
            answerObject.put("Type", "Error").put("Value", "card zone is full!");
        } else if (!GameController.getInstance().canPlayerSummonOrSetAnotherCard()) {
            answerObject.put("Type", "Error").put("Value", "you already summoned/set on this turn!");
        } else if (GameController.getInstance().isSelectedCardAMonster() && !GameController.getInstance().isThereEnoughCardToTribute()) {
            answerObject.put("Type", "Error").put("Value", "there are not enough cards for tribute!");
        } else {
            String result = GameController.getInstance().setCard();
            answerObject.put("Type", "Successful").put("Value", result);
        }
        return answerObject.toString();
    }

    private String summonACard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();
        if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error").put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().canSummonSelectedCard()) {
            answerObject.put("Type", "Error").put("Value", "you can’t summon this card!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error").put("Value", "action not allowed in this phase!");
        } else if (GameController.getInstance().isCardFieldZoneFull()) {
            answerObject.put("Type", "Error").put("Value", "monster card zone is full!");
        } else if (!GameController.getInstance().canPlayerSummonOrSetAnotherCard()) {
            answerObject.put("Type", "Error").put("Value", "you already summoned/set on this turn!");
        } else if (!GameController.getInstance().isThereEnoughCardToTribute()) {
            answerObject.put("Type", "Error").put("Value", "there are not enough cards for tribute!");
        } else {
            String result = GameController.getInstance().summonCard();
            answerObject.put("Type", "Successful");
            answerObject.put("Value", result);
            if (result.startsWith("summoned successfully")) {
                GameController.getInstance().activeTraps(TrapNames.TRAP_HOLE);
                GameController.getInstance().activeTraps(TrapNames.TORRENTIAL_TRIBUTE);
            }
        }

        return answerObject.toString();
    }

    private String deselectCardInGame(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        if (GameController.getInstance().getSelectedCard() == null)
            return errorAnswer("No card is selected yet!");

        GameController.getInstance().deselectCard();
        return successfulAnswer("Card deselected");
    }

    private String selectCardInGame(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String cardType = valueObject.getString("Type");
        int cardPosition = Integer.parseInt(valueObject.getString("Position"));
        boolean isOpponentCard = valueObject.getString("Owner").equals("Opponent");

        JSONObject answerObject = new JSONObject();
        if (!GameController.getInstance().isCardAddressValid(cardPosition)) {
            answerObject.put("Type", "Error").put("Value", "invalid selection!");
        } else if (!GameController.getInstance().isThereACardInGivenPosition(cardType, cardPosition, isOpponentCard)) {
            answerObject.put("Type", "Error").put("Value", "no card found in the given position!");
        } else {
            GameController.getInstance().selectCard(cardType, cardPosition, isOpponentCard);
            answerObject.put("Type", "Successful").put("Value", "card selected");
        }

        return answerObject.toString();
    }

    private String newDuelWithAI(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        int numberOfRound = Integer.parseInt(valueObject.getString("Rounds number"));

        JSONObject answerObject = new JSONObject();
        if (numberOfRound != 1 && numberOfRound != 3) {
            answerObject.put("Type", "Error").put("Value", "number of rounds is not supported!");
        } else if (User.getByUsername(onlineUsers.get(token)).getActiveDeck() == null) {
            answerObject.put("Type", "Error").put("Value", onlineUsers.get(token) + " has no active deck");
        } else if (!User.getByUsername(onlineUsers.get(token)).getActiveDeck().isDeckValid()) {
            answerObject.put("Type", "Error").put("Value", onlineUsers.get(token) + "’s deck is invalid");
        } else {
            GameController.getInstance().newDuelWithAI(onlineUsers.get(token), numberOfRound);
            answerObject.put("Type", "Successful")
                    .put("Value", "");
        }

        return answerObject.toString();
    }

    private String newDuel(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        String rivalName = valueObject.getString("Second player name");
        int numberOfRound = Integer.parseInt(valueObject.getString("Rounds number"));

        if (User.getByUsername(rivalName) == null)
            errorAnswer("there is no player with this username!");
        if (numberOfRound != 1 && numberOfRound != 3)
            errorAnswer("number of rounds is not supported!");
        if (User.getByUsername(onlineUsers.get(token)).getActiveDeck() == null)
            return errorAnswer(onlineUsers.get(token) + " has no active deck");
        if (User.getByUsername(rivalName).getActiveDeck() == null)
            return errorAnswer(rivalName + " has no active deck");
        if (!User.getByUsername(onlineUsers.get(token)).getActiveDeck().isDeckValid())
            return errorAnswer(onlineUsers.get(token) + "’s deck is invalid");
        if (!User.getByUsername(rivalName).getActiveDeck().isDeckValid())
            return errorAnswer(rivalName + "’s deck is invalid");

        GameController.getInstance().newDuel(onlineUsers.get(token), rivalName, numberOfRound);
        return successfulAnswer("Duel started successfully");
    }

    /**
     * shop Requests
     **/
    private String showAllCardsInShop(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();
        answerObject.put("Type", "Successful");

        JSONArray cardsArray = new JSONArray();
        for (Card card : Monster.getAllMonsters().values()) {
            cardsArray.put(card.getPrice() + ": " + card.getCardName());
        }
        for (Card card : SpellAndTrap.getAllSpellAndTraps().values()) {
            cardsArray.put(card.getPrice() + ": " + card.getCardName());
        }
        answerObject.put("Value", cardsArray);

        return answerObject.toString();
    }

    private String reduceBalance(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        int amount = valueObject.getInt("Amount");
        User user = User.getByUsername(onlineUsers.get(token));

        if (user.getBalance() < amount)
            return errorAnswer(
                    "Not enough balance to pay for this.\n" +
                            "Price: " + amount +
                            "\nYour balance: " + user.getBalance() + " (-" + (amount - user.getBalance()) + ")");
        try {
            user.increaseBalance(-amount);
            return successfulAnswer("User balance successfully changed");
        } catch (DatabaseException e) {
            return errorAnswer(e.errorMessage);
        }
    }

    private String buyCard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String cardName = valueObject.getString("Card name");

        if (!ShopController.getInstance().doesCardExist(cardName))
            return errorAnswer("there is no card with this name in the shop!");
        if (!ShopController.getInstance().doesPlayerHaveEnoughMoney(onlineUsers.get(token), cardName))
            errorAnswer("You don't have enough money to buy this card");

        ShopController.getInstance().buyCard(onlineUsers.get(token), cardName);
        return successfulAnswer(cardName + " successfully purchased and added to your inventory");
    }

    private String showAllPlayerCards(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();
        answerObject.put("Type", "Successful");
        ArrayList<Card> allUsersCards = User.getByUsername(onlineUsers.get(token)).getCards();
        JSONArray cardsArray = new JSONArray();
        for (Card card : allUsersCards) {
            cardsArray.put(card.getCardName() + ": " + card.getDescription());
        }
        answerObject.put("Value", cardsArray);

        return answerObject.toString();
    }

    /**
     * Deck Requests
     **/
    private String showDeck(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String deckName = valueObject.getString("Deck name");
        String deckType = valueObject.getString("Deck type");
        if (Deck.getByDeckName(deckName) == null)
            return errorAnswer("deck with name " + deckName + " does not exist");

        return successfulAnswer(Deck.getByDeckName(deckName).showDeck(DeckType.valueOf(deckType.toUpperCase())));
    }

    private String showDeckSummary(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String deckName = valueObject.getString("Deck name");
        String deckType = valueObject.getString("Deck type");
        if (Deck.getByDeckName(deckName) == null)
            return errorAnswer("deck with name " + deckName + " does not exist");

        return successfulAnswer(Deck.getByDeckName(deckName).showDeckSummary(DeckType.valueOf(deckType.toUpperCase())));
    }

    private String showAllDecksGraphic(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        Deck activeDeck = User.getByUsername(onlineUsers.get(token)).getActiveDeck();
        JsonArray decks = new JsonArray();

        ArrayList<Deck> userDecks = User.getByUsername(onlineUsers.get(token)).getDecks();
        for (Deck deck : userDecks) {
            JSONObject jsonDeck = new JSONObject();
            jsonDeck.put("Name", deck.getDeckName())
                    .put("SideDeckNum", deck.getNumberOfCards(DeckType.SIDE))
                    .put("MainDeckNum", deck.getNumberOfCards(DeckType.MAIN))
                    .put("Valid", deck.isDeckValid())
                    .put("Active", deck == activeDeck);

            decks.add(jsonDeck.toString());
        }
        return successfulAnswer(decks);
    }

    private String showDeckGraphic(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String deckName = valueObject.getString("Deck name");
        Deck deck = Deck.getByDeckName(deckName);

        JSONObject answerObject = new JSONObject();
        if (deck == null)
            return errorAnswer("deck with name " + deckName + " does not exist");

        answerObject.put("Type", "Successful");
        JSONObject deckJson = new JSONObject();
        deckJson.put("Name", deck.getDeckName())
                .put("SideDeckNum", deck.getNumberOfCards(DeckType.SIDE))
                .put("MainDeckNum", deck.getNumberOfCards(DeckType.MAIN))
                .put("Valid", deck.isDeckValid())
                .put("MainDeck", deck.getCardNames(DeckType.MAIN))
                .put("SideDeck", deck.getCardNames(DeckType.SIDE));
        answerObject.put("Deck", deckJson);
        return answerObject.toString();
    }

    private String showAllDecks(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        JSONObject answerObject = new JSONObject();
        Deck activeDeck = User.getByUsername(onlineUsers.get(token)).getActiveDeck();
        answerObject.put("Type", "Successful")
                .put("Active deck", DeckController.getInstance().getUserActiveDeck(onlineUsers.get(token)));

        List<String> otherDecks = User.getByUsername(onlineUsers.get(token)).getDecks().stream()
                .filter(deck -> deck != activeDeck).map(Deck::generalOverview).collect(Collectors.toList());
        answerObject.put("Other deck", otherDecks);

        return answerObject.toString();
    }

    private String showAllUserCards(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        return new JSONObject()
                .put("Type", "Successful").put("Cards",
                        User.getByUsername(onlineUsers.get(token))
                                .getCards().stream().map(Card::getCardName).toArray()).toString();
    }

    private String removeCardFromDeck(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String deckName = valueObject.getString("Deck name");
        String cardName = valueObject.getString("Card name");
        String deckType = valueObject.getString("Deck type");

        if (Deck.getByDeckName(deckName) == null)
            return errorAnswer("deck with name " + deckName + " does not exist");
        if (!Deck.getByDeckName(deckName).doesContainCard(Card.getCardByName(cardName), DeckType.valueOf(deckType.toUpperCase())))
            return errorAnswer("card with name " + cardName + " does not exist in " + deckName + " deck");
        try {
            Deck.getByDeckName(deckName).removeCard(cardName, DeckType.valueOf(deckType.toUpperCase()));
            return successfulAnswer(cardName + " removed from deck successfully!");
        } catch (DatabaseException e) {
            return errorAnswer(e.errorMessage);
        }
    }

    private String addCardToDeck(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String deckName = valueObject.getString("Deck name");
        String cardName = valueObject.getString("Card name");
        DeckType deckType = DeckType.valueOf(valueObject.getString("Deck type").toUpperCase());

        if (Deck.getByDeckName(deckName) == null)
            return errorAnswer("deck with name " + deckName + " does not exist");
        if (Card.getCardByName(cardName) == null)
            return errorAnswer("card with name " + cardName + " does not exist");
        if (!DeckController.getInstance().doesUserHaveAnymoreCard(onlineUsers.get(token), cardName, deckName))
            return errorAnswer("you don't have anymore " + cardName);
        if (Deck.getByDeckName(deckName).isDeckFull(deckType))
            return errorAnswer(deckType.getName() + " is full");
        if (!DeckController.getInstance().canUserAddCardToDeck(deckName, cardName))
            return errorAnswer("There are already three cards with name " + cardName + " in deck " + deckName);

        DeckController.getInstance().addCardToDeck(deckName, deckType, cardName);
        return successfulAnswer("card added to " + deckType.getName() + " successfully!");
    }

    private String setActiveDeck(JSONObject valueObject) {
        String deckName = valueObject.getString("Deck name");
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (Deck.getByDeckName(deckName) == null)
            return errorAnswer("deck with name " + deckName + " does not exist");
        if (!User.getByUsername(onlineUsers.get(token)).getDecks().contains(Deck.getByDeckName(deckName)))
            return errorAnswer("this is not your deck!");

        DeckController.getInstance().setActiveDeck(onlineUsers.get(token), deckName);
        return successfulAnswer("deck activated successfully!");
    }

    private String deleteDeck(JSONObject valueObject) {
        String deckName = valueObject.getString("Deck name");
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (Deck.getByDeckName(deckName) == null)
            return errorAnswer("deck with name " + deckName + " does not exist");

        DeckController.getInstance().deleteDeck(onlineUsers.get(token), deckName);
        return successfulAnswer("deck deleted successfully!");
    }

    private String createANewDeck(JSONObject valueObject) {
        String deckName = valueObject.getString("Deck name");
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        if (Deck.getByDeckName(deckName) != null)
            return errorAnswer("deck with name " + deckName + " already exists");

        DeckController.getInstance().createNewDeck(onlineUsers.get(token), deckName);
        return successfulAnswer("deck created successfully!");
    }

    // Invalid Request Error
    private String error() {
        return errorAnswer("Invalid Request Type!!!");
    }

    private String scoreboardRequest() {
        return successfulAnswer(UserController.getInstance().getScoreBoard());
    }

    private String changePasswordRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String currentPassword = valueObject.getString("Current password");
        String newPassword = valueObject.getString("New password");

        if (!User.getByUsername(onlineUsers.get(token)).doesMatchPassword(currentPassword))
            return errorAnswer("current password is invalid!");
        if (newPassword.equals(currentPassword))
            return errorAnswer("please enter a new password!");

        try {
            User.getByUsername(onlineUsers.get(token)).changePassword(newPassword);
            return successfulAnswer("password changed successfully!");
        } catch (DatabaseException e) {
            return errorAnswer(e.errorMessage);
        }
    }

    private String changeNicknameRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String newNickname = valueObject.getString("Nickname");

        if (User.doesNicknameExists(newNickname))
            return errorAnswer("user with nickname " + newNickname + " already exists");

        UserController.getInstance().changeNickname(onlineUsers.get(token), newNickname);
        return successfulAnswer("nickname changed successfully!");
    }

    private String importCardJsonRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;

        try {
            Database.importCardFromJson(valueObject.getString("Json"));
            return successfulAnswer("Card Imported successfully");
        } catch (Exception e) {
            return errorAnswer("Couldn't import card: " + e.getMessage());
        }
    }

    private String getCardJson(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String cardName = valueObject.getString("Card name");

        if (Card.getCardByName(cardName) == null)
            return errorAnswer("no card found with this name in your database!");

        try {
            Object object;
            Card card = Card.getCardByName(cardName);
            if (card instanceof Monster) object = MonsterCSV.exportMonsterCSV((Monster) card);
            else object = SpellAndTrapCSV.exportSpellAndTrapCSV((SpellAndTrap) card);
            return successfulAnswer(new Gson().toJson(object));
        } catch (Exception e) {
            return errorAnswer("Couldn't export card: " + e.getMessage());
        }
    }

    private String exportCardRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String cardName = valueObject.getString("Card name");

        if (Card.getCardByName(cardName) == null)
            return errorAnswer("No card found with this name in your database!");
        try {
            Database.save(Card.getCardByName(cardName));
            return successfulAnswer("Card exported successfully in " + Database.CARDS_EXPORT_PATH);
        } catch (DatabaseException e) {
            return errorAnswer("Couldn't export this card: " + e.errorMessage);
        }
    }

    private String importCardRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        String cardName = valueObject.getString("Card name");

        try {
            Database.importCard(cardName);
            return successfulAnswer("Card imported successfully!");
        } catch (DatabaseException e) {
            return errorAnswer("Can't import this card:\n" + e.errorMessage);
        }
    }

    private String logoutRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        if (invalidToken(token)) return TOKEN_ERROR;
        onlineUsers.remove(token);
        return successfulAnswer("User logged out successfully!");
    }

    private String loginRequest(JSONObject valueObject) {
        String username = valueObject.getString("Username");
        String password = valueObject.getString("Password");

        if (User.getByUsername(username) == null ||
                !User.getByUsername(username).doesMatchPassword(password))
            return errorAnswer("Username or password is wrong!");

        return successfulAnswer(login(username));
    }

    // logging in the user and put it in the online users hashmap
    private String login(String username) {
        String token = UUID.randomUUID().toString();
        onlineUsers.put(token, username);
        return token;
    }

    private boolean invalidToken(String token) {
        return !onlineUsers.containsKey(token);
    }

    private String registerRequest(JSONObject valueObject) {
        String username = valueObject.getString("Username");
        String password = valueObject.getString("Password");
        String nickname = valueObject.getString("Nickname");

        if (User.getByUsername(username) != null)
            return errorAnswer("User with username " + username + " already exists");
        if (User.doesNicknameExists(nickname))
            return errorAnswer("User with nickname " + nickname + " already exists");
        try {
            new User(username, password, nickname).setStartingCards();
            return successfulAnswer("User created successfully!");
        } catch (DatabaseException e) {
            return errorAnswer(e.errorMessage);
        }
    }

    private String successfulAnswer(Object value) {
        return new JSONObject().put("Type", "Successful")
                .put("Value", value).toString();
    }

    private String successAnswer(Object value) {
        return new JSONObject().put("Type", "Success")
                .put("Value", value).toString();
    }

    private String errorAnswer(Object value) {
        return new JSONObject().put("Type", "Error")
                .put("Value", value).toString();
    }
}
