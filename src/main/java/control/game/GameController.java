package control.game;

import control.MainController;
import control.databaseController.DatabaseException;
import model.card.*;
import model.enums.AttackingFormat;
import model.enums.CardAttributes;
import model.enums.FaceUpSituation;
import model.enums.MonsterTypes;
import model.game.Board;
import model.game.Game;
import model.game.Player;
import model.game.PlayerTurn;
import model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static control.game.GamePhases.*;
import static control.game.TypeOfSelectedCard.*;
import static control.game.UpdateEnum.*;
import static model.enums.FaceUpSituation.*;
import static model.enums.SpellAndTrapIcon.*;
import static model.game.PlayerTurn.PLAYER1;
import static model.game.PlayerTurn.PLAYER2;

enum TypeOfSelectedCard {
    MONSTER,
    SPELL,
    FIELD_CARD,
    HAND_CARD,
}

public class GameController {
    private static GameController gameController;

    private GameController() {
        this.currentPhase = DRAW;
        this.currentRound = 1;

        // creating a random number to determine the starting player
        Random random = new Random();
        if (random.nextInt(2) % 2 == 0)
            turn = PLAYER1;
        else
            turn = PLAYER2;
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

    public void newDuel(String firstPlayerName, String secondPlayerName, int numberOfRound) {
        game = new Game(User.getByUsername(firstPlayerName), User.getByUsername(secondPlayerName), numberOfRound);
        gameUpdates = new Update(game);
    }

    public void newDuelWithAI(String username, int numberOfRound) {
        game = new Game(User.getByUsername(username), numberOfRound);
        gameUpdates = new Update(game);
        //TODO AI controller
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
                if (board.getInHandCards().size() <= cardPosition)
                    return true;
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
        if (monster.getType() == MonsterTypes.EFFECT || monster.getType() == MonsterTypes.NORMAL) {
            return true;
        }
        return false;
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
                if (card != null && card instanceof Monster) {
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
                if (card.equals(selectedCard) || card == null) {
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
                return tributeCards(viewAnswer, board);
            }
        }
        if (monster.getCardName().equals("Gate Guardian")) {
            JSONObject messageToSendToView = new JSONObject();
            messageToSendToView.put("Type", "Gate Guardian");
            String viewAnswer = MainController.getInstance().sendRequestToView(messageToSendToView);
            return tributeCards(viewAnswer, board);
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
        return tributeCards(viewAnswer, board);
    }

    public String tributeCards(String message, Board board) {
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
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Summon");
            return "summoned successfully";
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
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Summon");
            return "summoned successfully";
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
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Set");
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
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
            } else {
                board.setSpellAndTrapsInField(spellAndTrap);
            }
        }
        return "set successfully";
    }

