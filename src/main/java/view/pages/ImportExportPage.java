package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
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

public class ImportExportPage extends Application {
    private static Stage stage;
    private final ArrayList<ShopCard> allCards = new ArrayList<>();
    public ScrollPane scrollPane;
    @FXML
    public Button importButton;
    private ShopCard selectedCard;
    private ImageView selectedImage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ImportExportMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @FXML
    public void initialize() {
        loadAllCards();
        VBox vbox = new VBox();

        try {
            for (ShopCard card : allCards) {
                Image image = MainView.getInstance().getCardImage(card.getName());
                ImageView imageView = new ImageView(image);
                Pane pane = new Pane(imageView);

                imageView.setOnMouseEntered(mouseEvent -> {
                    if (selectedCard != card)
                        imageView.setEffect(new DropShadow(20, Color.BLACK));
                });

                imageView.setOnMouseExited(mouseEvent -> {
                    if (selectedCard != card) imageView.setEffect(new DropShadow(0, Color.BLACK));
                });

                imageView.setOnMouseClicked(mouseEvent -> {
                    if (selectedCard == card) {
                        imageView.setEffect(new DropShadow(0, Color.BLACK));
                        selectedCard = null;
                        selectedImage = null;
                    } else {
                        if (selectedImage != null) selectedImage.setEffect(new DropShadow(0, Color.BLACK));

                        DropShadow dropShadow = new DropShadow(25, Color.GREEN);
                        dropShadow.setSpread(0.6);
                        imageView.setEffect(dropShadow);
                        selectedImage = imageView;
                        selectedCard = card;
                    }
                });

                imageView.setOnDragDetected(event -> {
                    JSONObject answer = MainView.getInstance().getCardJson(card.getName());
                    if (answer.getString("Type").equals("Successful"))
                        setJsonOnDrag(answer.getString("Value"), card.getName(), imageView);
                    event.consume();
                });

                imageView.setPreserveRatio(true);
                imageView.setFitWidth(340);
                vbox.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        scrollPane.setPadding(new Insets(15));
        scrollPane.setCenterShape(true);
        scrollPane.setContent(vbox);


        importButton.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != importButton) {
                if (hasJsonFile(event.getDragboard()))
                    importButton.setStyle("-fx-border-radius: 40;"
                            + "-fx-border-width: 6;"
                            + "-fx-border-color: lightgreen;");
                else
                    importButton.setStyle("-fx-border-radius: 40;"
                            + "-fx-border-width: 6;"
                            + "-fx-border-color: red;");
            }
            event.consume();
        });

        importButton.setOnDragOver(event -> {
            if (event.getGestureSource() != importButton &&
                    hasJsonFile(event.getDragboard())) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        importButton.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */
            importButton.setStyle("-fx-border-width: 0;");
            event.consume();
        });

        importButton.setOnDragDropped(event -> {
            /* data dropped */
            /* if there is a card file on dragBoard, read it and use it */
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (hasJsonFile(dragboard)) {
                importCardFromFile(dragboard.getFiles().get(0));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void setJsonOnDrag(String value, String cardName, ImageView imageView) {
        Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
        File file = new File(cardName + ".json");

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(value);
            writer.close();

            ClipboardContent content = new ClipboardContent();
            ArrayList<File> files = new ArrayList<>();
            files.add(file);
            content.putFiles(files);
            db.setContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.deleteOnExit();
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
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


    private void importCardFromFile(File file) {
        if (file == null) return;
        Alert alert;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String cardFile = reader.readLine();
            JSONObject answer = MainView.getInstance().importCardJson(cardFile);

            String newCardName = new JSONObject(cardFile).getString("Name");

            if (answer.get("Type").equals("Successful")) {
                alert = new Alert(Alert.AlertType.INFORMATION);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
            }
            alert.setTitle(answer.getString("Value"));
            alert.setContentText(MainView.getInstance().showCard(newCardName).getString("Value"));
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Couldn't read card file: " + e.getMessage());
        }
        alert.show();
    }

    public void importCard(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose card file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Yu-Gi-Oh card (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        importCardFromFile(file);
    }

    public void exportCard(MouseEvent mouseEvent) {
        if (selectedCard == null) return;
        String cardName = selectedCard.getName();

        MainView view = MainView.getInstance();
        if (cardName.equals("")) return;
        JSONObject answer = view.getCardJson(cardName);

        if (!answer.get("Type").equals("Successful")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a directory to save the card file");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Yu-Gi-Oh card (*.json)", "*.json"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Yu-Gi-Oh multiple card (*.csv)", "*.csv"));
        fileChooser.setInitialFileName(cardName);
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        JSONObject value = new JSONObject(answer.getString("Value"));
        StringBuilder stringBuilder = new StringBuilder();

        if (isJsonFileName(file.getName())) stringBuilder.append(value);
        else { // requested file is csv
            if (value.has("Atk")) { // card is monster
                stringBuilder.append("Name,Level,Attribute,MonsterType,CardType,Atk,Def,Description,Price,cardID\n");
                stringBuilder.append("\"").append(value.getString("Name")).append("\"").append(",")
                        .append(value.getInt("Level")).append(",")
                        .append(value.getString("Attribute")).append(",")
                        .append(value.getString("MonsterType")).append(",")
                        .append(value.getString("CardType")).append(',')
                        .append(value.getInt("Atk")).append(",")
                        .append(value.getInt("Def")).append(",")
                        .append("\"").append(value.getString("Description")).append("\"").append(",")
                        .append(value.getInt("Price")).append(",")
                        .append(value.getInt("CardID")).append("\n");
            } else { // card is spell
                stringBuilder.append("Name,Type,Icon,Description,Status,Price,cardID\n");
                stringBuilder.append("\"").append(value.getString("Name")).append("\"").append(",")
                        .append(value.getString("Type")).append(",")
                        .append(value.getString("Icon")).append(",")
                        .append("\"").append(value.getString("Description")).append("\"").append(",")
                        .append(value.getString("Status")).append(",")
                        .append(value.getInt("Price")).append(",")
                        .append(value.getInt("CardID")).append("\n");
            }
        }

        Alert alert;
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Card save successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Couldn't export card: " + e.getMessage());
        }
        alert.show();
    }

    private boolean hasJsonFile(Dragboard dragboard) {
        if (!dragboard.hasFiles()) return false;
        return isJsonFileName(dragboard.getFiles().get(0).getName());
    }

    private boolean isJsonFileName(String fileName) {
        int i = fileName.lastIndexOf('.');
        return i > 0 && fileName.substring(i + 1).equals("json");
    }
}
