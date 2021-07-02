package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartPage extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Start.fxml"));
        stage.setScene(new Scene(startingPane));
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageimages/logo.png"))));
        stage.setTitle("YU-GI-OH");
        stage.show();
    }
}
