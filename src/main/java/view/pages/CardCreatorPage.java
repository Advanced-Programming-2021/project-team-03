package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewcontroller.MainView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CardCreatorPage extends Application {
    private static Stage stage;
    public Label priceLabel;
    public TextArea descriptionText;
    public ChoiceBox monsterTypeChoiceBox;
    public ChoiceBox attributeChoiceBox;
    public Label defenceLabel;
    public Label attackLabel;
    public Slider defenceSlider;
    public Slider levelSlider;
    public Slider attackSlider;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/CardCreatorMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @FXML
    public void initialize() {
        /*monsterTypeChoiceBox.getItems().addAll("", "", "", "", "");
        attributeChoiceBox.getItems().addAll("DARK", "LIGHT","    EARTH,\n" +
                "    FIRE,\n" +
                "    WATER,\n" +
                "    WIND,"));*/
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }


    public void createCard(MouseEvent mouseEvent) {

    }
}