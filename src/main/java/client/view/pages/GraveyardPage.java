package client.view.pages;

import client.view.controller.MainView;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GraveyardPage extends Application implements Initializable {
    private static Stage stage;

    @FXML
    public ScrollPane scrollPane;
    public Text nameLabel;
    public ScrollPane scrollBar;

    private static ArrayList<String> cardNames;


    @Override
    public void start(Stage stage) throws Exception {
        Stage pauseWindow = new Stage();
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Graveyard.fxml"));
        Scene scene = new Scene(startingPane);
        pauseWindow.setScene(scene);
        pauseWindow.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        pauseWindow.setTitle("GRAVEYARD");
        pauseWindow.setResizable(false);
        pauseWindow.initModality(Modality.APPLICATION_MODAL);
        GraveyardPage.stage = pauseWindow;
        pauseWindow.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCards();
        animations();
    }

    private void animations() {
        nameLabel.setOpacity(0.0);
        FadeTransition label = new FadeTransition(Duration.seconds(1), nameLabel);
        label.setFromValue(0.0);
        label.setToValue(1.0);
        label.play();
    }

    private void loadCards() {
        HBox hBox = new HBox();
        try {
            for (String cardName : cardNames) {
                Image image = MainView.getInstance().getCardImage(cardName);
                ImageView imageView = new ImageView(image);
                Pane pane = new Pane(imageView);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(120);
                hBox.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        scrollPane.setPadding(new Insets(15));
        scrollPane.setCenterShape(true);
        scrollPane.setContent(hBox);
    }

    public void setCardNames(ArrayList<String> cardNames) {
        this.cardNames = cardNames;
    }
}