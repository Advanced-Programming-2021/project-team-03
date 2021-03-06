package server.control.game;

import org.json.JSONArray;
import org.json.JSONObject;
import server.control.MainController;
import server.control.databaseController.DatabaseException;
import server.model.card.*;
import server.model.enums.*;
import server.model.game.Board;
import server.model.game.Game;
import server.model.game.Player;
import server.model.game.PlayerTurn;
import server.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static server.control.game.GamePhases.*;
import static server.control.game.TypeOfSelectedCard.*;
import static server.control.game.UpdateEnum.TEXCHANGER_ACTIVATED;
import static server.model.enums.FaceUpSituation.FACE_DOWN;
import static server.model.enums.FaceUpSituation.FACE_UP;
import static server.model.enums.SpellAndTrapIcon.*;
import static server.model.game.PlayerTurn.PLAYER1;
import static server.model.game.PlayerTurn.PLAYER2;

enum TypeOfSelectedCard {
    MONSTER,
    SPELL,
    FIELD_CARD,
    HAND_CARD,
}

public class GameController {
    private static GameController gameController;

    private GameController() {
    }

    public static GameController getInstance() {
        if (gameController == null)
            gameController = new GameController();
        return gameController;
    }

    private Update gameUpdates;
    private Card selectedCard;
    private TypeOfSelectedCard typeOfSelectedCard;
    private GamePhases currentPhase;
    private Game game;
    private PlayerTurn turn;
    private int currentRound;
    private boolean firstBattlePhase;

    public void newDuel(String firstPlayerName, String secondPlayerName, int numberOfRound) {
        this.currentPhase = DRAW;
        this.currentRound = 1;
        firstBattlePhase = true;

        // creating a random number to determine the starting player
        Random random = new Random();
        if (random.nextInt(2) % 2 == 0)
            turn = PLAYER1;
        else
            turn = PLAYER2;

        game = new Game(User.getByUsername(firstPlayerName), User.getByUsername(secondPlayerName), numberOfRound);
        gameUpdates = new Update(game);

        StringBuilder answer = new StringBuilder();

        answer.append("Duel starts between").append(firstPlayerName).append(" & ").append(secondPlayerName).append('\n');
        if (turn == PLAYER1)
            answer.append("its ").append(game.getPlayer1().getUser().getNickname()).append("???s turn\n");
        else
            answer.append("its ").append(game.getPlayer2().getUser().getNickname()).append("???s turn\n");

        answer.append("phase: Draw Phase\n");
        answer.append(drawPhase());

        MainController.getInstance().sendPrintRequestToView(answer.toString());
    }

    public void newDuelWithAI(String username, int numberOfRound) {
        game = new Game(User.getByUsername(username), numberOfRound);
        gameUpdates = new Update(game);
        AIController.getInstance().initialize(game, gameUpdates);

        this.currentPhase = DRAW;
        this.currentRound = 1;
        turn = PLAYER1;
        firstBattlePhase = true;

        String answer = "Duel starts between" + username + " & " + "AI" + '\n' +
                "its " + game.getPlayer1().getUser().getNickname() + "???s turn\n" +
                "phase: Draw Phase\n" +
                drawPhase();
        MainController.getInstance().sendPrintRequestToView(answer);
    }

    public boolean isCardAddressValid(int cardPosition) {
        return cardPosition <= 6 && cardPosition >= 1;
    }

    public boolean isThereACardInGivenPosition(String cardType, int cardPosition, boolean isOpponentCard) {
        Board board;
        if ((turn == PlayerTurn.PLAYER1 && isOpponentCard)
                || (turn == PlayerTurn.PLAYER2 && !isOpponentCard)) {
            board = game.getPlayer2().getBoard();
        } else {
            board = game.getPlayer1().getBoard();
        }
        return checkTheBoard(board, cardType, cardPosition);
    }

    private boolean checkTheBoard(Board board, String cardType, int cardPosition) {
        // return true if the board contain a card in given position
        switch (cardType) {
            case "Monster" -> {
                if (board.getMonstersInField().containsKey(cardPosition))
                    return true;
            }
            case "Spell" -> {
                if (board.getSpellAndTrapsInField().containsKey(cardPosition))
                    return true;
            }
            case "Field" -> {
                if (board.getFieldCard() != null)
                    return true;
            }
            case "Hand" -> {
                return board.getInHandCards().size() >= cardPosition;
            }
        }
        return false;
    }

