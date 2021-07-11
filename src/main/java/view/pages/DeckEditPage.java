package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewcontroller.MainView;

import java.util.ArrayList;

public class DeckEditPage extends Application {
    private static Stage stage;
    private final String deckName = DeckMenuPage.selectedDeck;
    @FXML
    public ImageView main1;
    public ImageView main2;
    public ImageView main3;
    public ImageView main4;
    public ImageView main5;
    public ImageView main6;
    public ImageView main7;
    public ImageView main8;
    public ImageView main9;
    public ImageView main10;
    public ImageView main11;
    public ImageView main12;
    public ImageView main13;
    public ImageView main14;
    public ImageView main15;
    public ImageView main16;
    public ImageView main17;
    public ImageView main18;
    public ImageView main19;
    public ImageView main20;
    public ImageView main21;
    public ImageView main22;
    public ImageView main23;
    public ImageView main24;
    public ImageView main25;
    public ImageView main26;
    public ImageView main27;
    public ImageView main28;
    public ImageView main29;
    public ImageView main30;
    public ImageView main31;
    public ImageView main32;
    public ImageView main33;
    public ImageView main34;
    public ImageView main35;
    public ImageView main36;
    public ImageView main37;
    public ImageView main38;
    public ImageView main39;
    public ImageView main40;
    public ImageView main41;
    public ImageView main42;
    public ImageView main43;
    public ImageView main44;
    public ImageView main45;
    public ImageView main46;
    public ImageView main47;
    public ImageView main48;
    public ImageView main49;
    public ImageView main50;
    public ImageView main51;
    public ImageView main52;
    public ImageView main53;
    public ImageView main54;
    public ImageView main55;
    public ImageView main56;
    public ImageView main57;
    public ImageView main58;
    public ImageView main59;
    public ImageView main60;
    public ImageView side1;
    public ImageView side2;
    public ImageView side3;
    public ImageView side4;
    public ImageView side5;
    public ImageView side6;
    public ImageView side7;
    public ImageView side8;
    public ImageView side9;
    public ImageView side10;
    public ImageView side11;
    public ImageView side12;
    public ImageView side13;
    public ImageView side14;
    public ImageView side15;
    public ArrayList<ImageView> mainCardImages;
    public ArrayList<ImageView> sideCardImages;
    public ArrayList<ImageView> userCardImages;
    public JSONArray mainCards;
    public JSONArray sideCards;
    public ArrayList<String> userCards;
    public Label deckLabel;
    public Label mainLabel;
    public Label sideLabel;
    public ScrollPane scrollPane;
    public ImageView zoomedImage;

    private void updateDeck() {
        MainView view = MainView.getInstance();
        JSONObject answer = view.showDeck(deckName);
        if (!answer.getString("Type").equals("Successful")) view.alertMaker(answer);

        JSONObject deckJson = answer.getJSONObject("Deck");

        boolean valid = deckJson.getBoolean("Valid");
        deckLabel.setText(deckName + (valid ? " (valid)" : " (invalid)"));
        if (valid) deckLabel.setStyle("-fx-text-fill: green;-fx-alignment: center");
        else deckLabel.setStyle("-fx-text-fill: red;-fx-alignment: center");

        mainLabel.setText("Main deck: " + deckJson.getInt("MainDeckNum") + " / 40~60");
        sideLabel.setText("Side deck: " + deckJson.getInt("SideDeckNum") + " / 15");

        zoomedImage.setImage(null);

        mainCards = deckJson.getJSONArray("MainDeck");
        sideCards = deckJson.getJSONArray("SideDeck");

        setCardImages(mainCards, mainCardImages);
        setCardImages(sideCards, sideCardImages);

        for (int i = mainCards.length(); i < 60; i++)
            mainCardImages.get(i).setImage(null);
        for (int i = sideCards.length(); i < 15; i++)
            sideCardImages.get(i).setImage(null);

    }

    private void setCardImages(JSONArray cards, ArrayList<ImageView> cardImages) {
        for (int i = 0; i < cards.length(); i++) {
            try {
                cardImages.get(i).setImage(MainView.getInstance().getCardImage(cards.get(i).toString()));
            } catch (Exception e) {
                cardImages.get(i).setImage(null);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Couldn't load some images");
                alert.show();
            }
        }
    }

    public void back() throws Exception {
        new DeckMenuPage().start(stage);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/DeckEditPage.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @FXML
    public void initialize() {
        mainCardImages = new ArrayList<>();
        sideCardImages = new ArrayList<>();

        mainCardImages.add(main1);
        mainCardImages.add(main2);
        mainCardImages.add(main3);
        mainCardImages.add(main4);
        mainCardImages.add(main5);
        mainCardImages.add(main6);
        mainCardImages.add(main7);
        mainCardImages.add(main8);
        mainCardImages.add(main9);
        mainCardImages.add(main10);
        mainCardImages.add(main11);
        mainCardImages.add(main12);
        mainCardImages.add(main13);
        mainCardImages.add(main14);
        mainCardImages.add(main15);
        mainCardImages.add(main16);
        mainCardImages.add(main17);
        mainCardImages.add(main18);
        mainCardImages.add(main19);
        mainCardImages.add(main20);
        mainCardImages.add(main21);
        mainCardImages.add(main22);
        mainCardImages.add(main23);
        mainCardImages.add(main24);
        mainCardImages.add(main25);
        mainCardImages.add(main26);
        mainCardImages.add(main27);
        mainCardImages.add(main28);
        mainCardImages.add(main29);
        mainCardImages.add(main30);
        mainCardImages.add(main31);
        mainCardImages.add(main32);
        mainCardImages.add(main33);
        mainCardImages.add(main34);
        mainCardImages.add(main35);
        mainCardImages.add(main36);
        mainCardImages.add(main37);
        mainCardImages.add(main38);
        mainCardImages.add(main39);
        mainCardImages.add(main40);
        mainCardImages.add(main41);
        mainCardImages.add(main42);
        mainCardImages.add(main43);
        mainCardImages.add(main44);
        mainCardImages.add(main45);
        mainCardImages.add(main46);
        mainCardImages.add(main47);
        mainCardImages.add(main48);
        mainCardImages.add(main49);
        mainCardImages.add(main50);
        mainCardImages.add(main51);
        mainCardImages.add(main52);
        mainCardImages.add(main53);
        mainCardImages.add(main54);
        mainCardImages.add(main55);
        mainCardImages.add(main56);
        mainCardImages.add(main57);
        mainCardImages.add(main58);
        mainCardImages.add(main59);
        mainCardImages.add(main60);

        sideCardImages.add(side1);
        sideCardImages.add(side2);
        sideCardImages.add(side3);
        sideCardImages.add(side4);
        sideCardImages.add(side5);
        sideCardImages.add(side6);
        sideCardImages.add(side7);
        sideCardImages.add(side8);
        sideCardImages.add(side9);
        sideCardImages.add(side10);
        sideCardImages.add(side11);
        sideCardImages.add(side12);
        sideCardImages.add(side13);
        sideCardImages.add(side14);
        sideCardImages.add(side15);

        ArrayList<ImageView> allCards = new ArrayList<>(mainCardImages);
        allCards.addAll(sideCardImages);

        for (ImageView card : allCards) {
            card.setOnMouseEntered(event -> {
                zoomedImage.setImage(card.getImage());
            });

            card.setOnMouseExited(event -> {
                zoomedImage.setImage(null);
            });
        }

        updateDeck();
    }
}
