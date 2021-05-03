package control;

import model.card.Card;

import java.util.ArrayList;

public class ShopController {
    private static ShopController shopController;
    private final ArrayList<Card> allCards;

    private ShopController() {
        allCards = new ArrayList<>();
    }

    public static ShopController getInstance() {
        if (shopController == null)
            shopController = new ShopController();
        return shopController;
    }

    public boolean doesCardExists(String cardName) {
        //TODO
        return false;
    }

    public boolean doesPlayerHaveEnoughMoney(String username, String cardName) {
        //TODO
        return false;
    }

    public void buyCard(String username, String cardName) {
        //TODO
    }

    public ArrayList<Card> getAllCards() {
        return allCards;
    }
}
