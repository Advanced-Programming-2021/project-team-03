package client.view.pages;

import client.view.controller.MainView;
import client.view.model.ClientUser;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.List;

public class DuelMenuPage extends Application {
    private static Stage stage;

    @FXML
    public TextField numberOfRoundsField;
    public TextField opponentUsernameField;
    public Text message;
    public Button playButton;
    public Text numberOfRoundsText;
    public Text opponentUsernameText;
    public ImageView fireImageView;
    public ScrollPane usersPane;
    public Label onlineUsersLabel;

    private String nickname;
    private HBox hBox;
    private final MainView view = MainView.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/DuelMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
    }

    @FXML
    public void initialize() {
        initializeUsers();
        setSinglePlayMatchFiledVisible(false);
        setMultiPlayMatchFiledVisible(false);
        fireAnimation();
    }

    private void initializeUsers() {
        nickname = view.getNickname();
        usersPane.setPadding(new Insets(10));
        usersPane.setCenterShape(true);

        hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(8));
        usersPane.setContent(hBox);
        usersPane.setPadding(new Insets(8));

        String cssLayout2 = """
                -fx-border-color: rgb(231, 201, 133);
                -fx-border-insets: 5;
                -fx-border-width: 6;
                -fx-border-radius: 10px;
                -fx-background-color: rgba(255, 255, 255, 0)
                """;
        usersPane.setStyle(cssLayout2);
        loadUsers();
    }

    private void loadUsers() {
        List<String> users = view.getOnlineUsers();
        hBox.getChildren().clear();
        users.stream().map(view::getUserInfo)
                .map(this::getProfileVBox).forEach(vBox1 -> hBox.getChildren().add(vBox1));
        onlineUsersLabel.setText("Lobby | " + hBox.getChildren().size() + " online users");
    }

    private VBox getProfileVBox(ClientUser user) {
        VBox profileVBox = new VBox();
        profileVBox.setAlignment(Pos.CENTER);
        if (user.nickname.equals(nickname)) {
            profileVBox.setStyle("""
                -fx-border-color: orange;
                -fx-border-insets: 1;
                -fx-border-width: 4;
                -fx-border-radius: 5px;
                """);
        } else {
            profileVBox.setStyle("""
                -fx-border-color: green;
                -fx-border-insets: 1;
                -fx-border-width: 2;
                -fx-border-radius: 5px;
                """);
        }
        profileVBox.setMaxWidth(120);
        ImageView profileImage = new ImageView(view.getProfileImageByID(user.profileImageID));
        profileImage.setPreserveRatio(true);
        profileImage.setFitWidth(100);
        profileImage.setOnMouseClicked(event -> showProfilePopup(user));
        profileVBox.getChildren().add(profileImage);

        Label nicknameLabel = new Label(user.nickname);
        nicknameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        profileVBox.getChildren().add(nicknameLabel);
        return profileVBox;
    }

    private void showProfilePopup(ClientUser clientUser) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Details");
        alert.setHeaderText(clientUser.nickname);
        alert.setContentText(clientUser.toString());
        alert.show();
    }

    private void fireAnimation() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(5), fireImageView);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    public void singlePlayMatch(MouseEvent mouseEvent) {
        setMultiPlayMatchFiledVisible(false);
        setSinglePlayMatchFiledVisible(true);
        message.setText("");
        numberOfRoundsField.clear();
    }

    public void multiPlayMatch(MouseEvent mouseEvent) {
        setSinglePlayMatchFiledVisible(false);
        setMultiPlayMatchFiledVisible(true);
        message.setText("");
        numberOfRoundsField.clear();
        opponentUsernameField.clear();
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void play(MouseEvent mouseEvent) throws Exception {
        message.setText("");
        if (opponentUsernameText.isVisible()) {
            startNewMultiPlayMatch();
        } else {
            startNewSinglePlayMatch();
        }
    }

    private void startNewSinglePlayMatch() throws Exception {
        String numberOfRounds = numberOfRoundsField.getText();
        if (numberOfRounds.equals("")) {
            showErrorMessage("You must fill in all the fields!");
            return;
        }
        if (!numberOfRounds.matches("^\\d+$")) {
            showErrorMessage("You must select a number for the number of rounds.");
            return;
        }
        JSONObject answer = MainView.getInstance().startNewSingleMatch(Integer.parseInt(numberOfRounds));
        String type = answer.getString("Type");
        String value = answer.getString("Value");
        if (type.equals("Successful")) {
            new GamePage().start(stage);
        } else {
            showErrorMessage(value);
        }
    }

    private void startNewMultiPlayMatch() throws Exception {
        String numberOfRounds = numberOfRoundsField.getText();
        String opponentUsername = opponentUsernameField.getText();
        if (numberOfRounds.equals("") || opponentUsername.equals("")) {
            showErrorMessage("You must fill in all the fields!");
            return;
        }
        if (!numberOfRounds.matches("^\\d+$")) {
            showErrorMessage("You must select a number for the number of rounds.");
            return;
        }
        JSONObject answer = MainView.getInstance().startNewMultiMatch(Integer.parseInt(numberOfRounds), opponentUsername);
        String type = answer.getString("Type");
        String value = answer.getString("Value");
        if (type.equals("Successful")) {
            new GamePage().start(stage);
        } else {
            showErrorMessage(value);
        }
    }

    private void showErrorMessage(String text) {
        message.setText("Error: " + text);
    }

    private void setSinglePlayMatchFiledVisible(boolean b) {
        numberOfRoundsText.setVisible(b);
        numberOfRoundsField.setVisible(b);
        playButton.setVisible(b);
        message.setVisible(b);
    }

    private void setMultiPlayMatchFiledVisible(boolean b) {
        numberOfRoundsText.setVisible(b);
        numberOfRoundsField.setVisible(b);
        opponentUsernameText.setVisible(b);
        opponentUsernameField.setVisible(b);
        playButton.setVisible(b);
        message.setVisible(b);
    }
}
