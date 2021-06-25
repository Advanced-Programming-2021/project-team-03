package control.game;

import control.MainController;
import model.card.*;
import model.enums.AttackingFormat;
import model.enums.FaceUpSituation;
import model.enums.MonsterTypes;
import model.enums.TrapNames;
import model.game.Board;
import model.game.Game;
import model.game.Player;
import model.game.PlayerTurn;

import java.util.Random;

import static control.game.UpdateEnum.TEXCHANGER_ACTIVATED;
import static model.enums.FaceUpSituation.FACE_DOWN;
import static model.enums.SpellAndTrapIcon.*;

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

        if (canSetSpellsInHand()) {
            setSpell();
            AIPlayLog.append("AI Set a spell in field\n");
            AIPlayLog.append("AI Activate a spell effect\n");
        }

        while (canAttack()) {
            attack();
            AIPlayLog.append("AI attacked!!!\n");
        }
        MainController.getInstance().sendPrintRequestToView(AIPlayLog.toString());
    }

    private void attack() {
        /*return the result of attack in a string*/
        String attackingPlayerUsername = game.getPlayerByTurn(PlayerTurn.PLAYER2).getUser().getUsername();
        Board attackingPlayerBoard = bot.getBoard();
        Board opponentBoard = opponent.getBoard();
        int position = getOpponentMonsterPosition();
        if (position == -1) {
            checkDirectAttack();
            return;
        }
        Monster attackingMonster = bot.getBoard().getMonsterInFieldByPosition(selectedMonsterIndex);
        Monster opponentMonster = game.getPlayerOpponentByTurn(PlayerTurn.PLAYER2).getBoard().getMonsterInFieldByPosition(position);
        AttackingFormat opponentMonsterFormat = game.getPlayerOpponentByTurn(PlayerTurn.PLAYER2).getBoard().getMonsterInFieldByPosition(position).getAttackingFormat();
        FaceUpSituation opponentMonsterFaceUpSit = game.getPlayerOpponentByTurn(PlayerTurn.PLAYER2).getBoard().getMonsterInFieldByPosition(position).getFaceUpSituation();

        int attackingDef = attackingMonster.getAttackingPower() - opponentMonster.getAttackingPower();
        int defendingDef = attackingMonster.getAttackingPower() - opponentMonster.getDefensivePower();
        StringBuilder answerString = new StringBuilder();

        if (GameController.getInstance().activeTraps(TrapNames.NEGATE_ATTACK)) return;

        if (GameController.getInstance().activeTraps(TrapNames.MAGIC_CYLINDER)) return;

        if (GameController.getInstance().activeTraps(TrapNames.MIRROR_FORCE)) return;

        gameUpdate.addMonstersToAttackedMonsters(attackingMonster);
        if (opponentMonster.getCardName().equals("The Calculator")) {
            int attack = AllMonsterEffects.getInstance().theCalculatorAtkPower(attackingPlayerBoard);
            attackingDef += attack;
            answerString.append("\nThe Calculator Monster activated, giving you ")
                    .append(attack).append(" attack power!!!\n");
        }

        if (opponentMonster.getCardName().equals("Suijin") && !AllMonsterEffects.getInstance().isSuijinActivatedBefore(gameUpdate)) {
            answerString.append(AllMonsterEffects.getInstance().suijinEffect(game, gameUpdate, attackingPlayerUsername,
                    attackingPlayerBoard, attackingMonster, opponentMonster, opponentMonsterFormat, opponentMonsterFaceUpSit));
            MainController.getInstance().sendPrintRequestToView(answerString.toString());
            return;
        }
        if (opponentMonster.getCardName().equals("Texchanger") && !AllMonsterEffects.getInstance().isTexChangerActivatedBefore(gameUpdate)) {
            gameUpdate.getAllUpdates().put(TEXCHANGER_ACTIVATED, opponentMonster);
            answerString.append("Texchanger Monster Activated!!!");
            MainController.getInstance().sendPrintRequestToView(answerString.toString());
            return;
        }
        if (opponentMonster.getCardName().equals("Marshmallon")) {
            answerString.append(AllMonsterEffects.getInstance().marshmallonEffect(game, opponentMonster, opponentMonsterFaceUpSit,
                    game.getPlayerByTurn(PlayerTurn.PLAYER2), opponentMonsterFormat
                    , attackingMonster, attackingPlayerBoard, opponentBoard, attackingDef, defendingDef, gameUpdate, PlayerTurn.PLAYER2));
            MainController.getInstance().sendPrintRequestToView(answerString.toString());
            return;
        }
        switch (opponentMonsterFormat) {
            case ATTACKING -> {
                if (attackingDef == 0) {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdate.addCardToGraveyard(attackingMonster);
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdate.addCardToGraveyard(opponentMonster);
                    answerString.append("both you and your opponent monster cards are destroyed and no one receives damage");
                } else if (attackingDef > 0) {
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdate.addCardToGraveyard(opponentMonster);
                    if (opponentMonster.getCardName().equals("Exploder Dragon")) {
                        answerString.append("\nExploder Dragon activated!!!\n");
                        attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                        attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                        gameUpdate.addCardToGraveyard(attackingMonster);
                    } else {
                        game.getPlayerOpponentByTurn(PlayerTurn.PLAYER2).decreaseHealthByAmount(attackingDef);
                        answerString.append("your opponent’s monster is destroyed and your opponent receives ").append(attackingDef).append(" battle damage");
                    }
                } else {
                    attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                    attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                    gameUpdate.addCardToGraveyard(attackingMonster);
                    if (attackingMonster.getCardName().equals("Exploder Dragon")) {
                        answerString.append("\nExploder Dragon activated!!!\n");
                        opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                        opponentBoard.addCardToGraveyard(opponentMonster);
                        gameUpdate.addCardToGraveyard(opponentMonster);
                    } else {
                        game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(attackingDef);
                        answerString.append("Your monster card is destroyed and you received ").append(attackingDef).append(" battle damage");
                    }
                }
                MainController.getInstance().sendPrintRequestToView(answerString.toString());
            }
            case DEFENDING -> {
                if (opponentMonsterFaceUpSit == FACE_DOWN) {
                    opponentMonster.setFaceUpSituation(FaceUpSituation.FACE_UP);
                    gameUpdate.flipCard(opponentMonster);
                    answerString.append("opponent’s monster card was ").append(opponentMonster.getCardName());
                }
                if (defendingDef == 0) {
                    answerString.append("no card is destroyed!");
                } else if (defendingDef > 0) {
                    opponentBoard.removeCardFromField(opponentBoard.getMonsterPosition(opponentMonster), true);
                    opponentBoard.addCardToGraveyard(opponentMonster);
                    gameUpdate.addCardToGraveyard(opponentMonster);
                    answerString.append("the defense position monster is destroyed!");
                    if (opponentMonster.getCardName().equals("Exploder Dragon")) {
                        answerString.append("\nExploder Dragon activated!!!\n");
                        attackingPlayerBoard.removeCardFromField(attackingPlayerBoard.getMonsterPosition(attackingMonster), true);
                        attackingPlayerBoard.addCardToGraveyard(attackingMonster);
                        gameUpdate.addCardToGraveyard(attackingMonster);
                    }
                } else {
                    game.getPlayerByName(attackingPlayerUsername).decreaseHealthByAmount(defendingDef);
                    answerString.append("no card is destroyed and you received ").append(defendingDef).append(" battle damage!");
                }
                MainController.getInstance().sendPrintRequestToView(answerString.toString());
            }
        }
    }

    private void checkDirectAttack() {
        if (canAttackDirectly())
            attackDirectlyToTheOpponent();
    }

    private boolean canAttackDirectly() {
        return game.getPlayerOpponentByTurn(PlayerTurn.PLAYER2).getBoard().getMonstersInField().size() == 0;
    }

    private int attackDirectlyToTheOpponent() {
        /*return the opponents receiving damage*/
        Monster attackingMonster = bot.getBoard().getMonsterInFieldByPosition(selectedMonsterIndex);
        int attackingPower = attackingMonster.getAttackingPower();
        game.getPlayerOpponentByTurn(PlayerTurn.PLAYER2).decreaseHealthByAmount(attackingPower);
        gameUpdate.addMonstersToAttackedMonsters(attackingMonster);
        return attackingPower;
    }

    private int getOpponentMonsterPosition() {
        for (Integer integer : opponent.getBoard().getMonstersInField().keySet()) {
            return integer;
        }
        return -1;
    }

    private boolean canAttack() {

        for (Monster monster : bot.getBoard().getMonstersInField().values()) {
            if (!gameUpdate.didMonsterAttack(monster)) {
                selectedMonsterIndex = bot.getBoard().getMonsterPosition(monster);
                return true;
            } else
                return false;
        }
        return false;
    }

    private void setSpell() {
        Board board = bot.getBoard();
        Board opponentBoard = opponent.getBoard();
        Card card = bot.getBoard().getInHandCards().get(selectedHandIndex);
        if (card instanceof SpellAndTrap) {
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
            activateSpellEffect(card);
        }
    }

    private void activateSpellEffect(Card card) {
        boolean trapActivate = GameController.getInstance().activeTraps(TrapNames.MAGIC_JAMAMER);
        if (trapActivate)
            return;
        SpellAndTrap spell = (SpellAndTrap) card;
        Board board = bot.getBoard();
        if (spell.getIcon() == FIELD) {
            spell.setActive(true);
            game.setActivatedFieldCard(spell);
            game.setFiledActivated(true);
        } else if (spell.getIcon() == EQUIP)
            AllSpellsEffects.getInstance().equipmentActivator(board, spell, game, gameUpdate, PlayerTurn.PLAYER2);
        else {
            AllSpellsEffects.getInstance().cardActivator(spell, game, gameUpdate, PlayerTurn.PLAYER2);
        }
    }

    private boolean canSetSpellsInHand() {
        if (bot.getBoard().getSpellAndTrapsInField().size() >= 5)
            return false;
        for (Card card : bot.getBoard().getInHandCards()) {
            if (card instanceof SpellAndTrap)
                return true;
        }
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
        Card card = bot.getBoard().getInHandCards().get(selectedHandIndex);
        Random random = new Random();
        if (card instanceof Monster) {
            Monster monster = (Monster) card;
            if (monster.getLevel() < 5) {
                board.setOrSummonMonsterFromHandToFiled(card, "Set");
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
        if (bot.getBoard().getMonstersInField().size() >= 5)
            return false;
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
