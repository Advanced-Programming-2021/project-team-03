package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
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

public class ShopMenuPage extends Application {
    private static Stage stage;
    public Text balance;
    public ImageView I1;
    public Text P1;
    public Text N1;
    public Text messageText;
    public ImageView I2;
    public ImageView I6;
    public ImageView I5;
    public ImageView I4;
    public ImageView I3;
    public Text N3;
    public Text N4;
    public Text N5;
    public Text N2;
    public Text N6;
    public Text P3;
    public Text P2;
    public Text P4;
    public Text P5;
    public Text P6;

    private ArrayList<ShopCard> allCards = new ArrayList<>();
    private ImageView[] cardImages = new ImageView[6];
    private Text[] prices = new Text[6];
    private Text[] cardsNumbers = new Text[6];
    private int pageIndex = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ShopMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @FXML
    public void initialize() {
        loadAllCards();
        loadStartingTextsAndImages();
        loadSixCard();
    }

    private void loadAllCards() {
        JSONObject answer = MainView.getInstance().getAllCards();
        String type = answer.getString("Type");
        JSONArray value = answer.getJSONArray("Value");
        if (type.equals("Successful")) {
            for (int i = 0; i < value.length(); i++) {
                addCard(value.getString(i));
            }
        }
    }

    private void addCard(String card) {
        String[] splitInfo = card.split(": ");
        allCards.add(new ShopCard(splitInfo[1], Integer.parseInt(splitInfo[0])));
    }

    private void loadSixCard() {
        for (int i = pageIndex * 6; i < pageIndex * 6 + 6; i++) {
            int j = i - pageIndex * 6;
            try {
                System.out.println(allCards.get(i).getName());
                Image image = MainView.getInstance().getCardImage(allCards.get(i).getName());
                System.out.println("check");
                cardImages[j].setImage(image);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            prices[j].setText(String.valueOf(allCards.get(i).getPrice()));
            cardsNumbers[j].setText(String.valueOf(MainView.getInstance().getNumberOfBoughtCard(allCards.get(i).getName())));
        }
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void previousPage(MouseEvent mouseEvent) {
        if (pageIndex != 0) {
            pageIndex--;
            loadSixCard();
        }
    }

    public void nextPage(MouseEvent mouseEvent) {
        if (pageIndex != Math.ceil(allCards.size() / 6.0) - 1) {
            pageIndex++;
            loadSixCard();
        }
    }

    private void loadStartingTextsAndImages() {
        cardImages[0] = I1;
        cardImages[1] = I2;
        cardImages[2] = I3;
        cardImages[3] = I4;
        cardImages[4] = I5;
        cardImages[5] = I6;
        prices[0] = P1;
        prices[1] = P2;
        prices[2] = P3;
        prices[3] = P4;
        prices[4] = P5;
        prices[5] = P6;
        cardsNumbers[0] = N1;
        cardsNumbers[1] = N2;
        cardsNumbers[2] = N3;
        cardsNumbers[3] = N4;
        cardsNumbers[4] = N5;
        cardsNumbers[5] = N6;
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
