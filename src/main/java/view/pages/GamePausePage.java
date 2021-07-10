package view.pages;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GamePausePage extends Application {
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Stage pauseWindow = new Stage();
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/GamePauseMenu.fxml"));
        Scene scene = new Scene(startingPane);
        pauseWindow.setScene(scene);
        pauseWindow.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        pauseWindow.setTitle(stage.getTitle());
        pauseWindow.setWidth(600);
        pauseWindow.setHeight(400);
        pauseWindow.setResizable(false);
        pauseWindow.initModality(Modality.APPLICATION_MODAL);
        GamePausePage.stage = pauseWindow;
        pauseWindow.showAndWait();
    }

    public void exit(ActionEvent actionEvent) {
        stage.close();
    }

    public void surrender(ActionEvent actionEvent) {

    }

    public void mute(ActionEvent actionEvent) {
        StartPage.mediaPlayer.pause();
    }

    public void unmute(ActionEvent actionEvent) {
        StartPage.mediaPlayer.play();
    }
}
