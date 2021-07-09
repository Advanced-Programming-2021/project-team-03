package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DuelMenuPage extends Application {
    private static Stage stage;
    public TextField numberOfRoundsField;
    public TextField opponentUsernameField;
    public Text message;
    public Button playButton;
    public Text numberOfRoundsText;
    public Text opponentUsernameText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/DuelMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        primaryStage.show();
    }

    public void singlePlayMatch(MouseEvent mouseEvent) {
    }

    public void multiPlayMatch(MouseEvent mouseEvent) {
    }

    public void back(MouseEvent mouseEvent) {
    }

    public void play(MouseEvent mouseEvent) {
    }
}
