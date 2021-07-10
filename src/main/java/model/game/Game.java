package model.game;

import control.databaseController.DatabaseException;
import control.game.GameController;
import control.game.Update;
import model.card.Card;
import model.card.SpellAndTrap;
import model.user.Deck;
import model.user.DeckType;
import model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;

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
            e.printStackTrace();
        }
    }

    public boolean isFiledActivated() {
        return filedActivated;
    }

    public void setFiledActivated(boolean filedActivated) {
        this.filedActivated = filedActivated;
    }

    public Player getPlayerOpponentByPlayer(Player player) {
        if (player.equals(player1))
            return player2;
        else
            return player1;
    }

    public JSONObject showGameBoardsForGraphic() {
        if (GameController.getInstance().getTurn() == PLAYER1) {
            return boardBuilderForGraphic(player1, player2);
        } else {
            return boardBuilderForGraphic(player2, player1);
        }
    }

    private JSONObject boardBuilderForGraphic(Player player1, Player player2) {
        JSONObject board = new JSONObject();

        board.put("LP", player1.getHealth());
        board.put("NI", player1.getUser().getNickname());
        board.put("IM", player1.getUser().getProfileImageID());
        board.put("Hand", getHandArray(player1));
        board.put("Monsters", getPlayersMonsterArray(player1));
        board.put("Spells", getPlayersSpellsArray(player1));
        board.put("Field", getPlayerFieldCard(player1));
        board.put("Grave", getLastGraveyardCard(player1));

        board.put("OPLP", player2.getHealth());
        board.put("OPNI", player2.getUser().getNickname());
        board.put("OPIM", player2.getUser().getProfileImageID());
        board.put("OPHand", getHandArray(player2));
        board.put("OPMonsters", getPlayersMonsterArray(player2));
        board.put("OPSpells", getPlayersSpellsArray(player2));
        board.put("OPField", getPlayerFieldCard(player2));
        board.put("OPGrave", getLastGraveyardCard(player2));

        return board;
    }

    private String getLastGraveyardCard(Player player) {
        if (player.getBoard().getGraveyard().get(player.getBoard().getGraveyard().size() - 1) == null) {
            return "None";
        } else {
            return player.getBoard().getGraveyard().get(player.getBoard().getGraveyard().size() - 1).getCardName();
        }
    }

    private JSONObject getPlayerFieldCard(Player player) {
        JSONObject field = new JSONObject();

        if (player.getBoard().getFieldCard() == null) {
            field.put("Name", "None");
            field.put("Activation", false);
        } else {
            field.put("Name", player.getBoard().getFieldCard().getCardName());
            field.put("Activation", (filedActivated && activatedFieldCard.getCardName().equals(player.getBoard().getFieldCard().getCardName())));
        }

        return field;
    }

    private JSONArray getHandArray(Player player) {
        JSONArray spells = new JSONArray();

        for (Card card : player.getBoard().getInHandCards()) {
            spells.put(card.getCardName());
        }

        return spells;
    }

    private JSONArray getPlayersSpellsArray(Player player) {
        JSONArray spells = new JSONArray();

        for (Integer position : player.getBoard().getSpellAndTrapsInField().keySet()) {
            JSONObject spell = new JSONObject();

            spell.put("Name", player.getBoard().getSpellAndTrapsInField().get(position).getCardName());
            spell.put("Position", position);
            spell.put("Activation", player.getBoard().getSpellAndTrapsInField().get(position).isActive());

            spells.put(spell);
        }

        return spells;
    }

    private JSONArray getPlayersMonsterArray(Player player) {
        JSONArray monsters = new JSONArray();

        for (Integer position : player.getBoard().getMonstersInField().keySet()) {
            JSONObject monster = new JSONObject();

            monster.put("Name", player.getBoard().getMonstersInField().get(position).getCardName());
            monster.put("Position", position);
            monster.put("AttackingFormat", player.getBoard().getMonstersInField().get(position).getAttackingFormat().string);
            monster.put("FaceUpSit", player.getBoard().getMonstersInField().get(position).getFaceUpSituation().string);

            monsters.put(monster);
        }

        return monsters;
    }
}
