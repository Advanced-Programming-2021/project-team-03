package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;
import view.viewcontroller.MainView;

public class RegisterPage extends Application {
    private static Stage stage;

    @FXML
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

    public void register(MouseEvent mouseEvent) {
        String username = usernameField.getText();
        String nickname = nicknameField.getText();
        String password = passwordField.getText();
        if (username.equals("") || nickname.equals("") || password.equals("")) {
            messageText.setText("Error: You must fill in all the fields!");
        } else {
            JSONObject controlAnswer = MainView.getInstance().register(username, password, nickname);
            String messageType = controlAnswer.getString("Type");
            String message = controlAnswer.getString("Value");
            messageText.setText(messageType + ": " + message);
            clearFields();
        }
    }

    private void clearFields() {
        usernameField.clear();
        nicknameField.clear();
        passwordField.clear();
    }

    public void clearMessage(MouseEvent mouseEvent) {
        messageText.setText("");
    }
}