    public boolean canChangeCardPosition() {
        HashMap<Integer, Monster> monstersInField = getPlayerByTurn().getBoard().getMonstersInField();
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            Monster monster = entry.getValue();
            if (selectedCard.equals(selectedCard))
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

    public boolean canFlipSummon(String username) {
        if (game.getPlayerByName(username).getBoard().getMonstersInField().containsValue((Monster) selectedCard)) {
            Monster monster = (Monster) selectedCard;
            return monster.getFaceUpSituation().equals(FACE_DOWN);
        }
        return false;
    }

    public void flipSummon() {
        Monster monster = (Monster) selectedCard;
        monster.setAttackingFormat(AttackingFormat.ATTACKING);
        monster.setFaceUpSituation(FaceUpSituation.FACE_UP);
        gameUpdates.flipCard(monster);
    }

    public boolean canAttackWithThisCard(String username) {
        if (typeOfSelectedCard == MONSTER &&
                game.getPlayerByName(username).getBoard().getMonstersInField().containsValue((Monster) selectedCard)) {
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


    public String attack(String attackingPlayerUsername, int position) {
        /*return the result of attack in a string*/

        Board attackingPlayerBoard = game.getPlayerByName(attackingPlayerUsername).getBoard();
        Board opponentBoard = game.getPlayerOpponentByTurn(turn).getBoard();
        Monster attackingMonster = (Monster) selectedCard;
        Monster opponentMonster = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterInFieldByPosition(position);
        AttackingFormat opponentMonsterFormat = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterInFieldByPosition(position).getAttackingFormat();
        FaceUpSituation opponentMonsterFaceUpSit = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterInFieldByPosition(position).getFaceUpSituation();
        gameUpdates.addMonstersToAttackedMonsters(attackingMonster);
        int attackingDef = attackingMonster.getAttackingPower() - opponentMonster.getAttackingPower();
        int defendingDef = attackingMonster.getAttackingPower() - opponentMonster.getDefensivePower();
        StringBuilder answerString = new StringBuilder();

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
                    , attackingMonster, attackingPlayerBoard, opponentBoard, attackingDef, defendingDef, gameUpdates, turn));
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
                        answerString.append("your opponent’s monster is destroyed and your opponent receives ").append(attackingDef).append(" battle damage");
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
                    answerString.append("opponent’s monster card was ").append(opponentMonster.getCardName());
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
        if (board.getSpellAndTrapsInField().size() <= 4) return true;
        return false;
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
        //TODO
        return true;
    }

    public boolean activateSpellCard() {
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
                        cardsPositions.add(tributeCards.getInt(i)); //TODO: test this function
                    }
                    int monstersLevelSum = 0;
                    for (Integer position : cardsPositions) {
                        Monster monster = board.getMonsterInFieldByPosition(position);
                        if (monster != null) {
                            monstersLevelSum += monster.getLevel();
                        } else {
                            MainController.getInstance().sendPrintRequestToView("selected monsters levels don’t match with ritual monster");
                            return false;
                        }
                    }
                    if (ritualMonster.getLevel() > monstersLevelSum) {
                        MainController.getInstance().sendPrintRequestToView("selected monsters levels don’t match with ritual monster");
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
            AllSpellsEffects.getInstance().equipmentActivator(board, spell, game, gameUpdates, turn);
        else {
            AllSpellsEffects.getInstance().cardActivator(spell, game, gameUpdates, turn);
        }
        return true;
    }

    public String getGraveyard(String username) {
        StringBuilder graveyardString = new StringBuilder();
        int counter = 1;
        for (Card card : game.getPlayerByName(username).getBoard().getGraveyard()) {
            graveyardString.append(counter).append(". ").append(card.getCardName()).append(" : ").append(card.getDescription()).append("\n");
        }
        if (game.getPlayerByName(username).getBoard().getGraveyard().size() == 0)
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
                answerAnswer.append("phase: First Main Phase");
                currentPhase = FIRST_MAIN;
            }
            case FIRST_MAIN -> {
                answerAnswer.append("phase: Battle Phase");
                currentPhase = BATTLE;
            }
            case BATTLE -> {
                answerAnswer.append("phase: Second Phase");
                currentPhase = SECOND_MAIN;
            }
            case SECOND_MAIN -> {
                answerAnswer.append("phase: End Phase\n");
                changeTurn();
                gameUpdates.reset();
                resetMonstersSuppliers();
                if (turn == PLAYER1)
                    answerAnswer.append("its ").append(game.getPlayer1().getUser().getNickname()).append("’s turn\n");
                else
                    answerAnswer.append("its ").append(game.getPlayer2().getUser().getNickname()).append("’s turn\n");
                if (getPlayerByTurn().canPlayerDrawCard()) {
                    answerAnswer.append("phase: Draw Phase\n");
                    currentPhase = DRAW;
                    answerAnswer.append(drawPhase());
                } else roundIsOver();
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
        StringBuilder answer = new StringBuilder("new card added to the hand : ");
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        answer.append(game.getPlayerByTurn(turn).getBoard().getInHandCards().get(game.getPlayerByTurn(turn).getBoard().getInHandCards().size() - 1).getCardName());
        return answer.toString();
    }

    private void standbyPhase() {
        //checking for effects of cards
        checkCommandKnight();
        if (game.isFiledActivated())
            checkFieldCard();
        checkForEquipments(game.getPlayerByTurn(turn).getBoard());
        checkForEquipments(game.getPlayerOpponentByTurn(turn).getBoard());
        //TODO
    }

    private void checkForEquipments(Board board) {
        for (SpellAndTrap spellAndTrap : board.getSpellAndTrapsInField().values()) {
            if (spellAndTrap.getIcon().equals(EQUIP) && spellAndTrap.isActive()) {
                AllSpellsEffects.getInstance().equipmentActivator(board, spellAndTrap, game, gameUpdates, turn);
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

    public void roundIsOver() {
        currentRound += 1;
        game.checkRoundResults(gameUpdates);
        String results = game.getWinner().getUser().getUsername() + " won the game and the score is: 1000 - 0";
        JSONObject answerObject = new JSONObject();
        if (IsGameOver()) {
            answerObject.put("Type", "Game is over");
            answerObject.put("Value", checkGameStatus());
        } else {
            answerObject.put("Type", "Round is over");
            answerObject.put("Value", results);
        }
        MainController.getInstance().sendRequestToView(answerObject);
    }

    private boolean IsGameOver() {
        if (currentRound > game.getNumberOfRounds())
            return true;
        else return gameUpdates.isGameOver();
    }

    private String checkGameStatus() {
        Player gameWinner = gameUpdates.getWinner();
        int winnerNumberOfWins = gameUpdates.getWins(gameWinner);
        Player looser = gameUpdates.getLooser(gameWinner);
        int looserNumberOfWins = gameUpdates.getWins(looser);
        try {
            gameWinner.getUser().increaseBalance((winnerNumberOfWins * 1000) + 3 * gameWinner.getHealth());
            looser.getUser().increaseBalance((looserNumberOfWins * 1000));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return gameWinner.getUser().getUsername() + " won the whole match with score: " + winnerNumberOfWins * 1000 + "-" + looserNumberOfWins * 1000;
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

    public void cheatCode(String type, String username, JSONObject valueObject) {
        switch (type) {
            case "Force increase" -> addCardToHand(username);
            case "Increase LP" -> increaseHealth(username, valueObject);
            case "Set winner" -> setWinner(username, valueObject);
            case "Increase money" -> increaseMoney(username, valueObject);
            case "Hesoyam" -> hesoyamSafaaaa(username);
        }
    }

    private void hesoyamSafaaaa(String username) {

    }

    private void increaseMoney(String username, JSONObject valueObject) {

    }

    private void setWinner(String username, JSONObject valueObject) {

    }

    private void increaseHealth(String username, JSONObject valueObject) {

    }

    private void addCardToHand(String username) {
        game.getPlayerByName(username).getBoard().addCardFromRemainingToInHandCards();
        MainController.getInstance().sendPrintRequestToView("Cheat Code Activated!\nyou have a new card in your hand now!\n");
    }
}
