package client.view.pages;

import client.view.controller.MainView;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPage extends Application implements Initializable {
    private static Stage stage;
    public Button IO;
    public Button shop;
    public Button deck;
    public Button scoreboard;
    public Button profile;
    public Button logout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Main.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startingAnimations();
    }

    private void startingAnimations() {
        play(IO, true);
        play(shop, true);
        play(deck, true);
        play(scoreboard, false);
        play(profile, false);
        play(logout, false);
    }

    private void play(Button button, boolean toRight) {
        button.setLayoutX(418);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), button);
        if (toRight)
            transition.setToX(350);
        else
            transition.setToX(-350);
        transition.play();
    }

    public void logout(MouseEvent mouseEvent) throws Exception {
        JSONObject controlAnswer = MainView.getInstance().logoutUser();
        String type = controlAnswer.getString("Type");
        if (type.equals("Error")) {
            String message = controlAnswer.getString("Value");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Logout Error");
            alert.setContentText(message);
            alert.setTitle("Logout Error");
            alert.show();
        } else {
            new StartPage().start(stage);
        }
    }

    public void profile(MouseEvent mouseEvent) throws Exception {
        new ProfileMenuPage().start(stage);
    }

    public void scoreBoard(MouseEvent mouseEvent) throws Exception {
        new ScoreBoardPage().start(stage);
    }

    public void deck(MouseEvent mouseEvent) throws Exception {
        new DeckMenuPage().start(stage);
    }

    public void duel(MouseEvent mouseEvent) throws Exception {
        new DuelMenuPage().start(stage);
    }

    public void shop(MouseEvent mouseEvent) throws Exception {
        new ShopMenuPage().start(stage);
    }

    public void importExport(MouseEvent mouseEvent) throws Exception {
        new ImportExportPage().start(stage);
    }

    public void cardCreator(MouseEvent mouseEvent) throws Exception {
        new CardCreatorPage().start(stage);
    }

    public void chatRoom(ActionEvent actionEvent) throws Exception {
        new ChatRoomPage().start(stage);
    }
}
