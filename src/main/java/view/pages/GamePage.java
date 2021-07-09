package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GamePage extends Application {
    private static Stage stage;
    public Text opponentLifePoint;
    public ProgressBar playerLPBar;
    public Text playerNickname;
    public Text playerLifePoint;
    public Text opponentNickname;
    public ProgressBar opponentLPBar;
    public ImageView playerProfile;
    public ImageView opponentProfile;
    public ImageView selectedCard;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Game.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    public void pause(MouseEvent mouseEvent) {
    }

    public void nextPhase(MouseEvent mouseEvent) {
    }
}
