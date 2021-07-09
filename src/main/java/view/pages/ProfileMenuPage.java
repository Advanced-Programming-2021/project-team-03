package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;
import view.viewcontroller.MainView;

public class ProfileMenuPage extends Application {
    private static Stage stage;

    @FXML
    public Text nicknameText;
    public Text usernameText;
    public ImageView profileImage;
    public Button OKButton;
    public TextField nicknameField;
    public Text newNicknameText;
    public Text messageText;
    public PasswordField currentPasswordField;
    public Text currentPasswordText;
    public PasswordField newPasswordField;
    public Text newPasswordText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ProfileMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
    }

    @FXML
    public void initialize() {
        loadUserInfo();
    }

    private void loadUserInfo() {
        usernameText.setText(MainView.getInstance().getUsername());
        nicknameText.setText(MainView.getInstance().getNickname());
        int profileImageNumber = MainView.getInstance().getUserProfileImageNumber();
        profileImage.setImage(new Image(String.valueOf(getClass().getResource("/assets/profilepictures/"
                + String.valueOf(profileImageNumber) + ".png"))));
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void addChangeNicknameFields(MouseEvent mouseEvent) {
        messageText.setText("");
        setChangePasswordFiledVisible(false);
        setChangeNicknameFiledVisible(true);
    }

    public void addChangePasswordFields(MouseEvent mouseEvent) {
        messageText.setText("");
        setChangeNicknameFiledVisible(false);
        setChangePasswordFiledVisible(true);
    }

    public void okButton(MouseEvent mouseEvent) {
        if (nicknameField.isVisible()) {
            changeNickname();
        } else {
            changePassword();
        }
    }

    private void changeNickname() {
        String newNickname = nicknameField.getText();
        nicknameField.clear();
        if (newNickname.equals("")) {
            messageText.setFill(Color.DARKRED);
            messageText.setText("Error: You must fill in all the fields!");
        } else {
            JSONObject answer = MainView.getInstance().changeNickname(newNickname);
            String type = answer.getString("Type");
            String message = answer.getString("Value");
            if (type.equals("Successful")) {
                messageText.setFill(Color.DARKGREEN);
                nicknameText.setText(newNickname);
            } else {
                messageText.setFill(Color.DARKRED);
            }
            messageText.setText(type + ": " + message);
        }
    }

    private void changePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        currentPasswordField.clear();
        newPasswordField.clear();
        if (currentPassword.equals("") || newPassword.equals("")) {
            messageText.setFill(Color.DARKRED);
            messageText.setText("Error: You must fill in all the fields!");
        } else {
            JSONObject answer = MainView.getInstance().changePassword(currentPassword, newPassword);
            String type = answer.getString("Type");
            String message = answer.getString("Value");
            if (type.equals("Successful")) {
                messageText.setFill(Color.DARKGREEN);
            } else {
                messageText.setFill(Color.DARKRED);
            }
            messageText.setText(type + ": " + message);
        }
    }

    private void setChangeNicknameFiledVisible(boolean b) {
        OKButton.setVisible(b);
        newNicknameText.setVisible(b);
        nicknameField.setVisible(b);
    }

    private void setChangePasswordFiledVisible(boolean b) {
        OKButton.setVisible(b);
        currentPasswordField.setVisible(b);
        currentPasswordText.setVisible(b);
        newPasswordField.setVisible(b);
        newPasswordText.setVisible(b);
    }
}
