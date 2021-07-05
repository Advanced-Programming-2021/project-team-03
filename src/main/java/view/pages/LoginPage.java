package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import view.viewcontroller.MainView;

public class LoginPage extends Application {
    private static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Login.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new StartPage().start(stage);
    }

    public void login(MouseEvent mouseEvent) {

    }
}
