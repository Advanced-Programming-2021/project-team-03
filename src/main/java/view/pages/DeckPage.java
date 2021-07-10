package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeckPage extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/DeckMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        Stage stage = primaryStage;
    }

    @FXML
    public void initialize() {

    }

}
