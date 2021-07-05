package view.pages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterPage extends Application {
    private static Stage stage;
    public TextField usernameField;
    public TextField nicknameField;
    public PasswordField passwordField;
    public Text messageText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Register.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new StartPage().start(stage);
    }

    public void login(MouseEvent mouseEvent) {
    }
}
