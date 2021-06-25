package model.game;

import control.databaseController.DatabaseException;
import control.game.GameController;
import control.game.Update;
import model.card.Card;
import model.card.SpellAndTrap;
import model.user.Deck;
import model.user.DeckType;
import model.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static model.game.PlayerTurn.PLAYER1;
import static model.game.PlayerTurn.PLAYER2;

public class Game {
    private final Player player1;
    private final Player player2;
    private final int numberOfRounds;
    private boolean filedActivated;
    private SpellAndTrap activatedFieldCard;

    public Game(User user1, User user2, int numberOfRounds) {
        this.player1 = new Player(8000, new Board(user1.getActiveDeck(), user1), user1);
        this.player2 = new Player(8000, new Board(user2.getActiveDeck(), user2), user2);
        this.numberOfRounds = numberOfRounds;
        filedActivated = false;
    }

    public Game(User user1, int numberOfRounds) {
        /*duel with AI*/
        this.player1 = new Player(8000, new Board(user1.getActiveDeck(), user1), user1);

        User AI = User.getByUsername("AIBot");
        Deck deck;
        try {
            deck = new Deck("AIBotDeck");
            deck.addCard(generateDeck(AI), DeckType.MAIN);
            AI.setActiveDeck(deck);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        this.player2 = new Player(8000, new Board(AI.getActiveDeck(), User.getByUsername("AIBot")), User.getByUsername("AIBot"));
        this.numberOfRounds = numberOfRounds;
        filedActivated = false;
    }

    private ArrayList<Card> generateDeck(User user) {
        ArrayList<Card> cards = user.getCards();
        Collections.shuffle(cards);
        return cards.stream().limit(50).collect(Collectors.toCollection(ArrayList::new));
    }

    public SpellAndTrap getActivatedFieldCard() {
        return activatedFieldCard;
    }

    public void setActivatedFieldCard(SpellAndTrap activatedFieldCard) {
        this.activatedFieldCard = activatedFieldCard;
    }

    public Player getPlayerByName(String username) {
        if (player1.getUser().getUsername().equals(username)) return player1;
        else return player2;
    }

    public Player getPlayerByTurn(PlayerTurn turn) {
        if (turn == PLAYER1)
            return player1;
        else
            return player2;
    }

    public Player getPlayerOpponentByTurn(PlayerTurn turn) {
        if (turn == PLAYER1)
            return player2;
        else
            return player1;
    }

    public String showGameBoards() {
        StringBuilder board = new StringBuilder();
        if (GameController.getInstance().getTurn() == PLAYER1) {
            boardBuilder(board, player2, player1);
        } else {
            boardBuilder(board, player1, player2);
        }
        return board.toString();
    }

    private void boardBuilder(StringBuilder board, Player firstPlayer, Player secondPlayer) {
        board.append(firstPlayer.getUser().getNickname()).append(": ").append(firstPlayer.getHealth()).append("\n");
        board.append(firstPlayer.getBoard().showAsEnemyBoard());
        board.append("\n--------------------------\n\n");
        board.append(secondPlayer.getBoard().toString());
        board.append(secondPlayer.getUser().getNickname()).append(": ").append(secondPlayer.getHealth()).append("\n");
    }

    public Board getCardsInBoard(Card card) { /* monster effect */
        if (player1.getBoard().doesContainCard(card))
            return player1.getBoard();

        if (player2.getBoard().doesContainCard(card))
            return player2.getBoard();
        return null;
    }

    public PlayerTurn getCardsOwner(Card card) { /* monster effect */
        if (player1.getBoard().doesContainCard(card))
            return PLAYER1;
        if (player2.getBoard().doesContainCard(card))
            return PLAYER2;
        return null;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void surrender(PlayerTurn turn) {
        try {
            getPlayerByTurn(turn).getUser().increaseScore(-1000);
            getPlayerOpponentByTurn(turn).getUser().increaseScore(1000);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public Player getWinner() {
        if (player1.getHealth() <= 0)
            return player2;
        if (player2.getHealth() <= 0)
            return player1;
        if (getPlayer1().canPlayerDrawCard())
            return player1;
        return player2;
    }

    public void checkRoundResults(Update gameUpdates) {
        try {
            getWinner().getUser().increaseScore(1000);
            gameUpdates.playerWins(getWinner());
        } catch (DatabaseException e) {
            System.out.println(e);
        }
    }

    public boolean isFiledActivated() {
        return filedActivated;
    }

    public void setFiledActivated(boolean filedActivated) {
        this.filedActivated = filedActivated;
    }
}
