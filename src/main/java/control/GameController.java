package control;

import model.card.Card;
import model.card.Monster;
import model.game.Game;
import model.user.User;

import java.util.regex.Matcher;

enum Phase {
    DRAW,
    STANDBY,
    FIRST_MAIN,
    BATTLE,
    SECOND_MAIN,
}

public class GameController {
    private Card selectedCard;
    private Phase currentPhase;
    private Game game;

    public String makeNewDuel(String firstUsername, User SecondUsername, int roundsCount) {
        //TODO
        return null;
    }

    public static String selectCard(String cardType, int position) {
        //TODO
        return null;
    }

    public static String deselectCard(String cardType, int position) {
        //TODO
        return null;
    }

    private static void changePhase() {

    }

    public static String summonCard() {
        //TODO
        return null;
    }

    public static String setCard(String type) {
        //TODO
        return null;
    }

    public static String setCardStandFormat() {
        //TODO
        return null;
    }

    public static String attackToRivalCard(int targetCardPosition) {
        //TODO
        return null;
    }

    public static String DirectAttackToRivalHealth() {
        //TODO
        return null;
    }

    public static String activateSpellCardInOnwTurn() {
        //TODO
        return null;
    }

    public static String activateSpellCardInRivalTurn() {
        //TODO
        return null;
    }

    public static String ritualSummon() {
        //TODO
        return null;
    }

    public static String specialSummon() {
        //TODO
        return null;
    }

    public static String showGraveyard(String whichGraveyard) {
        //TODO
        return null;
    }

    private static void rewardPlayers() {

    }

    public static String surrender(String username) {
        //TODO
        return null;
    }

}
