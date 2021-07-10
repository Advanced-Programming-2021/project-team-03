package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import view.viewcontroller.MainView;

import java.io.*;
import java.util.ArrayList;

public class ImportExportPage extends Application {
    private final ArrayList<ShopCard> allCards = new ArrayList<>();
    private static Stage stage;
    public ScrollPane scrollPane;
    private ShopCard selectedCard;
    private Pane selectedPane;

    @FXML
    public Button importButton;

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
                        pane.setStyle("-fx-border-width: 6;"
                            + "-fx-border-color: lightgreen;");
                });

                imageView.setOnMouseExited(mouseEvent -> {
                    if (selectedCard != card)
                        pane.setStyle("-fx-border-width: 0;");
                });

                imageView.setOnMouseClicked(mouseEvent -> {
                    if (selectedCard == card) {
                        selectedCard = null;
                        selectedPane = null;
                        pane.setStyle("-fx-border-width: 0");
                    } else {
                        selectedCard = card;
                        if (selectedPane != null) selectedPane.setStyle("-fx-border-width: 0");

                        pane.setStyle("-fx-border-width: 10;"
                                + "-fx-border-color: lightgreen;");
                        selectedPane = pane;
                    }
                });

                imageView.setOnDragDetected(event -> {
                    Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);

                    try {
                        JSONObject answer = MainView.getInstance().getCardJson(card.getName());

                        if (answer.getString("Type").equals("Successful")) {
                            File file = new File(card.getName() + ".json");
                            FileWriter writer = new FileWriter(file);
                            writer.write(answer.getString("Value"));
                            writer.close();
                            ClipboardContent content = new ClipboardContent();
                            ArrayList<File> files = new ArrayList<>();
                            files.add(file);
                            content.putFiles(files);
                            db.setContent(content);
                            file.deleteOnExit();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    event.consume();
                });

                imageView.setPreserveRatio(true);
                imageView.setFitWidth(350);
                vbox.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        vbox.setCenterShape(true);
        scrollPane.setCenterShape(true);
        scrollPane.setContent(vbox);


        importButton.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != importButton) {
                if (isJsonFile(event.getDragboard()))
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
                    isJsonFile(event.getDragboard())) {
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
            System.out.println("onDragDropped");
            /* if there is a card file on dragBoard, read it and use it */
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (isJsonFile(dragboard)) {
                importCardFromFile(dragboard.getFiles().get(0));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
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
            alert.setContentText(answer.getString("Value")
             + "\n" + MainView.getInstance().showCard(newCardName).getString("Value"));
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Yu-Gi-Oh card (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(cardName);
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        Alert alert;
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(answer.getString("Value"));
            fileWriter.close();

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Card save successfully at: " + file.getAbsolutePath());
            selectedCard = null;
            selectedPane = null;
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Couldn't export card: " + e.getMessage());
        }
        alert.show();
    }

    private boolean isJsonFile(Dragboard dragboard) {
        if (!dragboard.hasFiles()) return false;

        String fileName = dragboard.getFiles().get(0).getName();
        int i = fileName.lastIndexOf('.');
        return i > 0 && fileName.substring(i + 1).equals("json");
    }
}
