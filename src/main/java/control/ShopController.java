package control;

import control.databaseController.DatabaseException;
import model.card.Card;
import model.user.User;

public class ShopController {
    private static ShopController shopController;

    private ShopController() {
    }

    public static ShopController getInstance() {
        if (shopController == null)
            shopController = new ShopController();
        return shopController;
    }

    public boolean doesCardExist(String cardName) {
        return Card.getCardByName(cardName) != null;
    }

    public boolean doesPlayerHaveEnoughMoney(String username, String cardName) {
        return Card.getCardByName(cardName).getPrice() <= User.getByUsername(username).getBalance();
    }

    public void buyCard(String username, String cardName) {
        try {
            User.getByUsername(username).addCard(Card.getCardByName(cardName));
            User.getByUsername(username).increaseBalance(-Card.getCardByName(cardName).getPrice());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}
