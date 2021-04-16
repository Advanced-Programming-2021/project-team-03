package view;

import model.game.Game;
import model.user.User;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static Matcher regexMatcher;

    private static void getRegexMatcher(String command, String regex, boolean findMatches) {
        regexMatcher = Pattern.compile(regex).matcher(command);
        if (findMatches)
            regexMatcher.find();
    }

    public static void registerMenu() {

    }

    public static void mainMenu(User user) {

    }

    public static void deckMenu(User user) {

    }

    public static void duelMenu(User user) {

    }

    public static void shopMenu(User user) {

    }

    public static void importExportMenu(User user) {

    }

    public static void profileMenu(User user) {

    }

    public static void scoreBoard() {

    }
}
