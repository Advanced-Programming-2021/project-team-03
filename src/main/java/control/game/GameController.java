package control.game;

import control.MainController;
import model.card.AllMonsterEffects;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;
import model.enums.AttackingFormat;
import model.enums.CardAttributes;
import model.enums.FaceUpSituation;
import model.game.Board;
import model.game.Game;
import model.game.Player;
import model.game.PlayerTurn;
import model.user.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static control.game.GamePhases.*;
import static control.game.TypeOfSelectedCard.*;
import static control.game.UpdateEnum.*;
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
                selectedCard = board.getMonsterByPosition(cardPosition);
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
            if (card.getCardIdInTheGame() == selectedCard.getCardIdInTheGame()) {
                isCardInHand = true;
                break;
            }
        }
        if (!isCardInHand) return false;
        //TODO: check that card has normal summon or not.
        return selectedCard instanceof Monster;
    }

    public boolean isCardFieldZoneFull() {
        Player player = getPlayerByTurn();
        return player.getBoard().getMonstersInField().size() >= 5;
    }

    public boolean canPlayerSummonOrSetAnotherCard() {
        return !gameUpdates.haveBeenSetOrSummonACard();
    }

    public boolean isThereEnoughCardToTribute() {
        Monster monster = (Monster) selectedCard;
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
        if (monster.getLevel() < 5) {
            board.setOrSummonMonsterFromHandToFiled(selectedCard, "Summon");
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
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
        if (requestType.equals("One Card")) {
            int position = Integer.parseInt(valueObject.getString("Position"));
            Monster monster = board.getMonsterByPosition(position);
            if (monster == null) {
                return "there no monsters one this address";
            }
            board.addCardToGraveyard(monster);
            board.removeCardFromField(position, true);
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
            return "summoned successfully";
        } else {
            int firstPosition = Integer.parseInt(valueObject.getString("First position"));
            Monster firstMonster = board.getMonsterByPosition(firstPosition);
            int secondPosition = Integer.parseInt(valueObject.getString("Second position"));
            Monster secondMonster = board.getMonsterByPosition(secondPosition);
            if (firstMonster == null || secondMonster == null) {
                return "there no monsters one this address";
            }
            board.addCardToGraveyard(firstMonster);
            board.addCardToGraveyard(secondMonster);
            board.removeCardFromField(firstPosition, true);
            board.removeCardFromField(secondPosition, true);
            gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
            return "summoned successfully";
        }
    }

    public boolean canSetSelectedCard() {
        if (game.getCardsOwner(selectedCard) != turn) return false;
        ArrayList<Card> inHandCards = game.getCardsInBoard(selectedCard).getInHandCards();
        for (Card card : inHandCards) {
            if (card.getCardIdInTheGame() == selectedCard.getCardIdInTheGame()) return true;
        }
        return false;
    }

    public String setCard() {
        Board board = getPlayerByTurn().getBoard();
        board.setOrSummonMonsterFromHandToFiled(selectedCard, "Set");
        gameUpdates.setHaveBeenSetOrSummonACardInPhase(true);
        return "set successfully";
    }

    public boolean canChangeCardPosition() {
        HashMap<Integer, Monster> monstersInField = getPlayerByTurn().getBoard().getMonstersInField();
        for (Map.Entry<Integer, Monster> entry : monstersInField.entrySet()) {
            Monster monster = entry.getValue();
            if (selectedCard.getCardIdInTheGame() == monster.getCardIdInTheGame()) return true;
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
            return monster.getFaceUpSituation().equals(FaceUpSituation.FACE_DOWN);
        }
        return false;
    }

    public void flipSummon() {
        Monster monster = (Monster) selectedCard;
        monster.setAttackingFormat(AttackingFormat.ATTACKING);
        monster.setFaceUpSituation(FaceUpSituation.FACE_UP);
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
        Monster opponentMonster = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterByPosition(position);
        AttackingFormat opponentMonsterFormat = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterByPosition(position).getAttackingFormat();
        FaceUpSituation opponentMonsterFaceUpSit = game.getPlayerOpponentByTurn(turn).getBoard().getMonsterByPosition(position).getFaceUpSituation();
        gameUpdates.addMonstersToAttackedMonsters(attackingMonster);
        int attackingDef = attackingMonster.getAttackingPower() - opponentMonster.getAttackingPower();
        int defendingDef = attackingMonster.getAttackingPower() - opponentMonster.getDefensivePower();
        StringBuilder answerString = new StringBuilder();
        if (opponentMonster.getCardName().equals("Suijin") && !isSuijinActivatedBefore()) {
            answerString.append(AllMonsterEffects.getInstance().attackSuijin(game, gameUpdates, attackingPlayerUsername,
                    attackingPlayerBoard, attackingMonster, opponentMonster, opponentMonsterFormat, opponentMonsterFaceUpSit));
            return answerString.toString();
        }
        switch (opponentMonsterFormat) {
            case ATTACKING -> {
                if (attackingDef == 0) {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addMonsterToGraveyard(attackingMonster);
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdates.addMonsterToGraveyard(opponentMonster);
                    answerString.append("both you and your opponent monster cards are destroyed and no one receives damage");
                } else if (attackingDef > 0) {
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdates.addMonsterToGraveyard(opponentMonster);
                    game.getPlayerOpponentByTurn(turn).decreaseHealthByAmount(attackingDef);
                    answerString.append("your opponent’s monster is destroyed and your opponent receives ").append(attackingDef).append(" battle damage");
                } else {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdates.addMonsterToGraveyard(attackingMonster);
                    game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(attackingDef);
                    answerString.append("Your monster card is destroyed and you received ").append(attackingDef).append(" battle damage");
                }
                return answerString.toString();
            }
            case DEFENDING -> {
                if (opponentMonsterFaceUpSit == FaceUpSituation.FACE_DOWN) {
                    opponentMonster.setFaceUpSituation(FaceUpSituation.FACE_UP);
                    answerString.append("opponent’s monster card was ").append(opponentMonster.getCardName());
                }
                if (defendingDef == 0) {
                    answerString.append("no card is destroyed!");
                } else if (defendingDef > 0) {
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdates.addMonsterToGraveyard(opponentMonster);
                    answerString.append("the defense position monster is destroyed!");
                } else {
                    game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(defendingDef);
                    answerString.append("no card is destroyed and you received ").append(defendingDef).append(" battle damage!");
                }
                return answerString.toString();
            }
        }
        return "Unknown Error";
    }

    private boolean isSuijinActivatedBefore() {
        for (UpdateEnum updateEnum : gameUpdates.getAllUpdates().keySet()) {
            if (updateEnum.equals(SUIJIN_ACTIVATED)) {
                return true;
            }
        }
        return false;
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

    public boolean cardAlreadyActivated() {
        //TODO
        return false;
    }

    public boolean doesFieldHaveSpaceForThisCard() {
        //TODO
        return false;
    }

    public boolean canCardActivate() {
        //TODO
        return false;
    }

    public void activateSpellCard() {
        //TODO
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

    public boolean canShowSelectedCardToPlayer(String username) {
        if (game.getPlayerOpponentByTurn(turn).getBoard().getInHandCards().contains(selectedCard))
            return false;
        if (selectedCard instanceof Monster &&
                game.getPlayerOpponentByTurn(turn).getBoard().getMonstersInField().containsValue((Monster) selectedCard) &&
                ((Monster) selectedCard).getFaceUpSituation().equals(FaceUpSituation.FACE_DOWN))
            return false;
        if (selectedCard instanceof SpellAndTrap &&
                game.getPlayerOpponentByTurn(turn).getBoard().getSpellAndTrapsInField().containsValue((SpellAndTrap) selectedCard) &&
                !((SpellAndTrap) selectedCard).isActive())
            return false;
        return true;
    }

    //TODO game status
    //TODO get winner in 3 round games

    public String surrender(String username) {
        /*return the surrender message*/
        currentRound += 1;
        game.surrender(turn);
        return game.getPlayerOpponentByTurn(turn).getUser().getUsername() + " won the game and the score is: 1000 - -1000";
    }

    public void changeTurn() {
        if (turn == PLAYER1)
            turn = PLAYER2;
        else
            turn = PLAYER1;
    }

    public PlayerTurn getTurn() {
        return turn;
    }

    public Player getPlayerByTurn() {
        if (turn == PLAYER1) return game.getPlayer1();
        return game.getPlayer2();
    }

    public boolean isRoundFinished() {
        return !game.getPlayerByTurn(turn).canPlayerDrawCard() ||
                game.getPlayerByTurn(turn).getHealth() <= 0;
    }

    public String checkGameStatus(String username) {
        if (isRoundFinished())
            return "This Round is over!";
        return "the round is not over yet!";
    }

    public String endPhase(String username) {
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
                if (isRoundFinished()) {
                    answerAnswer.append(getRoundResults());
                } else {
                    changeTurn();
                    if (turn == PLAYER1)
                        answerAnswer.append("its ").append(game.getPlayer1().getUser().getNickname()).append("’s turn\n");
                    else
                        answerAnswer.append("its ").append(game.getPlayer2().getUser().getNickname()).append("’s turn\n");

                    answerAnswer.append("phase: Draw Phase\n");
                    currentPhase = DRAW;
                    answerAnswer.append(drawPhase());
                }
            }
        }
        return answerAnswer.toString();
    }

    private String getRoundResults() {
        currentRound += 1;
        game.checkRoundResults();
        return game.getWinner().getUser().getUsername() + " won the game and the score is: 1000 - 0";
    }

    private String drawPhase() {
        StringBuilder answer = new StringBuilder("new card added to the hand : ");
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        answer.append(game.getPlayerByTurn(turn).getBoard().getInHandCards().get(game.getPlayerByTurn(turn).getBoard().getInHandCards().size() - 1).getCardName());
        return answer.toString();
    }

    private void standbyPhase() {
        //TODO
        //checking for effects of cards
    }

}
