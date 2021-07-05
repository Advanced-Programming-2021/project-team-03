package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;
import view.viewcontroller.MainView;

public class MainPage extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Main.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
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
        new ImportExportMenuPage().start(stage);
    }
}
