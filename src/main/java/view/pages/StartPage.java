package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.Objects;

public class StartPage extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Start.fxml"));
        Scene scene = new Scene(startingPane);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        primaryStage.setTitle("YU-GI-OH");
        backgroundMusic();
        stage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static MediaPlayer mediaPlayer;

    private void backgroundMusic() {
        mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/ThemeSongs.mp3")).toString()));
        mediaPlayer.setVolume(0.2);
        mediaPlayer.setCycleCount(-1);
        //mediaPlayer.play();
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
