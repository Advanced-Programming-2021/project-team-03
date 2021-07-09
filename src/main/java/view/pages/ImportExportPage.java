package view.pages;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.viewcontroller.MainView;

import java.io.File;

public class ImportExportPage extends Application {
    private static Stage stage;
    private static boolean initialized;

    public HBox hBox;
    public VBox vBox;

    @FXML
    public TextField cardNameField;
    public Button importButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ImportExportMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);

    }

    public void chooseToImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose card file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Yu-Gi-Oh! card file (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) System.out.println(file.getName());

        //MainView.alertMaker(cardNameField, MainView.getInstance().importExportCard(cardName, "Import card"));
    }

    public void importCard(MouseEvent mouseEvent) {
        if (initialized) chooseToImport();

        importButton.setText("Drag and drop to import \n" +
                "or click here to choose manually");

        importButton.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                System.out.println("onDragEntered");
                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != importButton) {
                    if (isJsonFile(event.getDragboard()))
                        importButton.setStyle("-fx-border-radius: 40;"
                                + "-fx-border-width: 5;"
                                + "-fx-border-color: lightgreen;");
                    else
                        importButton.setStyle("-fx-border-radius: 40;"
                                + "-fx-border-width: 5;"
                                + "-fx-border-color: red;");
                }
                event.consume();
            }
        });

        initialized = true;
    }

    public void exportCard(MouseEvent mouseEvent) {
        String cardName = cardNameField.getText();
        if (cardName.equals("")) return;
        MainView.getInstance().alertMaker(cardNameField, MainView.getInstance().importExportCard(cardName, "Export card"));
    }

    private boolean isJsonFile(Dragboard dragboard) {
        if (!dragboard.hasFiles()) return false;

        String fileName = dragboard.getFiles().get(0).getName();
        int i = fileName.lastIndexOf('.');
        return i > 0 && fileName.substring(i + 1).equals("json");
    }
}
