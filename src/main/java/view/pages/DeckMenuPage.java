package view.pages;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewcontroller.MainView;

import java.util.ArrayList;

public class DeckMenuPage extends Application {
    private static Stage stage;
    private ArrayList<ViewDeck> allDecks;
    public ScrollPane scrollPane;
    public TextField newDeckNameField;
    public static String selectedDeck;


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

        allDecks.forEach(viewDeck -> vbox.getChildren().add(deckVBoxGenerator(viewDeck)));

        scrollPane.setContent(vbox);
    }

    private Node deckVBoxGenerator(ViewDeck deck) {
        VBox deckVBox = new VBox();

        TextField nameTextField = new TextField();
        nameTextField.setEditable(false);
        nameTextField.setText(deck.name);
        nameTextField.setStyle("-fx-font-size : 20px");
        deckVBox.getChildren().add(nameTextField);

        TextField mainDeckTextField = new TextField();
        mainDeckTextField.setEditable(false);
        mainDeckTextField.setText("Main deck cards: " + deck.mainDeckCardsNum);
        mainDeckTextField.setStyle("-fx-font-size : 16px");
        deckVBox.getChildren().add(mainDeckTextField);

        TextField sideDeckTextfield = new TextField();
        sideDeckTextfield.setEditable(false);
        sideDeckTextfield.setText("Side deck cards: " + deck.sideDeckCardsNum);
        sideDeckTextfield.setStyle("-fx-font-size : 16px");
        deckVBox.getChildren().add(sideDeckTextfield);

        TextField validTextfield = new TextField();
        validTextfield.setEditable(false);
        validTextfield.setText(deck.isValid ? "Valid" : "Invalid");
        validTextfield.setStyle("-fx-font-size : 16px;"
                + "-fx-text-fill : " + ((deck.isValid) ? "green" : "red"));
        deckVBox.getChildren().add(validTextfield);

        if (deck.isActive) {
            TextField activeTextfield = new TextField();
            activeTextfield.setEditable(false);
            activeTextfield.setText("Active deck");
            activeTextfield.setStyle("-fx-font-size : 16px;" + "-fx-text-fill : green");
            deckVBox.getChildren().add(activeTextfield);
            deckVBox.setStyle("-fx-border-width: 5;" + "-fx-border-color: green;");
        } else {
            deckVBox.setStyle("-fx-border-width: 3;" + "-fx-border-color: gray;");
        }

        Popup popup = new Popup();
        popup.setAutoHide(true);

        deckVBox.setOnMouseEntered(event -> {
            JSONObject answer = MainView.getInstance().showDeck(deck.name);
            if (answer.getString("Type").equals("Successful")) {
                Label label = new Label(deck.name + ":\n" + answer.getString("Value"));
                label.setStyle("-fx-background-color: white;");
                popup.getContent().add(label);

                if (!popup.isShowing())
                    popup.show(stage);
            }
        });

        deckVBox.setOnMouseExited(event -> {
            if (popup.isShowing()) popup.hide();
        });

        deckVBox.setOnMouseClicked(event -> {
            try {
                selectedDeck = deck.name;
                new DeckPage().start(stage);
            } catch (Exception e) {
                e.printStackTrace();
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
            JSONObject deck = new JSONObject(deckObject.toString());
            allDecks.add(new ViewDeck(deck.getString("Name"),
                    deck.getInt("MainDeckNum"),
                    deck.getInt("SideDeckNum"),
                    deck.getBoolean("Valid"),
                    deck.getBoolean("Active")));
        }
        return true;
    }


    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void createNewDeck(MouseEvent mouseEvent) {
        String newDeckName = newDeckNameField.getText();
        if (newDeckName.equals("")) return;

        if (MainView.getInstance().alertMaker(MainView.getInstance().createNewDeck(newDeckName)))
            initialize();
    }
}

class ViewDeck {
    public final String name;
    int mainDeckCardsNum;
    int sideDeckCardsNum;
    boolean isValid;
    boolean isActive = false;

    ViewDeck(String name, int mainDeckCardsNum, int sideDeckCardsNum, boolean isValid, boolean isActive) {
        this.name = name;
        this.mainDeckCardsNum = mainDeckCardsNum;
        this.sideDeckCardsNum = sideDeckCardsNum;
        this.isValid = isValid;
        this.isActive = isActive;
    }
}
