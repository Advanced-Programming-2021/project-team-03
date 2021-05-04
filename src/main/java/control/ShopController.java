package control;

import model.card.Card;
import model.user.User;

import java.util.HashMap;

public class ShopController {
    private static ShopController shopController;
    private final HashMap<String, Card> allCards;

    private ShopController() {
        allCards = new HashMap<>();
    }

    public static ShopController getInstance() {
        if (shopController == null)
            shopController = new ShopController();
        return shopController;
    }

    public boolean doesCardExists(String cardName) {
        return allCards.containsKey(cardName);
    }

    public boolean doesPlayerHaveEnoughMoney(String username, String cardName) {
        return allCards.get(cardName).getPrice() <= User.getUserByUsername(username).getBalance();
    }

    public void buyCard(String username, String cardName) {
        //TODO
    }

    public HashMap<String, Card> getAllCards() {
        return allCards;
    }
}