    public void selectCard(String cardType, int cardPosition, boolean isOpponentCard) {
        Board board;
        if ((turn == PlayerTurn.PLAYER1 && isOpponentCard)
                || (turn == PlayerTurn.PLAYER2 && !isOpponentCard)) {
            board = game.getPlayer2().getBoard();
        } else {
            board = game.getPlayer1().getBoard();
        }
        switch (cardType) {
            case "Monster" -> {
                selectedCard = board.getMonsterInFieldByPosition(cardPosition);
                typeOfSelectedCard = MONSTER;
            }
            case "Spell" -> {
                selectedCard = board.getSpellAndTrapByPosition(cardPosition);
                typeOfSelectedCard = SPELL;
            }
            case "Field" -> {
                selectedCard = board.getFieldCard();
                typeOfSelectedCard = FIELD_CARD;
            }
            case "Hand" -> {
                selectedCard = board.getInHandCardByPosition(cardPosition);
                typeOfSelectedCard = HAND_CARD;
            }
        }
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void deselectCard() {
        selectedCard = null;
        typeOfSelectedCard = null;
    }

    public GamePhases getCurrentPhase() {
        return currentPhase;
    }

    public boolean canSummonSelectedCard() {
        if (game.getCardsOwner(selectedCard) != turn) return false;
        ArrayList<Card> inHandCards = game.getCardsInBoard(selectedCard).getInHandCards();
        boolean isCardInHand = false;
        for (Card card : inHandCards) {
            if (card.equals(selectedCard)) {
                isCardInHand = true;
                break;
            }
        }
        if (!isCardInHand) return false;
        if (!(selectedCard instanceof Monster)) return false;
        Monster monster = (Monster) selectedCard;
        return monster.getType() == MonsterTypes.EFFECT || monster.getType() == MonsterTypes.NORMAL;
    }

    public boolean isCardFieldZoneFull() {
        Player player = getPlayerByTurn();
        if (selectedCard instanceof Monster) {
            return player.getBoard().getMonstersInField().size() >= 5;
        } else {
            SpellAndTrap spell = (SpellAndTrap) selectedCard;
            if (spell.getIcon() == FIELD) return false;
            else return player.getBoard().getSpellAndTrapsInField().size() >= 5;
        }
    }

    public boolean canPlayerSummonOrSetAnotherCard() {
        if (selectedCard instanceof Monster) {
            Monster monster = (Monster) selectedCard;
            if (monster.getCardName().equals("Gate Guardian")) return true;
            if (monster.getCardName().equals("The Tricky")) return true;
            return !gameUpdates.haveBeenSetOrSummonACard();
        } else return true;
    }

    public boolean isThereEnoughCardToTribute() {
        Monster monster = (Monster) selectedCard;
        if (monster.getCardName().equals("Beast King Barbaros")) return true;
        Board board = getPlayerByTurn().getBoard();
        if (monster.getLevel() <= 4) return true;
        if (monster.getLevel() <= 6) {
            return board.getMonstersInField().size() >= 1;
        }
        return board.getMonstersInField().size() >= 2;
    }

    public String summonCard() {
        boolean activeTrap = activeTraps(TrapNames.SOLEMN_WARNING);
        if (activeTrap) {
            return "summon Canceled and Your card has been destroyed Because the opponent activated the Solemn Warning trap";
        }
        Monster monster = (Monster) selectedCard;
        Board board = getPlayerByTurn().getBoard();
        if (monster.getCardName().equals("Terratiger, the Empowered Warrior")) {
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Summon");
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Terratiger, the Empowered Warrior");
            String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
            JSONObject inputObject = new JSONObject(viewAnswer);
            String requestType = inputObject.getString("Type");
            if (requestType.equals("Cancel")) {
                return "summoned successfully\n" +
                        "And Special summon for Terratiger, the Empowered Warrior did not used.";
            } else {
                String positionString = inputObject.getString("Position");
                int position = Integer.parseInt(positionString);
                Card card = board.getInHandCardByPosition(position);
                if (card instanceof Monster) {
                    Monster secondMonster = (Monster) card;
                    if (secondMonster.getLevel() < 4 &&
                            board.getMonstersInField().size() < 5 &&
                            secondMonster.getType() == MonsterTypes.NORMAL) {
                        board.setOrSummonMonsterFromHandToFiled(secondMonster, "Summon");
                        secondMonster.setAttackingFormat(AttackingFormat.DEFENDING);
                        return "summoned successfully\n" +
                                "And Special summon done successfully.";
                    }
                }
                return "summoned successfully\n" +
                        "But you can not use selected card from hand for Special summon";
            }
        }
        if (monster.getCardName().equals("The Tricky")) {
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "The Tricky");
            String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
            JSONObject inputObject = new JSONObject(viewAnswer);
            String requestType = inputObject.getString("Type");
            if (requestType.equals("Successful")) {
                String positionString = inputObject.getString("Position");
                int position = Integer.parseInt(positionString);
                Card card = board.getInHandCardByPosition(position);
                if (card == null || card.equals(selectedCard)) {
                    MainController.getInstance().sendPrintRequestToView("Selected card to remove from hand is invalid\n" +
                            "Special summon stopped And normal summon will continued.");
                } else {
                    board.addCardToGraveyard(card);
                    board.removeCardFromHand(card);
                    board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
                    return "summoned successfully\n" +
                            "And Special summon done successfully.";
                }
            }
        }
        if (monster.getCardName().equals("Beast King Barbaros")) {
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Beast King Barbaros");
            String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
            JSONObject inputObject = new JSONObject(viewAnswer);
            String requestType = inputObject.getString("Type");
            if (requestType.equals("Cancel")) {
                board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
                monster.setBaseAttack(1900);
                return "summoned successfully";
            } else {
                return tributeCards("Summon", viewAnswer, board);
            }
        }
        if (monster.getCardName().equals("Gate Guardian")) {
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Gate Guardian");
            String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
            return tributeCards("Summon", viewAnswer, board);
        }
        if (monster.getLevel() < 5) {
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Summon");
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
            if (selectedCard.getCardName().equals("Mirage Dragon")) {
                AllMonsterEffects.getInstance().mirageDragonEffect(gameUpdates, turn, game);
            }
            return "summoned successfully";
        }
        JSONObject value = new JSONObject();
        JSONObject messageToSendToView = new JSONObject();
        messageToSendToView.put("Type", "Get tribute cards");
        if (monster.getLevel() == 5 || monster.getLevel() == 6) {
            value.put("Number of required cards", String.valueOf(1));
            messageToSendToView.put("Value", value);
        } else if (monster.getLevel() == 7 || monster.getLevel() == 8) {
            value.put("Number of required cards", String.valueOf(2));
            messageToSendToView.put("Value", value);
        }
        String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
        return tributeCards("Summon", viewAnswer, board);
    }

