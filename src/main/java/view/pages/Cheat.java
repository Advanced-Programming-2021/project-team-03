package view.pages;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Cheat extends Application {
    public TextField input;
    public Text result;

    @Override
    public void start(Stage stage) throws Exception {
        Stage cheatWindow = new Stage();
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Cheat.fxml"));
        Scene scene = new Scene(startingPane);
        cheatWindow.setScene(scene);
        cheatWindow.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        cheatWindow.setTitle("CHEAT");
        cheatWindow.setResizable(false);
        cheatWindow.initModality(Modality.APPLICATION_MODAL);
        cheatWindow.showAndWait();
    }

    public void submit(ActionEvent event) {

    }
}
