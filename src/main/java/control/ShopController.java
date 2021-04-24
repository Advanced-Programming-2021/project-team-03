package control;

import model.card.Card;

public class ShopController {
    private static ShopController shopController;

    private ShopController() {
    }

    public static ShopController getInstance() {
        if (shopController == null)
            shopController = new ShopController();
        return shopController;
    }

    public String buyCard(String card) {
        //TODO
        return null;
    }

    public String showAllCards() {
        //TODO
        return null;
    }
}
