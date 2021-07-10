package view.pages;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.viewcontroller.MainView;

public class GameResultPage extends Application {
    private static Stage stage;

    private String messageString;
    private GamePage gamePage;

    @FXML
    public Text message;

    public GameResultPage(GamePage gamePage,String message){
        this.messageString = message;
        this.gamePage = gamePage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Stage pauseWindow = new Stage();
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/GameResult.fxml"));
        Scene scene = new Scene(startingPane);
        pauseWindow.setScene(scene);
        pauseWindow.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        pauseWindow.setTitle(stage.getTitle());
        pauseWindow.setWidth(600);
        pauseWindow.setHeight(400);
        pauseWindow.setResizable(false);
        pauseWindow.initModality(Modality.APPLICATION_MODAL);
        GameResultPage.stage = pauseWindow;
        pauseWindow.showAndWait();
    }

    @FXML
    public void initialize(){
        this.message.setText(this.messageString);
    }

    public void okButton(MouseEvent mouseEvent) {
        stage.close();
        gamePage.endGame();
    }
}
