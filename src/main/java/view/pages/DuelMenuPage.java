package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;
import view.viewcontroller.MainView;

public class DuelMenuPage extends Application {
    private static Stage stage;

    @FXML
    public TextField numberOfRoundsField;
    public TextField opponentUsernameField;
    public Text message;
    public Button playButton;
    public Text numberOfRoundsText;
    public Text opponentUsernameText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/DuelMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
    }

    @FXML
    public void initialize() {
        setSinglePlayMatchFiledVisible(false);
        setMultiPlayMatchFiledVisible(false);
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
