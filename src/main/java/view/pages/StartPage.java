package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class StartPage extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Start.fxml"));
        Scene scene = new Scene(startingPane);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageimages/logo.png"))));
        primaryStage.setTitle("YU-GI-OH");
        stage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void register(MouseEvent mouseEvent) throws Exception {
        new RegisterPage().start(stage);
    }

    public void login(MouseEvent mouseEvent) throws Exception {
        new LoginPage().start(stage);
    }

    public void Exit(MouseEvent mouseEvent) {
        stage.close();
    }
}
