package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewcontroller.MainView;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.HashMap;

public class ShopMenuPage extends Application {
    private static Stage stage;

    private ArrayList<ShopCard> allCards = new ArrayList<>();
    private ImageView[] cardImages = new ImageView[6];
    private Text[] prices = new Text[6];
    private Text[] cardsNumbers = new Text[6];
    private int pageindex = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ShopMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    private void loadAllCards() {
        JSONObject answer = MainView.getInstance().getAllCards();
        String type = answer.getString("Type");
        JSONArray value = answer.getJSONArray("Value");
        if (type.equals("Successful")) {
            for (int i = 0; i < value.length(); i++) {
                JSONObject cardInfo = value.getJSONObject(i);
                addCard(cardInfo);
            }
        }
    }

    private void addCard(JSONObject cardInfo) {
        String card = cardInfo.toString();
        String[] splitInfo = card.split(": ");
        allCards.add(new ShopCard(splitInfo[1], Integer.parseInt(splitInfo[0])));
    }

    private void loadSixCard() throws Exception {
        for (int i = pageindex*6; i < allCards.size(); i++) {
            int j = i - pageindex*6;
            cardImages[j].setImage(MainView.getInstance().getCardImage(allCards.get(i).getName()));
            prices[j].setText(String.valueOf(allCards.get(i).getPrice()));
            cardsNumbers[j].setText(String.valueOf(MainView.getInstance().getNumberOfBoughtCard(allCards.get(i).getName())));
        }
    }

    public void back(MouseEvent mouseEvent) {
    }

    public void previousPage(MouseEvent mouseEvent) {
    }

    public void nextPage(MouseEvent mouseEvent) {
    }
}

class ShopCard {
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    private String name;
    private int price;

    public ShopCard(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
