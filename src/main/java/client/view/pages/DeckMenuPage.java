package client.view.pages;

import client.view.controller.MainView;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeckMenuPage extends Application {
    public static String selectedDeck;
    private static Stage stage;
    private static VBox selectedVbox;
    public ScrollPane scrollPane;
    public TextField newDeckNameField;
    private ArrayList<ViewDeck> allDecks;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/DeckMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @FXML
    public void initialize() {
        if (!loadAllDecks()) return;

        VBox vbox = new VBox();
        Label label = new Label("Your decks");
        label.setCenterShape(true);
        label.setStyle("-fx-font-size : 23px;" + "-fx-text-fill : green;");
        vbox.getChildren().add(label);
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(20));
        scrollPane.setPadding(new Insets(5));

        allDecks.forEach(viewDeck -> vbox.getChildren().add(deckVBoxGenerator(viewDeck)));
        scrollPane.setContent(vbox);
    }

    private Node deckVBoxGenerator(ViewDeck deck) {
        String normalBorderStyle = "-fx-border-width: 3;-fx-border-color: gray;-fx-border-radius: 20;" +
                "-fx-background-color: rgba(199, 226, 240, 0.8);-fx-background-radius: 20";
        String selectedBorderStyle = "-fx-border-width: 5;" + "-fx-border-color: rgba(62, 83, 144, 1);" + "-fx-border-radius: 20;";
        String hoveredBorderStyle = "-fx-border-width: 3;" + "-fx-border-color: rgba(163, 102, 117, 0.8);" + "-fx-border-radius: 20;";

        VBox deckVBox = new VBox();
        deckVBox.setPadding(new Insets(10));
        deckVBox.setAlignment(Pos.CENTER);
        deckVBox.setStyle(normalBorderStyle);

        Label nameLabel = new Label(deck.name);
        nameLabel.setStyle("-fx-font-size : 20px");
        deckVBox.getChildren().add(nameLabel);

        Label mainDeckLabel = new Label("Main deck cards: " + deck.mainDeckCardsNum);
        mainDeckLabel.setAlignment(Pos.CENTER);
        mainDeckLabel.setStyle("-fx-font-size : 16px;" + "-fx-alignment: center;");
        deckVBox.getChildren().add(mainDeckLabel);

        Label sideDeckLabel = new Label("Side deck cards: " + deck.sideDeckCardsNum);
        sideDeckLabel.setStyle("-fx-font-size : 16px");
        deckVBox.getChildren().add(sideDeckLabel);

        Label validLabel = new Label(deck.isValid ? "Valid" : "Invalid");
        validLabel.setStyle("-fx-font-size : 16px;"
                + "-fx-text-fill : " + ((deck.isValid) ? "green" : "red"));
        deckVBox.getChildren().add(validLabel);

        if (deck.isActive) {
            Label activeLabel = new Label("Active deck");
            activeLabel.setStyle("-fx-font-size : 16px;" + "-fx-text-fill : green");
            deckVBox.getChildren().add(activeLabel);
        }

        deckVBox.setOnMouseEntered(event -> {
            if (!deck.popup.isShowing()) {
                deck.popup.setAnchorX(stage.getX() + 450);
                deck.popup.setAnchorY(stage.getY() + 110);
                deck.popup.show(stage);
            }
            if (!deck.name.equals(selectedDeck)) deckVBox.setStyle(hoveredBorderStyle);
        });

        deckVBox.setOnMouseExited(event -> {
            if (deck.popup.isShowing()) deck.popup.hide();
            if (!deck.name.equals(selectedDeck)) {
                deckVBox.setStyle(normalBorderStyle);
            }
        });

        deckVBox.setOnMouseClicked(event -> {
            if (deck.name.equals(selectedDeck)) {
                deckVBox.setStyle(normalBorderStyle);
                selectedDeck = null;
                selectedVbox = null;
            } else {
                deckVBox.setStyle(selectedBorderStyle);
                if (selectedVbox != null)
                    selectedVbox.setStyle(normalBorderStyle);
                selectedDeck = deck.name;
                selectedVbox = deckVBox;
            }
        });

        return deckVBox;
    }

    private boolean loadAllDecks() {
        allDecks = new ArrayList<>();
        JSONObject answer = MainView.getInstance().getUserDecks();
        if (!answer.getString("Type").equals("Successful")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to get user decks");
            alert.show();
            return false;
        }

        JSONArray decks = new JSONArray(answer.getString("Decks"));

        for (Object deckObject : decks) {
            JSONObject deckJson = new JSONObject(deckObject.toString());
            ViewDeck deck = new ViewDeck(deckJson.getString("Name"),
                    deckJson.getInt("MainDeckNum"),
                    deckJson.getInt("SideDeckNum"),
                    deckJson.getBoolean("Valid"),
                    deckJson.getBoolean("Active"));
            allDecks.add(deck);
        }
        return true;
    }


    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void createNewDeck(MouseEvent mouseEvent) {
        String newDeckName = newDeckNameField.getText();
        if (newDeckName.equals("")) return;

        if (MainView.getInstance().alertMaker(MainView.getInstance().createNewDeck(newDeckName))) {
            initialize();
            newDeckNameField.clear();
        }
    }

    public void deleteDeck(MouseEvent mouseEvent) {
        if (selectedDeck == null) return;
        if (MainView.getInstance().alertMaker(MainView.getInstance().deleteDeck(selectedDeck))) {
            newDeckNameField.clear();
            initialize();
        }
    }

    public void editDeck(MouseEvent mouseEvent) throws Exception {
        if (selectedDeck == null) return;
        new DeckEditPage().start(stage);
    }

    public void setAsActiveDeck(MouseEvent mouseEvent) {
        if (selectedDeck == null) return;
        if (MainView.getInstance().alertMaker(MainView.getInstance().setActiveDeck(selectedDeck))) {
            newDeckNameField.clear();
            initialize();
        }
    }
}

class ViewDeck {
    public final String name;
    int mainDeckCardsNum;
    int sideDeckCardsNum;
    boolean isValid;
    boolean isActive;
    Popup popup;

    ViewDeck(String name, int mainDeckCardsNum, int sideDeckCardsNum, boolean isValid, boolean isActive) {
        this.name = name;
        this.mainDeckCardsNum = mainDeckCardsNum;
        this.sideDeckCardsNum = sideDeckCardsNum;
        this.isValid = isValid;
        this.isActive = isActive;
        setDeckSummaryPopup();
    }

    public void setDeckSummaryPopup() {
        popup = new Popup();
        JSONObject answer = MainView.getInstance().showDeckSummary(name);

        if (answer.getString("Type").equals("Successful")) {
            Label label = new Label(answer.getString("Value"));
            label.setWrapText(true);
            label.setTextAlignment(TextAlignment.JUSTIFY);
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(15));
            label.setMaxWidth(380);
            label.setMaxHeight(450);

            label.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);" +
                    "-fx-border-radius: 30px;" +
                    "-fx-background-radius: 30px;" +
                    "-fx-font-size: 15px");

            popup.getContent().add(label);
        }
    }
}
