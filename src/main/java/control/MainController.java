package control;

import control.game.GameController;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;
import model.user.Deck;
import model.user.DeckType;
import model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;
import view.View;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static control.game.GamePhases.*;

public class MainController {
    // this class is responsible for view request and send the feedback to thee view via a Json string

    private static MainController mainControllerInstance;
    public static boolean initializing;

    private MainController() {
        onlineUsers = new HashMap<>();

        initializing = true;
        Monster.initialize();
        SpellAndTrap.initialize();
        Deck.initialize();
        User.initialize();
        initializing = false;
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
            default -> error();
        };
    }

    private String showMap(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Success");
            answerObject.put("Value", GameController.getInstance().getMap());
        }

        return answerObject.toString();
    }

    private String endPhaseCommand(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Success");
            answerObject.put("Value", GameController.getInstance().endPhase());
        }

        return answerObject.toString();
    }

    public String sendRequestToView(JSONObject messageToSend) {
        //TODO: will be complete in future
        /*send the statements of the game to view
         * such as phase name, players' turn, ask for card activation and etc*/
        return View.getInstance().getRequest(messageToSend.toString());
    }

    public void sendPrintRequestToView(String message) {
        JSONObject messageToSendToView = new JSONObject();
        messageToSendToView.put("Type", "Print message");
        JSONObject value = new JSONObject();
        value.put("Message", message);
        messageToSendToView.put("Value", value);
        sendRequestToView(messageToSendToView);
    }

    private String cheatCodes(JSONObject valueObject) {
        //TODO
        return null;
    }

    private String showCard(JSONObject valueObject) {
        /*show details of a given card*/
        String token = valueObject.getString("Token");
        String cardName = valueObject.getString("Card name");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Successful");
            answerObject.put("Value", Card.getCardByName(cardName).toString());
        }

        return answerObject.toString();
    }

    private String surrender(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Successful");
            answerObject.put("Value", GameController.getInstance().surrender());
        }

        return answerObject.toString();
    }

    private String showSelectedCard(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().canShowSelectedCardToPlayer()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "card is not visible!");
        } else {
            answerObject.put("Type", "Successful");
            answerObject.put("Value", GameController.getInstance().getSelectedCard().toString());
        }
        return answerObject.toString();
    }

    private String showGraveyard(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Graveyard");
            answerObject.put("Value", GameController.getInstance().getGraveyard(onlineUsers.get(token)));
        }
        return answerObject.toString();
    }

    private String activeEffect(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().isSpellCard()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "activate effect is only for spell cards!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t activate an effect on this turn!");
        } else if (GameController.getInstance().isCardInField()) {
            if (GameController.getInstance().cardAlreadyActivated()) {
                answerObject.put("Type", "Error");
                answerObject.put("Value", "you have already activated this card!");
            } else if (!GameController.getInstance().canCardActivate()) {
                answerObject.put("Type", "Error");
                answerObject.put("Value", "preparations of this spell are not done yet!");
            } else {
                boolean success = GameController.getInstance().activateSpellCard();
                if (success) {
                    answerObject.put("Type", "Successful");
                    answerObject.put("Value", "spell activated!");
                } else {
                    answerObject.put("Type", "Cancel");
                    answerObject.put("Value", "Command canceled.");
                }
            }
        } else {
            if (!GameController.getInstance().canSetSelectedCard()) {
                answerObject.put("Type", "Error");
                answerObject.put("Value", "selected card is not in your filed And you can not set and activate it.");
            } else if (!GameController.getInstance().doesFieldHaveSpaceForThisCard()) {
                answerObject.put("Type", "Error");
                answerObject.put("Value", "spell card zone is full!");
            } else if (!GameController.getInstance().canCardActivate()) {
                answerObject.put("Type", "Error");
                answerObject.put("Value", "preparations of this spell are not done yet!");
            } else {
                GameController.getInstance().setCard();
                boolean success = GameController.getInstance().activateSpellCard();
                if (success) {
                    answerObject.put("Type", "Successful");
                    answerObject.put("Value", "spell activated!");
                } else {
                    answerObject.put("Type", "Cancel");
                    answerObject.put("Value", "Command canceled.");
                }
            }
        }
        return answerObject.toString();
    }

    private String directAttack(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (GameController.getInstance().getCurrentPhase() != BATTLE) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "can't direct attack phase!");
        } else if (!GameController.getInstance().canAttackWithThisCard(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t attack with this card!");
        } else if (!GameController.getInstance().cardAlreadyAttacked()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "this card already attacked!");
        } else if (!GameController.getInstance().canAttackDirectly()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t attack the opponent directly!");
        } else {
            int damage = GameController.getInstance().attackDirectlyToTheOpponent();
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "you opponent receives " + damage + " battle damage");
        }

        return answerObject.toString();
    }

    private String attack(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String position = valueObject.getString("Position");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (GameController.getInstance().getCurrentPhase() != BATTLE) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "can't attack in this phase!");
        } else if (!GameController.getInstance().canAttackWithThisCard(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t attack with this card!");
        } else if (!GameController.getInstance().cardAlreadyAttacked()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "this card already attacked!");
        } else if (!GameController.getInstance().canAttackThisPosition(position)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "there is no card to attack here!");
        } else {
            String result = GameController.getInstance().attack(onlineUsers.get(token), Integer.parseInt(position));
            answerObject.put("Type", "Successful");
            answerObject.put("Value", result);
        }

        return answerObject.toString();
    }

    private String flipSummon(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "can't flip summon in this phase!");
        } else if (!GameController.getInstance().canChangeCardPosition()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t change this card position!");
        } else if (!GameController.getInstance().canFlipSummon(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t flip summon this card!");
        } else {
            GameController.getInstance().flipSummon();
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "flip summoned successfully!");
        }
        return answerObject.toString();
    }

    private String setPosition(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String position = valueObject.getString("Mode");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().canChangeCardPosition()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t change this card position!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "can't change position in this phase!");
        } else if (GameController.getInstance().isCardAlreadyInThisPosition(position)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "this card is already in the wanted position!");
        } else if (GameController.getInstance().isCardPositionChangedAlready()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you already changed this card position in this turn!");
        } else {
            GameController.getInstance().changeCardPosition(position);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "monster card position changed successfully!");
        }

        return answerObject.toString();
    }

    private String setACard(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().canSetSelectedCard()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t set this card!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "action not allowed in this phase!");
        } else if (GameController.getInstance().isCardFieldZoneFull()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "card zone is full!");
        } else if (!GameController.getInstance().canPlayerSummonOrSetAnotherCard()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you already summoned/set on this turn!");
        } else {
            String result = GameController.getInstance().setCard();
            answerObject.put("Type", "Successful");
            answerObject.put("Value", result);
        }
        return answerObject.toString();
    }

    private String summonACard(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else if (!GameController.getInstance().canSummonSelectedCard()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you can’t summon this card!");
        } else if (GameController.getInstance().getCurrentPhase() != FIRST_MAIN &&
                GameController.getInstance().getCurrentPhase() != SECOND_MAIN) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "action not allowed in this phase!");
        } else if (GameController.getInstance().isCardFieldZoneFull()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "monster card zone is full!");
        } else if (!GameController.getInstance().canPlayerSummonOrSetAnotherCard()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you already summoned/set on this turn!");
        } else if (!GameController.getInstance().isThereEnoughCardToTribute()) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "there are not enough cards for tribute!");
        } else {
            String result = GameController.getInstance().summonCard();
            answerObject.put("Type", "Successful");
            answerObject.put("Value", result);
        }

        return answerObject.toString();
    }


    private String deselectCardInGame(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (GameController.getInstance().getSelectedCard() == null) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card is selected yet!");
        } else {
            GameController.getInstance().deselectCard();
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "card deselected");
        }

        return answerObject.toString();
    }

    private String selectCardInGame(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String cardType = valueObject.getString("Type");
        int cardPosition = Integer.parseInt(valueObject.getString("Position"));
        boolean isOpponentCard = valueObject.getString("Owner").equals("Opponent");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!GameController.getInstance().isCardAddressValid(cardPosition)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid selection!");
        } else if (!GameController.getInstance().isThereACardInGivenPosition(cardType, cardPosition, isOpponentCard)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "no card found in the given position!");
        } else {
            GameController.getInstance().selectCard(cardType, cardPosition, isOpponentCard);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "card selected");
        }

        return answerObject.toString();
    }

    private String newDuelWithAI(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        int numberOfRound = Integer.parseInt(valueObject.getString("Rounds number"));

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (numberOfRound != 1 && numberOfRound != 3) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "number of rounds is not supported!");
        } else if (!UserController.getInstance().doesPlayerHaveActiveDeck(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", onlineUsers.get(token) + " has no active deck");
        } else if (!UserController.getInstance().isUserActiveDeckValid(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", onlineUsers.get(token) + "’s deck is invalid");
        } else {
            GameController.getInstance().newDuelWithAI(onlineUsers.get(token), numberOfRound);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "Duel starts between " + onlineUsers.get(token) + " & AI");
        }

        return answerObject.toString();
    }

    private String newDuel(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String rivalName = valueObject.getString("Second player name");
        int numberOfRound = Integer.parseInt(valueObject.getString("Rounds number"));

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!UserController.getInstance().doesUsernameExist(rivalName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "there is no player with this username!");
        } else if (numberOfRound != 1 && numberOfRound != 3) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "number of rounds is not supported!");
        } else if (!UserController.getInstance().doesPlayerHaveActiveDeck(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", onlineUsers.get(token) + " has no active deck");
        } else if (!UserController.getInstance().doesPlayerHaveActiveDeck(rivalName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", rivalName + " has no active deck");
        } else if (!UserController.getInstance().isUserActiveDeckValid(onlineUsers.get(token))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", onlineUsers.get(token) + "’s deck is invalid");
        } else if (!UserController.getInstance().isUserActiveDeckValid(rivalName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", rivalName + "’s deck is invalid");
        } else {
            GameController.getInstance().newDuel(onlineUsers.get(token), rivalName, numberOfRound);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "Duel starts between " + onlineUsers.get(token) + " & " + rivalName);
        }

        return answerObject.toString();
    }

    /**
     * shop Requests
     **/
    private String showAllCardsInShop(JSONObject valueObject) {
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Successful");

            JSONArray cardsArray = new JSONArray();
            for (Card card : Monster.getAllMonsters().values()) {
                cardsArray.put(card.getCardName() + ": " + card.getPrice());
            }
            for (Card card : SpellAndTrap.getAllSpellAndTraps().values()) {
                cardsArray.put(card.getCardName() + ": " + card.getPrice());
            }
            answerObject.put("Value", cardsArray);
        }

        return answerObject.toString();
    }

    private String buyCard(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String cardName = valueObject.getString("Card name");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!ShopController.getInstance().doesCardExist(cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "there is no card with this name in the shop!");
        } else if (ShopController.getInstance().doesPlayerHaveEnoughMoney(onlineUsers.get(token), cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "You don't have enough money to buy this card");
        } else {
            ShopController.getInstance().buyCard(onlineUsers.get(token), cardName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", cardName + " successfully purchased and added to your inventory");
        }

        return answerObject.toString();
    }

    private String showAllPlayerCards(JSONObject valueObject) {
        //TODO double check the card representation
        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Successful");
            ArrayList<Card> allUsersCards = DeckController.getInstance().getAllUsersCards(onlineUsers.get(token));
            JSONArray cardsArray = new JSONArray();
            for (Card card : allUsersCards) {
                cardsArray.put(card.getCardName() + ":" + card.getDescription());
            }
            answerObject.put("Value", cardsArray);
        }

        return answerObject.toString();
    }

    /**
     * Deck Requests
     **/
    private String showDeck(JSONObject valueObject) {
        //TODO double check the deck representation
        String token = valueObject.getString("Token");
        String deckName = valueObject.getString("Deck name");
        String deckType = valueObject.getString("Deck type");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!DeckController.getInstance().doesDeckAlreadyExist(deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "deck with name " + deckName + " does not exist");
        } else {
            answerObject.put("Type", "Successful");
            answerObject.put("Value", DeckController.getInstance().getDeck(deckName).showDeck(DeckType.valueOf(deckType.toUpperCase())));
        }

        return answerObject.toString();
    }

    private String showAllDecks(JSONObject valueObject) {
        // TODO double check the other decks representation

        String token = valueObject.getString("Token");

        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else {
            answerObject.put("Type", "Successful");
            answerObject.put("Active deck", DeckController.getInstance().getUserActiveDeck(onlineUsers.get(token)));
            List<String> otherDecks = DeckController.getInstance().getAllUserDecks(onlineUsers.get(token)).stream().map(Deck::generalOverview).collect(Collectors.toList());
            answerObject.put("Other deck", otherDecks);
        }

        return answerObject.toString();
    }

    private String removeCardFromDeck(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String deckName = valueObject.getString("Deck name");
        String cardName = valueObject.getString("Card name");
        String deckType = valueObject.getString("Deck type");

        // answer Json object
        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!DeckController.getInstance().doesDeckAlreadyExist(deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "deck with name " + deckName + " does not exist");
        } else if (!DeckController.getInstance().doesDeckContainThisCard(deckName, DeckType.valueOf(deckType.toUpperCase()), cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "card with name " + cardName + " does not exist in " + deckName + " deck");
        } else {
            DeckController.getInstance().removeCardFromDeck(deckName, DeckType.valueOf(deckType.toUpperCase()), cardName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", cardName + " removed form deck successfully!");
        }

        return answerObject.toString();
    }

    private String addCardToDeck(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String deckName = valueObject.getString("Deck name");
        String cardName = valueObject.getString("Card name");
        String deckType = valueObject.getString("Deck type");

        // answer Json object
        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!DeckController.getInstance().doesDeckAlreadyExist(deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "deck with name " + deckName + " does not exist");
        } else if (!DeckController.getInstance().doesCardExist(cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "card with name " + cardName + " does not exist");
        } else if (!DeckController.getInstance().doesUserHaveAnymoreCard(onlineUsers.get(token), cardName, deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "you don't have anymore " + cardName);
        } else if (DeckController.getInstance().isDeckFull(deckName, DeckType.valueOf(deckType.toUpperCase()))) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", deckType + " deck is full");
        } else if (!DeckController.getInstance().canUserAddCardToDeck(deckName, cardName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "There are already three cards with name " + cardName + " in deck " + deckName);
        } else {
            DeckController.getInstance().addCardToDeck(deckName, DeckType.valueOf(deckType.toUpperCase()), cardName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "card added to " + deckType + " deck successfully!");
        }

        return answerObject.toString();
    }

    private String setActiveDeck(JSONObject valueObject) {
        String deckName = valueObject.getString("Deck name");
        String token = valueObject.getString("Token");

        // answer Json object
        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!DeckController.getInstance().doesDeckAlreadyExist(deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "deck with name " + deckName + " does not exist");
        } else {
            DeckController.getInstance().setActiveDeck(onlineUsers.get(token), deckName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "deck activated successfully!");
        }

        return answerObject.toString();
    }

    private String deleteDeck(JSONObject valueObject) {
        String deckName = valueObject.getString("Deck name");
        String token = valueObject.getString("Token");

        // answer Json object
        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!DeckController.getInstance().doesDeckAlreadyExist(deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "deck with name " + deckName + " does not exist");
        } else {
            DeckController.getInstance().deleteDeck(onlineUsers.get(token), deckName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "deck deleted successfully!");
        }

        return answerObject.toString();
    }

    private String createANewDeck(JSONObject valueObject) {
        String deckName = valueObject.getString("Deck name");
        String token = valueObject.getString("Token");

        // answer Json object
        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (DeckController.getInstance().doesDeckAlreadyExist(deckName)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "deck with name " + deckName + " already exists");
        } else {
            DeckController.getInstance().createNewDeck(onlineUsers.get(token), deckName);
            answerObject.put("Type", "Successful");
            answerObject.put("Value", "deck created successfully!");
        }

        return answerObject.toString();
    }

    // Invalid Deck Error
    private String error() {
        JSONObject answerObject = new JSONObject();

        answerObject.put("Type", "Error");
        answerObject.put("Value", "Invalid Request Type!!!");

        return answerObject.toString();
    }

    private String scoreboardRequest() {
        // returning a json array as a value that holds the score board users information
        JSONObject answerObject = new JSONObject();

        String scoreboard = UserController.getInstance().getAllUsersForUsername();
        answerObject.put("Value", scoreboard);

        return answerObject.toString();
    }


    private String changePasswordRequest(JSONObject valueObject) {
        String token = valueObject.getString("Token");
        String currentPassword = valueObject.getString("Current password");
        String newPassword = valueObject.getString("New password");

        // creating the json response object
        JSONObject answerObject = new JSONObject();
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (!UserController.getInstance().doesUsernameAndPasswordMatch(onlineUsers.get(token), currentPassword)) {
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
        if (isTokenInvalid(token)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "invalid token!");
        } else if (UserController.getInstance().doesNicknameExist(newNickname)) {
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

        if (isTokenInvalid(token)) {
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

        if (isTokenInvalid(token)) {
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
        if (isTokenInvalid(token)) {
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
        if (!UserController.getInstance().doesUsernameExist(username) ||
                !UserController.getInstance().doesUsernameAndPasswordMatch(username, password)) {
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
        String token = createRandomStringToken();
        onlineUsers.put(token, username);
        return token;
    }

    // function to generate a random string of byte-length n as a token
    /* https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string */
    private String createRandomStringToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[30];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token); //base64 encoding
    }

    private boolean isTokenInvalid(String token) {
        return !onlineUsers.containsKey(token);
    }

    private String registerRequest(JSONObject valueObject) {
        String username = valueObject.getString("Username");
        String password = valueObject.getString("Password");
        String nickname = valueObject.getString("Nickname");
        // creating the json response object
        JSONObject answerObject = new JSONObject();

        // check possible errors
        if (UserController.getInstance().doesUsernameExist(username)) {
            answerObject.put("Type", "Error");
            answerObject.put("Value", "user with username " + username + " already exists");
        } else if (UserController.getInstance().doesNicknameExist(nickname)) {
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
