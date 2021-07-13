package view.pages;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewcontroller.MainView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ShopMenuPage extends Application {
    private static Stage stage;

    @FXML
    private Scene scene;
    public Text balance;
    public Text messageText;
    public ImageView I1;
    public ImageView I2;
    public ImageView I3;
    public ImageView I4;
    public ImageView I5;
    public ImageView I6;
    public Text N1;
    public Text N2;
    public Text N3;
    public Text N4;
    public Text N5;
    public Text N6;
    public Text P1;
    public Text P2;
    public Text P3;
    public Text P4;
    public Text P5;
    public Text P6;
    public Label NL1;
    public Label NL2;
    public Label NL3;
    public Label NL4;
    public Label NL5;
    public Label NL6;
    public Label PL1;
    public Label PL2;
    public Label PL3;
    public Label PL4;
    public Label PL5;
    public Label PL6;

    private ArrayList<ShopCard> allCards = new ArrayList<>();
    private ImageView[] cardImages = new ImageView[6];
    private Text[] prices = new Text[6];
    private Text[] cardsNumbers = new Text[6];
    private Label[] priceLabels = new Label[6];
    private Label[] cardNumberLabels = new Label[6];
    private int pageIndex = 0;
    private Image canNotBuyIcon = new Image(String.valueOf(getClass().getResource("/assets/icon/canNotBuyIcon.png")));

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ShopMenu.fxml"));
        this.scene = new Scene(startingPane);
        primaryStage.setScene(scene);
        stage = primaryStage;
    }

    @FXML
    public void initialize() throws IOException {
        loadAllCards();
        loadStartingTextsAndImages();
        setAllImagesOnMouseClickedFunction();
        setAllImagesOnMouseEnteredFunction();
        setAllImagesOnMouseExitedFunction();
        loadSixCard();
        loadBalance();
    }

    private void loadBalance() {
        balance.setText(String.valueOf(MainView.getInstance().getBalance()));
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
        if (pageIndex == Math.ceil(allCards.size() / 6.0) - 1) {
            for (int i = pageIndex * 6; i < allCards.size(); i++) {
                int j = i - pageIndex * 6;
                try {
                    Image image = MainView.getInstance().getCardImage(allCards.get(i).getName());
                    cardImages[j].setImage(image);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                prices[j].setText(String.valueOf(allCards.get(i).getPrice()));
                cardsNumbers[j].setText(String.valueOf(MainView.getInstance().getNumberOfBoughtCard(allCards.get(i).getName())));
            }
            for (int i = allCards.size(); i < pageIndex * 6 + 6; i++) {
                int j = i - pageIndex * 6;
                cardImages[j].setVisible(false);
                prices[j].setVisible(false);
                cardsNumbers[j].setVisible(false);
                priceLabels[j].setVisible(false);
                cardNumberLabels[j].setVisible(false);
            }
        } else {
            for (int i = pageIndex * 6; i < pageIndex * 6 + 6; i++) {
                int j = i - pageIndex * 6;
                cardImages[j].setVisible(true);
                prices[j].setVisible(true);
                cardsNumbers[j].setVisible(true);
                priceLabels[j].setVisible(true);
                cardNumberLabels[j].setVisible(true);
                try {
                    Image image = MainView.getInstance().getCardImage(allCards.get(i).getName());
                    cardImages[j].setImage(image);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                prices[j].setText(String.valueOf(allCards.get(i).getPrice()));
                cardsNumbers[j].setText(String.valueOf(MainView.getInstance().getNumberOfBoughtCard(allCards.get(i).getName())));
            }
        }
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void previousPage(MouseEvent mouseEvent) {
        if (pageIndex != 0) {
            messageText.setText("");
            pageIndex--;
            loadSixCard();
        }
    }

    public void nextPage(MouseEvent mouseEvent) {
        if (pageIndex != Math.ceil(allCards.size() / 6.0) - 1) {
            messageText.setText("");
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
        priceLabels[0] = PL1;
        priceLabels[1] = PL2;
        priceLabels[2] = PL3;
        priceLabels[3] = PL4;
        priceLabels[4] = PL5;
        priceLabels[5] = PL6;
        cardNumberLabels[0] = NL1;
        cardNumberLabels[1] = NL2;
        cardNumberLabels[2] = NL3;
        cardNumberLabels[3] = NL4;
        cardNumberLabels[4] = NL5;
        cardNumberLabels[5] = NL6;
    }

    private void setAllImagesOnMouseClickedFunction() {
        for (int i = 0; i < 6; i++) {
            ImageView imageView = cardImages[i];
            int finalI = i;
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    int cardIndex = pageIndex * 6 + finalI;
                    ShopCard card = allCards.get(cardIndex);
                    if (card.getPrice() <= Integer.parseInt(balance.getText())){
                        buyCard(finalI);
                    }
                }
            });
        }
    }

    private void setAllImagesOnMouseEnteredFunction(){
        for (int i = 0; i < 6; i++) {
            ImageView imageView = cardImages[i];
            int finalI = i;
            imageView.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    int cardIndex = pageIndex * 6 + finalI;
                    ShopCard card = allCards.get(cardIndex);
                    if (card.getPrice() > Integer.parseInt(balance.getText())){
                        stage.getScene().setCursor(new ImageCursor(canNotBuyIcon));
                    }
                }
            });
        }
    }

    private void setAllImagesOnMouseExitedFunction(){
        for (int i = 0; i < 6; i++) {
            ImageView imageView = cardImages[i];
            imageView.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    stage.getScene().setCursor(Cursor.DISAPPEAR);
                }
            });
        }
    }

    private void buyCard(int finalI) {
        messageText.setText("");
        int cardIndex = pageIndex * 6 + finalI;
        ShopCard card = allCards.get(cardIndex);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Buy Card Confirmation");
        alert.setHeaderText("Buy Card Confirmation");
        alert.setContentText("Are you sure?\n" +
                "Do you want to buy " + card.getName() + "card for " + card.getPrice() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            JSONObject answer = MainView.getInstance().buyCard(card.getName());
            String type = answer.getString("Type");
            String value = answer.getString("Value");
            if (type.equals("Successful")) {
                messageText.setFill(Color.DARKGREEN);
                loadBalance();
                cardsNumbers[finalI].setText(String.valueOf(MainView.getInstance().getNumberOfBoughtCard(card.getName())));
            } else {
                messageText.setFill(Color.DARKRED);
            }
            messageText.setText(value);
        }
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
