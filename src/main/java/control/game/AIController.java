package control.game;

import control.MainController;
import model.card.AllMonsterEffects;
import model.card.Card;
import model.card.Monster;
import model.card.SpellAndTrap;
import model.enums.AttackingFormat;
import model.enums.MonsterTypes;
import model.game.Board;
import model.game.Game;
import model.game.Player;
import model.game.PlayerTurn;
import org.json.JSONObject;

import java.util.Random;

import static model.enums.FaceUpSituation.FACE_UP;
import static model.enums.SpellAndTrapIcon.FIELD;

public class AIController {
    private static AIController AIController;

    private AIController() {
    }

    public static AIController getInstance() {
        if (AIController == null) {
            AIController = new AIController();
        }
        return AIController;
    }

    private Game game;
    private Update gameUpdate;
    private Player bot;
    private Player opponent;
    private int selectedHandIndex;
    private int selectedMonsterIndex;
    private int selectedSpellIndex;
    private boolean summon;

    public void initialize(Game game, Update gameUpdates) {
        this.game = game;
        this.gameUpdate = gameUpdates;
        this.bot = game.getPlayer2();
        this.opponent = game.getPlayer1();
    }

    public void play() {
        StringBuilder AIPlayLog = new StringBuilder();
        AIPlayLog.append("Now it's AI turn\n");
        bot.getBoard().addCardFromRemainingToInHandCards();
        AIPlayLog.append("AI Draws a card\n");
        if (canSummonOrSet()) {
            boolean summon = summonOrSet();
            if (summon) {
                summon();
                AIPlayLog.append("AI Summons a monster in attacking position\n");
            } else {
                set();
                AIPlayLog.append("AI Set a monster in defending position\n");
            }
        }

        while (canSetSpellsInHand()) {
            setSpell();
            AIPlayLog.append("AI Set a spell in field\n");
        }

        while (canAttack()) {
            attack();
            AIPlayLog.append("AI attacked!!!\n");
        }
        MainController.getInstance().sendPrintRequestToView(AIPlayLog.toString());
    }

    private void attack() {

    }

    private boolean canAttack() {

        return false;
    }

    private void setSpell() {

    }

    private boolean canSetSpellsInHand() {

        return false;
    }

    private boolean summonOrSet() {
        int max = 0;
        for (int i = 0; i < bot.getBoard().getInHandCards().size(); i++) {
            Card card = bot.getBoard().getInHandCards().get(i);
            if (card instanceof Monster) {
                Monster monster = (Monster) card;
                if (monster.getBaseAttack() >= max) {
                    max = monster.getBaseAttack();
                    selectedHandIndex = i;
                    summon = true;
                }
            }
        }

        for (int i = 0; i < bot.getBoard().getInHandCards().size(); i++) {
            Card card = bot.getBoard().getInHandCards().get(i);
            if (card instanceof Monster) {
                Monster monster = (Monster) card;
                if (monster.getDefensivePower() >= max) {
                    max = monster.getDefensivePower();
                    selectedHandIndex = i;
                    summon = false;
                }
            }
        }

        return summon;
    }

    private void set() {
        Board board = bot.getBoard();
        Board opponentBoard = opponent.getBoard();
        Card card = bot.getBoard().getInHandCards().get(selectedHandIndex);
        if (card instanceof Monster) {
            board.setOrSummonMonsterFromHandToFiled(card, "Set");
        } else {
            SpellAndTrap spellAndTrap = (SpellAndTrap) card;
            if (spellAndTrap.getIcon() == FIELD) {
                if (!game.isFiledActivated()) {
                    board.setFieldCard(gameUpdate, spellAndTrap);
                } else if (board.getFieldCard() == game.getActivatedFieldCard()) {
                    SpellAndTrap opponentFieldCard = (SpellAndTrap) opponentBoard.getFieldCard();
                    if (opponentFieldCard != null && opponentFieldCard.isActive()) {
                        game.setActivatedFieldCard(opponentFieldCard);
                        game.setFiledActivated(true);
                    } else {
                        game.setFiledActivated(false);
                        game.setActivatedFieldCard(null);
                    }
                    board.setFieldCard(gameUpdate, spellAndTrap);
                } else {
                    board.setFieldCard(gameUpdate, spellAndTrap);
                }
            } else {
                board.setSpellAndTrapsInField(spellAndTrap);
            }
        }
    }

    private void summon() {
        Monster monster = (Monster) bot.getBoard().getInHandCards().get(selectedHandIndex);
        Board board = bot.getBoard();
        Random random = new Random();
        if (monster.getCardName().equals("Terratiger, the Empowered Warrior")) {
            board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
            int position = random.nextInt(board.getInHandCards().size());
            Card card = board.getInHandCardByPosition(position);
            if (card instanceof Monster) {
                Monster secondMonster = (Monster) card;
                if (secondMonster.getLevel() < 4 &&
                        board.getMonstersInField().size() < 5 &&
                        secondMonster.getType() == MonsterTypes.NORMAL) {
                    board.setOrSummonMonsterFromHandToFiled(secondMonster, "Summon");
                    secondMonster.setAttackingFormat(AttackingFormat.DEFENDING);
                    return;
                }
            }
            return;
        }

        if (monster.getCardName().equals("The Tricky")) {
            int position = random.nextInt(board.getInHandCards().size());
            Card card = board.getInHandCardByPosition(position);
            if (card != null && !card.equals(monster)) {
                board.addCardToGraveyard(card);
                board.removeCardFromHand(card);
                board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
            }
            return;
        }
        if (monster.getLevel() < 5) {
            board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
            if (monster.getCardName().equals("Mirage Dragon")) {
                AllMonsterEffects.getInstance().mirageDragonEffect(gameUpdate, PlayerTurn.PLAYER2, game);
            }
            return;
        }

        if (monster.getLevel() == 5 || monster.getLevel() == 6) {
            int position = random.nextInt(board.getMonstersInField().size());
            Monster tribute = board.getMonsterInFieldByPosition(position);
            if (tribute == null) {
                return;
            }
            board.addCardToGraveyard(tribute);
            board.removeCardFromField(position, true);
            board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
        } else if (monster.getLevel() > 6) {
            int firstPosition = random.nextInt(board.getMonstersInField().size());
            Monster firstMonster = board.getMonsterInFieldByPosition(firstPosition);
            int secondPosition = random.nextInt(board.getMonstersInField().size());
            Monster secondMonster = board.getMonsterInFieldByPosition(secondPosition);
            if (firstMonster == null || secondMonster == null) {
                return;
            }
            board.addCardToGraveyard(firstMonster);
            board.addCardToGraveyard(secondMonster);
            board.removeCardFromField(firstPosition, true);
            board.removeCardFromField(secondPosition, true);
            board.setOrSummonMonsterFromHandToFiled(monster, "Summon");
        }
    }

    private boolean canSummonOrSet() {
        for (Card card : bot.getBoard().getInHandCards()) {
            if (card instanceof Monster) {
                Monster monster = (Monster) card;
                if (monster.getLevel() < 5)
                    return true;
                else return checkForTribute(monster.getLevel());
            }
        }
        return false;
    }

    private boolean checkForTribute(int level) {
        if (level < 7) {
            return bot.getBoard().getMonstersInField().size() > 0;
        } else {
            return bot.getBoard().getMonstersInField().size() > 1;
        }
    }


}
