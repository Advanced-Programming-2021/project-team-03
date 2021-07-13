package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StartPage extends Application implements Initializable {
    private static Stage stage;
    private static boolean startedBefore = false;
    private static boolean mute = false;
    public Button muteButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Start.fxml"));
        Scene scene = new Scene(startingPane);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        primaryStage.setTitle("YU-GI-OH");
        if (!startedBefore) {
            backgroundMusic();
            startedBefore = true;
        }
        stage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (mute) {
            muteButton.setText("UNMUTE");
        } else {
            muteButton.setText("MUTE");
        }
    }

    public static MediaPlayer mediaPlayer;

    private void backgroundMusic() {
        mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/ThemeSongs.mp3")).toString()));
        mediaPlayer.setVolume(0.1);
        mediaPlayer.setCycleCount(-1);
        mediaPlayer.play();
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

    public void mute(MouseEvent mouseEvent) {
        if (mute) {
            mute = false;
            muteButton.setText("MUTE");
            mediaPlayer.play();
        } else {
            mute = true;
            muteButton.setText("UNMUTE");
            mediaPlayer.pause();
        }
    }
}
