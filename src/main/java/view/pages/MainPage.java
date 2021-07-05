package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainPage extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Main.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        primaryStage.show();
    }

    public void logout(MouseEvent mouseEvent) {
    }

    public void profile(MouseEvent mouseEvent) {
    }

    public void scoreBoard(MouseEvent mouseEvent) {
    }

    public void deck(MouseEvent mouseEvent) {
    }

    public void duel(MouseEvent mouseEvent) {
    }

    public void shop(MouseEvent mouseEvent) {
    }

    public void back(MouseEvent mouseEvent) {
    }
}
