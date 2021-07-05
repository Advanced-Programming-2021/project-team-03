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

public class LoginPage extends Application {
    private static Stage stage;

    @FXML
    public PasswordField passwordField;
    public TextField usernameField;
    public Text messageText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Login.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new StartPage().start(stage);
    }

    public void login(MouseEvent mouseEvent) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.equals("") || password.equals("")) {
            messageText.setText("You must fill in all the fields!");
        } else {
            JSONObject controlAnswer = MainView.getInstance().loginUser(username, password);
            String messageType = controlAnswer.getString("Type");
            if (messageType.equals("Successful")) {
                new MainPage().start(stage);
            } else {
                String message = controlAnswer.getString("Value");
                messageText.setText(message);
                clearFields();
            }
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    public void clearMessage(MouseEvent mouseEvent) {
        messageText.setText("");
    }
}
