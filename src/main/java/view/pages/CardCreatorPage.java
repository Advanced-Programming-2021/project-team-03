package view.pages;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.enums.CardAttributes;
import model.enums.MonsterModels;


import java.util.Arrays;
import java.util.stream.Collectors;

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

        monsterTypeChoiceBox.setItems(FXCollections.observableList(Arrays.stream(MonsterModels.values())
                .map(cardEnum -> cardEnum.name).collect(Collectors.toList())));
        attributeChoiceBox.setItems(FXCollections.observableList(Arrays.stream(CardAttributes.values())
                .filter(attribute -> (attribute != CardAttributes.SPELL && attribute != CardAttributes.TRAP))
                .map(cardEnum -> cardEnum.name).collect(Collectors.toList())));
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }


    public void createCard(MouseEvent mouseEvent) {

    }
}
