package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShopMenuPage extends Application {
    private static Stage stage;
    @FXML
    public Text balance;
    public Text messageText;
    public ImageView I1;
    public ImageView I2;
    public ImageView I6;
    public ImageView I5;
    public ImageView I4;
    public ImageView I3;
    public Text N1;
    public Text N3;
    public Text N4;
    public Text N5;
    public Text N2;
    public Text N6;
    public Text P1;
    public Text P3;
    public Text P2;
    public Text P4;
    public Text P5;
    public Text P6;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ShopMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }
}
