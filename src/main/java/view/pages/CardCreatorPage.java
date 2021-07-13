package view.pages;

import com.google.gson.Gson;
import control.databaseController.MonsterCSV;
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
import org.json.JSONObject;
import view.viewcontroller.MainView;

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
    public Label levelLabel;
    public TextField cardNameField;

    private int level = 1;
    private int attack = 0;
    private int defence = 0;
    private int price = 0;

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

        levelSlider.valueProperty()
                .addListener((source, oldValue, newValue) -> {
                    level = (int) levelSlider.getValue();
                    updateLevel();
                    updatePrice();
                });

        attackSlider.valueProperty()
                .addListener((source, oldValue, newValue) -> {
                    attack = ((int) (attackSlider.getValue() / 100)) * 100;
                    updateAttack();
                    updatePrice();
                });

        defenceSlider.valueProperty()
                .addListener((source, oldValue, newValue) -> {
                    defence = ((int) (defenceSlider.getValue() / 100)) * 100;
                    updateDefence();
                    updatePrice();
                });

        monsterTypeChoiceBox.valueProperty()
                .addListener((source, oldValue, newValue) -> updatePrice());

        attributeChoiceBox.valueProperty()
                .addListener((source, oldValue, newValue) -> updatePrice());
    }

    private void updateLevel() {
        levelLabel.setText("Level: " + level);
    }

    private void updatePrice() {
        price = (int) (((attack + defence) / 1.1) * Math.exp(level / 6.0));
        if (monsterTypeChoiceBox.getValue() != null &&
                monsterTypeChoiceBox.getValue().toString()
                        .equals("Beast Warrior")) price += 500;
        if (attributeChoiceBox.getValue() != null &&
                attributeChoiceBox.getValue().toString()
                        .equals("Fire")) price += 500;

        priceLabel.setText("Calculated price: " + price);
    }

    private void updateDefence() {
        defenceLabel.setText("Defence: " + defence);
    }

    private void updateAttack() {
        attackLabel.setText("Attack: " + attack);
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }


    public void createCard(MouseEvent mouseEvent) throws Exception {
        String cardName = cardNameField.getText();
        Object monsterTypeObject = monsterTypeChoiceBox.getValue();
        Object attributeObject = attributeChoiceBox.getValue();

        if (monsterTypeObject == null) {
            fillAlert("Monster Type");
            return;
        }
        if (cardName.equals("")) {
            fillAlert("Card name");
            return;
        }
        if (attributeObject == null) {
            fillAlert("Attribute");
            return;
        }

        MainView view = MainView.getInstance();
        MonsterCSV monsterCSV = new MonsterCSV();
        monsterCSV.setName(cardName);
        monsterCSV.setLevel(level);
        monsterCSV.setAttribute(view.toEnumCase(attributeObject.toString()));
        monsterCSV.setMonsterType(view.toEnumCase(monsterTypeObject.toString()));
        monsterCSV.setCardType("NORMAL");
        monsterCSV.setAtk(attack);
        monsterCSV.setDef(defence);
        monsterCSV.setDescription(descriptionText.getText());
        updatePrice();
        monsterCSV.setPrice(price);
        monsterCSV.setCardID("0");

        Alert alert;
        int balance = view.getBalance();
        int amount = price / 10;

        if (balance < amount) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Couldn't create card");
            alert.setContentText(
                    "Not enough balance to pay for this.\n"+
                    "Price: " + amount + " (10% of price)" +
                    "\nYour balance: " + balance + " (-" + (amount - balance) + ")");
            alert.show();
            return;
        }

        JSONObject balanceAnswer = view.reduceBalance(price / 10);
        if (!balanceAnswer.getString("Type").equals("Successful")) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Couldn't create card");
            alert.setContentText(balanceAnswer.getString("Value"));
            alert.show();
            return;
        }

        if (view.alertMaker(view.importCardJson(new Gson().toJson(monsterCSV))))
            resetFields();

    }

    private void fillAlert(String fieldName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Card not complete");
        alert.setContentText("Please fill " + fieldName);
        alert.show();
    }

    private void resetFields() {
        attack = 0;
        defence = 0;
        level = 1;
        updateDefence();
        updateAttack();
        updateLevel();
        updatePrice();
        cardNameField.clear();
        defenceSlider.adjustValue(0);
        attackSlider.adjustValue(0);
        levelSlider.adjustValue(1);
        descriptionText.clear();
        attributeChoiceBox.setValue(null);
        monsterTypeChoiceBox.setValue(null);
    }
}
