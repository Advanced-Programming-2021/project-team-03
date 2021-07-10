package view.pages;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GamePausePage extends Application implements Initializable {
    private static Stage stage;
    public Button back;
    public Button surrender;
    public Button mute;
    public Button unmute;

    @Override
    public void start(Stage stage) throws Exception {
        Stage pauseWindow = new Stage();
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/GamePauseMenu.fxml"));
        Scene scene = new Scene(startingPane);
        pauseWindow.setScene(scene);
        pauseWindow.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        pauseWindow.setTitle("PAUSE");
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playTransition2ForButton(mute);
        playTransitionForButton(unmute);
        playTransitionForButton(back);
        playTransition2ForButton(surrender);
    }

    private void playTransition2ForButton(Button button) {
        TranslateTransition buttonTransition = new TranslateTransition();
        buttonTransition.setDuration(Duration.millis(2000));
        buttonTransition.setToX(-214);
        buttonTransition.setNode(button);
        buttonTransition.play();
    }

    private void playTransitionForButton(Button button) {
        TranslateTransition buttonTransition = new TranslateTransition();
        buttonTransition.setDuration(Duration.millis(2000));
        buttonTransition.setToX(214);
        buttonTransition.setNode(button);
        buttonTransition.play();
    }
}