    public String tributeCards(String action, String message, Board board) {
        JSONObject inputObject = new JSONObject(message);
        String requestType = inputObject.getString("Type");
        if (requestType.equals("Cancel")) return "The victimization operation was canceled";
        JSONObject valueObject = inputObject.getJSONObject("Value");
        if (requestType.equals("One card")) {
            int position = Integer.parseInt(valueObject.getString("Position"));
            Monster monster = board.getMonsterInFieldByPosition(position);
            if (monster == null) {
                return "there no monsters one this address";
            }
            board.addCardToGraveyard(monster);
            if (monster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(monster);
            board.removeCardFromField(position, true);
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
            board.setOrSummonMonsterFromHandToFiled(selectedCard, action);
            return action + "ed successfully";
        } else if (requestType.equals("Two card")) {
            int firstPosition = Integer.parseInt(valueObject.getString("First position"));
            Monster firstMonster = board.getMonsterInFieldByPosition(firstPosition);
            int secondPosition = Integer.parseInt(valueObject.getString("Second position"));
            Monster secondMonster = board.getMonsterInFieldByPosition(secondPosition);
            if (firstMonster == null || secondMonster == null) {
                return "there no monsters one this address";
            }
            board.addCardToGraveyard(firstMonster);
            if (firstMonster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(firstMonster);
            board.addCardToGraveyard(secondMonster);
            if (secondMonster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(secondMonster);
            board.removeCardFromField(firstPosition, true);
            board.removeCardFromField(secondPosition, true);
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
            board.setOrSummonMonsterFromHandToFiled(selectedCard, action);
            return action + "ed successfully";
        } else {
            int firstPosition = Integer.parseInt(valueObject.getString("First position"));
            Monster firstMonster = board.getMonsterInFieldByPosition(firstPosition);
            int secondPosition = Integer.parseInt(valueObject.getString("Second position"));
            Monster secondMonster = board.getMonsterInFieldByPosition(secondPosition);
            int thirdPosition = Integer.parseInt(valueObject.getString("Third position"));
            Monster thirdMonster = board.getMonsterInFieldByPosition(thirdPosition);
            if (firstMonster == null || secondMonster == null || thirdMonster == null) {
                return "there no monsters one this address";
            }
            board.addCardToGraveyard(firstMonster);
            if (firstMonster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(firstMonster);
            board.addCardToGraveyard(secondMonster);
            if (secondMonster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(secondMonster);
            board.addCardToGraveyard(thirdMonster);
            if (thirdMonster.getFaceUpSituation() == FACE_UP) gameUpdates.addCardToGraveyard(thirdMonster);
            board.removeCardFromField(firstPosition, true);
            board.removeCardFromField(secondPosition, true);
            board.removeCardFromField(thirdPosition, true);
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Summon");
            Monster monster = (Monster) selectedCard;
            if (monster.getCardName().equals("Beast King Barbaros")) {
                gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
                AllMonsterEffects.getInstance().beastKingBarbarosEffect(gameUpdates, turn, game);
            }
            return "summoned successfully";
        }
    }

    public boolean canSetSelectedCard() {
        if (game.getCardsOwner(selectedCard) != turn) return false;
        if (selectedCard instanceof Monster) {
            Monster monster = (Monster) selectedCard;
            if (monster.getCardName().equals("Gate Guardian")) return false;
            if (monster.getType() == MonsterTypes.RITUAL)
                return false;
        }
        ArrayList<Card> inHandCards = game.getCardsInBoard(selectedCard).getInHandCards();
        for (Card card : inHandCards) {
            if (card.equals(selectedCard)) return true;
        }
        return false;
    }

    public String setCard() {
        Board board = getPlayerByTurn().getBoard();
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        if (selectedCard instanceof Monster) {
            Monster monster = (Monster) selectedCard;
            if (monster.getLevel() < 5) {
                board.setOrSummonMonsterFromHandToFiled(selectedCard, "Set");
                gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
                return "set successfully";
            }
            JSONObject value = new JSONObject();
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Get tribute cards");
            if (monster.getLevel() == 5 || monster.getLevel() == 6) {
                value.put("Number of required cards", String.valueOf(1));
                messageToSendToView.put("Value", value);
            } else if (monster.getLevel() == 7 || monster.getLevel() == 8) {
                value.put("Number of required cards", String.valueOf(2));
                messageToSendToView.put("Value", value);
            }
            String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
            return tributeCards("Set", viewAnswer, board);
        } else {
            SpellAndTrap spellAndTrap = (SpellAndTrap) selectedCard;
            if (spellAndTrap.getIcon() == FIELD) {
                if (!game.isFiledActivated()) {
                    board.setFieldCard(gameUpdates, spellAndTrap);
                } else if (board.getFieldCard() == game.getActivatedFieldCard()) {
                    SpellAndTrap opponentFieldCard = (SpellAndTrap) opponentBoard.getFieldCard();
                    if (opponentFieldCard != null && opponentFieldCard.isActive()) {
                        game.setActivatedFieldCard(opponentFieldCard);
                        game.setFiledActivated(true);
                    } else {
                        game.setFiledActivated(false);
                        game.setActivatedFieldCard(null);
                    }
                    board.setFieldCard(gameUpdates, spellAndTrap);
                } else {
                    board.setFieldCard(gameUpdates, spellAndTrap);
                }
                removeFieldCardFromHand(game,spellAndTrap);
            } else {
                board.setSpellAndTrapsInField(spellAndTrap);
            }
        }
        return "set successfully";
    }

    public boolean canChangeCardPosition() {
        HashMap<Integer, Monster> monstersInField = getPlayerByTurn().getBoard().getMonstersInField();
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            if (entry.getValue().equals(selectedCard))
                return true;
        }
        return false;
    }

    public boolean isCardAlreadyInThisPosition(String position) {
        Monster monster = (Monster) selectedCard;
        if (position.equals("Attack")) {
            return monster.getAttackingFormat() != AttackingFormat.DEFENDING ||
                    monster.getFaceUpSituation() != FaceUpSituation.FACE_UP;
        } else {
            return monster.getAttackingFormat() != AttackingFormat.ATTACKING ||
                    monster.getFaceUpSituation() != FaceUpSituation.FACE_UP;
        }
    }

    public boolean isCardPositionChangedAlready() {
        return gameUpdates.isCardPositionChangedAlready((Monster) selectedCard);
    }

    public void changeCardPosition(String position) {
        Monster monster = (Monster) selectedCard;
        if (position.equals("Attack")) monster.setAttackingFormat(AttackingFormat.ATTACKING);
        else monster.setAttackingFormat(AttackingFormat.DEFENDING);
        gameUpdates.addMonstersToChangedPositionMonsters((Monster) selectedCard);
    }

    public boolean canFlipSummon() {
        if (game.getPlayerByTurn(turn).getBoard().getMonstersInField().containsValue((Monster) selectedCard)) {
            Monster monster = (Monster) selectedCard;
            return monster.getFaceUpSituation().equals(FACE_DOWN);
        }
        return false;
    }

    public String flipSummon() {
        boolean activeTrap = activeTraps(TrapNames.SOLEMN_WARNING);
        if (activeTrap) {
            return "filip summon Canceled and Your card has been destroyed Because the opponent activated the Solemn Warning trap";
        }
        Monster monster = (Monster) selectedCard;
        monster.setAttackingFormat(AttackingFormat.ATTACKING);
        monster.setFaceUpSituation(FaceUpSituation.FACE_UP);
        gameUpdates.flipCard(monster);
        return "flip summoned successfully!";
    }

    public boolean canAttackWithThisCard() {
        if (typeOfSelectedCard == MONSTER &&
                game.getPlayerByTurn(turn).getBoard().getMonstersInField().containsValue((Monster) selectedCard)) {
            Monster monster = (Monster) selectedCard;
            return monster.getAttackingFormat() == AttackingFormat.ATTACKING;
        }
        return false;
    }

    public boolean cardAlreadyAttacked() {
        if (selectedCard instanceof Monster)
            return gameUpdates.didMonsterAttack((Monster) selectedCard);
        else return false;
    }

    public boolean canAttackThisPosition(String position) {
        int positionNumber;
        try {
            positionNumber = Integer.parseInt(position);
        } catch (Exception e) {
            return false;
        }
        return isThereACardInGivenPosition("Monster", positionNumber, true);
    }


    public String attack(int position) {
        /*return the result of attack in a string*/
        String attackingPlayerUsername = game.getPlayerByTurn(turn).getUser().getUsername();

        Board attackingPlayerBoard = game.getPlayerByTurn(turn).getBoard();
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Monster attackingMonster = (Monster) selectedCard;
        Monster opponentMonster = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterInFieldByPosition(position);
        AttackingFormat opponentMonsterFormat = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterInFieldByPosition(position).getAttackingFormat();
        FaceUpSituation opponentMonsterFaceUpSit = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterInFieldByPosition(position).getFaceUpSituation();

        int attackingDef = attackingMonster.getAttackingPower() - opponentMonster.getAttackingPower();
        int defendingDef = attackingMonster.getAttackingPower() - opponentMonster.getDefensivePower();
        StringBuilder answerString = new StringBuilder();

        if (activeTraps(TrapNames.NEGATE_ATTACK)) {
            return "The attack was stopped due to the activation of the Negate Attack trap by the opponent.";
        }

        if (activeTraps(TrapNames.MAGIC_CYLINDER)) {
            return "The attack was stopped due to the activation of the Magic Cylinder trap by the opponent.";
        }

        if (activeTraps(TrapNames.MIRROR_FORCE)) {
            return "The attack was stopped due to the activation of the Mirror Force trap by the opponent.";
        }

        gameUpdates.addMonstersToAttackedMonsters(attackingMonster);
        if (opponentMonster.getCardName().equals("The Calculator")) {
            int attack = AllMonsterEffects.getInstance().theCalculatorAtkPower(attackingPlayerBoard);
            attackingDef += attack;
            answerString.append("\nThe Calculator Monster activated, giving you ")
                    .append(attack).append(" attack power!!!\n");
        }

        if (opponentMonster.getCardName().equals("Suijin") && !AllMonsterEffects.getInstance().isSuijinActivatedBefore(gameUpdates)) {
            answerString.append(AllMonsterEffects.getInstance().suijinEffect(game, gameUpdates, attackingPlayerUsername,
                    attackingPlayerBoard, attackingMonster, opponentMonster, opponentMonsterFormat, opponentMonsterFaceUpSit));
            return answerString.toString();
        }
        if (opponentMonster.getCardName().equals("Texchanger") && !AllMonsterEffects.getInstance().isTexChangerActivatedBefore(gameUpdates)) {
            gameUpdates.getAllUpdates().put(TEXCHANGER_ACTIVATED, opponentMonster);
            answerString.append("Texchanger Monster Activated!!!");
            return answerString.toString();
        }
        if (opponentMonster.getCardName().equals("Marshmallon")) {
            answerString.append(AllMonsterEffects.getInstance().marshmallonEffect(game, opponentMonster, opponentMonsterFaceUpSit,
                    game.getPlayerByTurn(turn), opponentMonsterFormat
                    , attackingMonster, attackingPlayerBoard, attackingDef, defendingDef, gameUpdates, turn));
            return answerString.toString();
        }
        switch (opponentMonsterFormat) {
            case ATTACKING -> {
                if (attackingDef == 0) {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addCardToGraveyard(attackingMonster);
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdates.addCardToGraveyard(opponentMonster);
                    answerString.append("both you and your opponent monster cards are destroyed and no one receives damage");
                } else if (attackingDef > 0) {
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdates.addCardToGraveyard(opponentMonster);
                    if (opponentMonster.getCardName().equals("Exploder Dragon")) {
                        answerString.append("\nExploder Dragon activated!!!\n");
                        attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                        attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                        gameUpdates.addCardToGraveyard(attackingMonster);
                    } else {
                        game.getPlayerOpponentByTurn(turn).decreaseHealthByAmount(attackingDef);
                        answerString.append("your opponent???s monster is destroyed and your opponent receives ").append(attackingDef).append(" battle damage");
                    }
                } else {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addCardToGraveyard(attackingMonster);
                    if (attackingMonster.getCardName().equals("Exploder Dragon")) {
                        answerString.append("\nExploder Dragon activated!!!\n");
                        opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                        opponentBoard.addCardToGraveyard(opponentMonster);
                        gameUpdates.addCardToGraveyard(opponentMonster);
                    } else {
                        game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(attackingDef);
                        answerString.append("Your monster card is destroyed and you received ").append(attackingDef).append(" battle damage");
                    }
                }
                return answerString.toString();
            }
            case DEFENDING -> {
                if (opponentMonsterFaceUpSit == FACE_DOWN) {
                    opponentMonster.setFaceUpSituation(FaceUpSituation.FACE_UP);
                    gameUpdates.flipCard(opponentMonster);
                    answerString.append("opponent???s monster card was ").append(opponentMonster.getCardName());
                }
                if (defendingDef == 0) {
                    answerString.append("no card is destroyed!");
                } else if (defendingDef > 0) {
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdates.addCardToGraveyard(opponentMonster);
                    answerString.append("the defense position monster is destroyed!");
                    if (opponentMonster.getCardName().equals("Exploder Dragon")) {
                        answerString.append("\nExploder Dragon activated!!!\n");
                        attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                        attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                        gameUpdates.addCardToGraveyard(attackingMonster);
                    }
                } else {
                    game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(defendingDef);
                    answerString.append("no card is destroyed and you received ").append(defendingDef).append(" battle damage!");
                }
                return answerString.toString();
            }
        }
        return "Unknown Error";
    }

    public boolean canAttackDirectly() {
        return game.getPlayerOpponentByTurn(turn).getBoard().getMonstersInField().size() == 0;
    }

    public int attackDirectlyToTheOpponent() {
        /*return the opponents receiving damage*/
        Monster attackingMonster = (Monster) selectedCard;
        int attackingPower = attackingMonster.getAttackingPower();
        game.getPlayerOpponentByTurn(turn).decreaseHealthByAmount(attackingPower);
        gameUpdates.addMonstersToAttackedMonsters(attackingMonster);
        return attackingPower;
    }

    public boolean isSpellCard() {
        return (selectedCard instanceof SpellAndTrap && selectedCard.getAttribute() == CardAttributes.SPELL);
    }

    public boolean isCardInField() {
        Board board = getPlayerByTurn().getBoard();
        return board.getSpellAndTrapsInField().containsValue((SpellAndTrap) selectedCard);
    }

    public boolean cardAlreadyActivated() {
        SpellAndTrap spell = (SpellAndTrap) selectedCard;
        return spell.isActive();
    }

    public boolean doesFieldHaveSpaceForThisCard() {
        Board board = getPlayerByTurn().getBoard();
        return board.getSpellAndTrapsInField().size() <= 4;
    }

    public boolean canCardActivate() {
        SpellAndTrap spellAndTrap = (SpellAndTrap) selectedCard;
        Board board = getPlayerByTurn().getBoard();
        HashMap<Integer, Monster> monstersInField = board.getMonstersInField();
        ArrayList<Card> inHandCards = board.getInHandCards();
        if (spellAndTrap.getIcon() == RITUAL) {
            ArrayList<Monster> ritualMonstersInHand = new ArrayList<>();
            for (Card card : inHandCards) {
                if ((card instanceof Monster)) {
                    Monster monster = (Monster) card;
                    if (monster.getType() == MonsterTypes.RITUAL) {
                        ritualMonstersInHand.add(monster);
                    }
                }
            }
            int monstersLevelSum = 0;
            for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
                Monster monster = entry.getValue();
                monstersLevelSum += monster.getLevel();
            }
            if (ritualMonstersInHand.size() != 0) {
                for (Monster monster : ritualMonstersInHand) {
                    if (monster.getLevel() <= monstersLevelSum) {
                        return true;
                    }
                }
            }
            MainController.getInstance().sendPrintRequestToView("there is no way you could ritual summon a monster");
            return false;
        }
        return true;
    }

    public boolean activateSpellCard() {
        boolean trapActivate = activeTraps(TrapNames.MAGIC_JAMMER);
        if (trapActivate) {
            MainController.getInstance().sendPrintRequestToView("Your spell card was destroyed by opponent trap.");
            return false;
        }
        SpellAndTrap spell = (SpellAndTrap) selectedCard;
        Board board = getPlayerByTurn().getBoard();
        if (spell.getIcon() == RITUAL) {
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Ritual summon");
            JSONObject viewAnswer = new JSONObject(MainController.getInstance().sendRequestToView(messageToSendToView));
            String type = viewAnswer.getString("Type");
            if (type.equals("Successful")) {
                JSONObject value = viewAnswer.getJSONObject("Value");
                int ritualMonsterPosition = value.getInt("Ritual monster number");
                Card selectedRitualCard = board.getCardInHandByPosition(ritualMonsterPosition);
                if (selectedRitualCard == null || (selectedRitualCard instanceof SpellAndTrap)) {
                    MainController.getInstance().sendPrintRequestToView("Invalid position for ritual monster.");
                    return false;
                }
                Monster ritualMonster = (Monster) selectedRitualCard;
                if (ritualMonster.getType() != MonsterTypes.RITUAL) {
                    MainController.getInstance().sendPrintRequestToView("Invalid position for ritual monster.");
                    return false;
                }
                messageToSendToView = new JSONObject();
                messageToSendToView.put("Type", "Get tribute cards for ritual summon");
                JSONObject viewTributeAnswer = new JSONObject(MainController.getInstance().sendRequestToView(messageToSendToView));
                type = viewTributeAnswer.getString("Type");
                if (type.equals("Successful")) {
                    JSONArray tributeCards = viewTributeAnswer.getJSONArray("Tribute card numbers");
                    ArrayList<Integer> cardsPositions = new ArrayList<>();
                    for (int i = 0; i < tributeCards.length(); i++) {
                        cardsPositions.add(tributeCards.getInt(i));
                    }
                    int monstersLevelSum = 0;
                    for (Integer position : cardsPositions) {
                        Monster monster = board.getMonsterInFieldByPosition(position);
                        if (monster != null) {
                            monstersLevelSum += monster.getLevel();
                        } else {
                            MainController.getInstance().sendPrintRequestToView("selected monsters levels don???t match with ritual monster");
                            return false;
                        }
                    }
                    if (ritualMonster.getLevel() > monstersLevelSum) {
                        MainController.getInstance().sendPrintRequestToView("selected monsters levels don???t match with ritual monster");
                        return false;
                    }
                    for (Integer position : cardsPositions) {
                        Monster monsterTORemove = board.getMonsterInFieldByPosition(position);
                        board.addCardToGraveyard(monsterTORemove);
                        if (monsterTORemove.getFaceUpSituation() == FACE_UP)
                            gameUpdates.addCardToGraveyard(monsterTORemove);
                        board.removeCardFromField(position, true);
                    }
                    board.setOrSummonMonsterFromHandToFiled(ritualMonster, "Summon");
                    return true;
                } else return false;
            } else return false;
        } else if (spell.getIcon() == FIELD) {
            spell.setActive(true);
            game.setActivatedFieldCard(spell);
            game.setFiledActivated(true);
        } else if (spell.getIcon() == EQUIP)
            AllSpellsEffects.getInstance().equipmentActivator(board, spell);
        else {
            AllSpellsEffects.getInstance().cardActivator(spell, game, gameUpdates, turn);
        }
        return true;
    }

    private void removeFieldCardFromHand(Game game, SpellAndTrap spell) {
        Board board = game.getPlayerByTurn(turn).getBoard();
        board.removeCardFromHand(spell);
    }

    public String getGraveyard() {
        StringBuilder graveyardString = new StringBuilder();
        int counter = 1;
        for (Card card : game.getPlayerByTurn(turn).getBoard().getGraveyard()) {
            graveyardString.append(counter).append(". ").append(card.getCardName()).append(" : ").append(card.getDescription()).append("\n");
        }
        if (game.getPlayerByTurn(turn).getBoard().getGraveyard().size() == 0)
            graveyardString.append("graveyard empty!");
        return graveyardString.toString();
    }

    public boolean canShowSelectedCardToPlayer() {
        if (game.getPlayerOpponentByTurn(turn).getBoard().getInHandCards().contains(selectedCard))
            return false;
        if (selectedCard instanceof Monster &&
                game.getPlayerOpponentByTurn(turn).getBoard().getMonstersInField().containsValue((Monster) selectedCard) &&
                ((Monster) selectedCard).getFaceUpSituation().equals(FACE_DOWN))
            return false;
        return !(selectedCard instanceof SpellAndTrap) ||
                !game.getPlayerOpponentByTurn(turn).getBoard().getSpellAndTrapsInField().containsValue((SpellAndTrap) selectedCard) ||
                ((SpellAndTrap) selectedCard).isActive();
    }

    public String surrender() {
        /*return the surrender message*/
        currentRound += 1;
        game.surrender(turn);
        return game.getPlayerOpponentByTurn(turn).getUser().getUsername() + " won the game and the score is: 1000 - -1000";
    }

    public void changeTurn() {
        if (game.getPlayer2().getUser().getUsername().equals("AIBot")) {
            AIController.getInstance().play();
            return;
        }
        if (turn == PLAYER1) turn = PLAYER2;
        else turn = PLAYER1;
    }

    public PlayerTurn getTurn() {
        return turn;
    }

    public Player getPlayerByTurn() {
        if (turn == PLAYER1) return game.getPlayer1();
        return game.getPlayer2();
    }

    public String endPhase() {
        StringBuilder answerAnswer = new StringBuilder();
        switch (currentPhase) {
            case DRAW -> {
                answerAnswer.append("phase: Standby Phase");
                currentPhase = STANDBY;
                standbyPhase();
            }
            case STANDBY -> {
                answerAnswer.append("phase: First view.Main Phase");
                currentPhase = FIRST_MAIN;
                activeTraps(TrapNames.CALL_OF_THE_HAUNTED);
                activeTraps(TrapNames.MIND_CRUSH);
            }
            case FIRST_MAIN -> {
                if (firstBattlePhase) {
                    answerAnswer.append("phase: Second Phase");
                    currentPhase = SECOND_MAIN;
                    firstBattlePhase = false;
                } else {
                    answerAnswer.append("phase: Battle Phase");
                    currentPhase = BATTLE;
                }
            }
            case BATTLE -> {
                answerAnswer.append("phase: Second view.Main Phase");
                currentPhase = SECOND_MAIN;
                activeTraps(TrapNames.CALL_OF_THE_HAUNTED);
                activeTraps(TrapNames.MIND_CRUSH);
            }
            case SECOND_MAIN -> {
                answerAnswer.append("phase: End Phase\n");
                changeTurn();
                gameUpdates.reset();
                resetMonstersSuppliers();
                if (turn == PLAYER1)
                    answerAnswer.append("its ").append(game.getPlayer1().getUser().getNickname()).append("???s turn\n");
                else
                    answerAnswer.append("its ").append(game.getPlayer2().getUser().getNickname()).append("???s turn\n");
                if (getPlayerByTurn().canPlayerDrawCard()) {
                    answerAnswer.append("phase: Draw Phase\n");
                    currentPhase = DRAW;
                    answerAnswer.append(drawPhase());
                } else roundIsOver(getPlayerByTurn());
            }
        }
        return answerAnswer.toString();
    }

    private void resetMonstersSuppliers() {
        Board board = game.getPlayerByTurn(turn).getBoard();
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();

        for (Monster monster : board.getMonstersInField().values()) {
            monster.setAttackSupplier(new ArrayList<>());
            monster.setDefensiveSupplies(new ArrayList<>());
        }
        for (Monster monster : opponentBoard.getMonstersInField().values()) {
            monster.setAttackSupplier(new ArrayList<>());
            monster.setDefensiveSupplies(new ArrayList<>());
        }
    }

    private String drawPhase() {
        activeTraps(TrapNames.TIME_SEAL);
        if (!gameUpdates.isCanPlayerDrawACard()) {
            gameUpdates.setCanPlayerDrawACard(true);
            return "You can not draw new card Because the opponent has activated Time seal trap";
        }
        StringBuilder answer = new StringBuilder();
        if (game.getPlayerByTurn(turn).getBoard().getInHandCards().size() > 6) {
            answer.append("Your hand is Full!\n");
        } else {
            answer = new StringBuilder("new card added to the hand : ");
            game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
            answer.append(game.getPlayerByTurn(turn).getBoard().getInHandCards().get(game.getPlayerByTurn(turn).getBoard().getInHandCards().size() - 1).getCardName());
        }
        return answer.toString();
    }

    private void standbyPhase() {
        //checking for effects of cards
        checkCommandKnight();
        if (game.isFiledActivated())
            checkFieldCard();
        checkForEquipments(game.getPlayerByTurn(turn).getBoard());
        checkForEquipments(game.getPlayerOpponentByTurn(turn).getBoard());
    }

    private void checkForEquipments(Board board) {
        for (SpellAndTrap spellAndTrap : board.getSpellAndTrapsInField().values()) {
            if (spellAndTrap.getIcon().equals(EQUIP) && spellAndTrap.isActive()) {
                AllSpellsEffects.getInstance().equipmentActivator(board, spellAndTrap);
            }
        }
    }


    private void checkFieldCard() {
        SpellAndTrap fieldCard = game.getActivatedFieldCard();
        AllSpellsEffects.getInstance().fieldCardActivator(fieldCard, game, turn);
    }

    private void checkCommandKnight() {
        Board board = game.getPlayerByTurn(turn).getBoard();
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        activeCommandKnightEffect(board);
        activeCommandKnightEffect(opponentBoard);
    }

    private void activeCommandKnightEffect(Board board) {
        HashMap<Integer, Monster> monstersInField = board.getMonstersInField();
        for (Map.Entry<Integer, Monster> monsterEntry : monstersInField.entrySet()) {
            Monster monster = monsterEntry.getValue();
            if (monster.getCardName().equals("Command Knight") && AllMonsterEffects.getInstance().canCommandKnightActivate(monster)) {
                AllMonsterEffects.getInstance().commandKnightEffect(game, turn);
            }
        }
    }

    public void roundIsOver(Player player) {
        currentRound += 1;
        game.checkRoundResults(gameUpdates);
        gameUpdates.playerWins(game.getPlayerOpponentByPlayer(player));
        String results = game.getWinner().getUser().getUsername() + " won the game and the score is: 1000 - 0";
        JSONObject answerObject = new JSONObject();
        JSONObject value = new JSONObject();
        if (IsGameOver()) {
            answerObject.put("Type", "Game is over");
            value.put("Message", checkGameStatus());
        } else {
            answerObject.put("Type", "Round is over");
            value.put("Message", results);
        }
        answerObject.put("Value", value);
        MainController.getInstance().sendRequestToView(answerObject);
    }

    private boolean IsGameOver() {
        if (currentRound > game.getNumberOfRounds())
            return true;
        else return gameUpdates.isGameOver();
    }

    private String checkGameStatus() {
        Player winner = gameUpdates.getWinner();
        int winnerNumberOfWins = gameUpdates.getWins(winner);
        Player looser = game.getPlayerOpponentByPlayer(winner);
        int looserNumberOfWins = gameUpdates.getWins(looser);
        try {
            winner.getUser().increaseBalance((winnerNumberOfWins * 1000) + 3 * winner.getHealth());
            looser.getUser().increaseBalance((looserNumberOfWins * 1000));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return winner.getUser().getUsername() + " won the whole match with score: " + winnerNumberOfWins * 1000 + "-" + looserNumberOfWins * 1000;
    }

    public void removeEquipment(Monster monster) {
        SpellAndTrap equipment = monster.getEquipment();
        if (equipment != null) {
            if (game.getPlayerByTurn(turn).getBoard().doesContainCard(equipment)) {
                game.getPlayerByTurn(turn).getBoard().removeCardFromField(game.getPlayerByTurn(turn).getBoard().getSpellPosition(equipment), false);
                game.getPlayerByTurn(turn).getBoard().addCardToGraveyard(equipment);
                gameUpdates.addCardToGraveyard(equipment);
            } else if (game.getPlayerOpponentByTurn(turn).getBoard().doesContainCard(equipment)) {
                game.getPlayerOpponentByTurn(turn).getBoard().removeCardFromField(game.getPlayerOpponentByTurn(turn).getBoard().getSpellPosition(equipment), false);
                game.getPlayerOpponentByTurn(turn).getBoard().addCardToGraveyard(equipment);
                gameUpdates.addCardToGraveyard(equipment);
            }
        }
    }

    public String getMap() {
        return game.showGameBoards();
    }

    public void cheatCode(String type, JSONObject valueObject) {
        String username = game.getPlayerByTurn(turn).getUser().getUsername();
        getPlayerByTurn();
        switch (type) {
            case "Force increase" -> addCardToHand(username);
            case "Increase LP" -> increaseHealth(username, valueObject);
            case "Set winner" -> setWinner(username);
            case "Increase money" -> increaseMoney(username, valueObject);
            case "hesoyam" -> hesoyamSafaaaa(username);
        }
    }

    private void hesoyamSafaaaa(String username) {
        //be yad bache hay Groove Street
        MainController.getInstance().sendPrintRequestToView("HESOYAM!!!\n");
        game.getPlayerByName(username).getBoard().addCardFromRemainingToInHandCards();
        MainController.getInstance().sendPrintRequestToView("you have a new card in your hand now!\n");
        game.getPlayerByName(username).decreaseHealthByAmount(-5000);
        MainController.getInstance().sendPrintRequestToView("your LP increased by 5000\n");
        try {
            game.getPlayerByName(username).getUser().increaseBalance(10000);
            MainController.getInstance().sendPrintRequestToView("your balance increased by 10000\n");
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private void increaseMoney(String username, JSONObject valueObject) {
        String amount = valueObject.getString("Amount");
        try {
            int balance = Integer.parseInt(amount);
            game.getPlayerByName(username).getUser().increaseBalance(balance);
            MainController.getInstance().sendPrintRequestToView("Cheat Code Activated!\nyour balance increased by " + balance + "\n");
        } catch (Exception ignored) {
            MainController.getInstance().sendPrintRequestToView("Cheat Code does not activated!!\n");
        }
    }

    private void setWinner(String username) {
        if (game.getPlayer1() == game.getPlayerByName(username)) {
            game.getPlayer2().decreaseHealthByAmount(game.getPlayer2().getHealth());
        } else {
            game.getPlayer1().decreaseHealthByAmount(game.getPlayer1().getHealth());
        }
        MainController.getInstance().sendPrintRequestToView("Cheat Code Activated!\nyour opponent's LP is 0 now\nYou win the game!\n");
    }

    private void increaseHealth(String username, JSONObject valueObject) {
        String amount = valueObject.getString("Amount");
        try {
            int LP = Integer.parseInt(amount);
            game.getPlayerByName(username).decreaseHealthByAmount(-LP);
            MainController.getInstance().sendPrintRequestToView("Cheat Code Activated!\nyour LP increased by " + LP + "\n");
        } catch (Exception ignored) {
            MainController.getInstance().sendPrintRequestToView("Cheat Code does not activated!!\n");
        }
    }

    private void addCardToHand(String username) {
        game.getPlayerByName(username).getBoard().addCardFromRemainingToInHandCards();
        MainController.getInstance().sendPrintRequestToView("Cheat Code Activated!\nyou have a new card in your hand now!\n");
    }

    public boolean activeTraps(TrapNames trapName) {
        AllTrapsEffects allTrapsEffects = AllTrapsEffects.getInstance();
        switch (trapName) {
            case TRAP_HOLE -> {
                if (allTrapsEffects.canTrapHoleActivate(selectedCard, game, turn, trapName)) {
                    allTrapsEffects.trapHoleEffect(selectedCard, game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case TORRENTIAL_TRIBUTE -> {
                if (allTrapsEffects.canTorrentialTributeActivate(game, turn, trapName)) {
                    allTrapsEffects.torrentialTributeEffect(game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case MAGIC_CYLINDER -> {
                if (allTrapsEffects.canMagicCylinderActivate(game, turn, trapName)) {
                    allTrapsEffects.magicCylinderEffect(selectedCard, game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case MIRROR_FORCE -> {
                if (allTrapsEffects.canMirrorForceActivate(game, turn, trapName)) {
                    allTrapsEffects.mirrorForceEffect(game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case NEGATE_ATTACK -> {
                if (allTrapsEffects.canNegateAttackActivate(game, turn, trapName, currentPhase)) {
                    allTrapsEffects.negateAttackEffect(game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case MAGIC_JAMMER -> {
                if (allTrapsEffects.canMagicJammerActivate(game, turn, trapName)) {
                    allTrapsEffects.magicJammerEffect(selectedCard, game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case TIME_SEAL -> {
                if (allTrapsEffects.canTimeSealActivate(currentPhase, game, turn, trapName)) {
                    allTrapsEffects.timeSealEffect(game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case CALL_OF_THE_HAUNTED -> {
                if (allTrapsEffects.canCallOfTheHauntedActivate(currentPhase, game, turn, trapName)) {
                    allTrapsEffects.callOfTheHauntedEffect(game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case SOLEMN_WARNING -> {
                if (allTrapsEffects.canSolemnWarningActivate(game, turn, trapName)) {
                    allTrapsEffects.solemnWarningEffect(selectedCard, game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
            case MIND_CRUSH -> {
                if (allTrapsEffects.canMindCrushActivate(currentPhase, game, turn, trapName)) {
                    allTrapsEffects.mindCrushEffect(game, gameUpdates, turn);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isSelectedCardAMonster() {
        return selectedCard instanceof Monster;
    }

    public JSONObject getMapForGraphic() {
        return game.showGameBoardsForGraphic();
    }

    public String getOpponentGraveyard() {
        StringBuilder graveyardString = new StringBuilder();
        int counter = 1;
        for (Card card : game.getPlayerOpponentByTurn(turn).getBoard().getGraveyard()) {
            graveyardString.append(counter).append(". ").append(card.getCardName()).append(" : ").append(card.getDescription()).append("\n");
        }
        if (game.getPlayerOpponentByTurn(turn).getBoard().getGraveyard().size() == 0)
            graveyardString.append("graveyard empty!");
        return graveyardString.toString();
    }
}
