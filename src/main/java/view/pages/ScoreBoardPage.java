package view.pages;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.viewcontroller.MainView;
import view.viewmodel.ScoreboardUser;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ScoreBoardPage extends Application implements Initializable {
    private static Stage stage;
    public TableView scoreboard;
    public TableColumn rankColumn;
    public TableColumn nicknameColumn;
    public TableColumn scoreColumn;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ScoreBoard.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        primaryStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<ScoreboardUser, String>("nickname"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<ScoreboardUser, Integer>("score"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<ScoreboardUser, Integer>("rank"));
        scoreboard.setItems(updateScoreBoard());
        scoreboard.setLayoutY(90);
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(1000));
        transition.setToY(-75);
        transition.setNode(scoreboard);
        transition.play();
    }

    private ObservableList<ScoreboardUser> updateScoreBoard() {
        ArrayList<ScoreboardUser> allUsers = new ArrayList<>(MainView.getInstance().getScoreboardUsers());
        allUsers.sort((o1, o2) -> {
            if (o1.getScore() == o2.getScore())
                return o1.getNickname().compareTo(o2.getNickname());
            else
                return o2.getScore() - o1.getScore();
        });

        ObservableList<ScoreboardUser> userObservableList = FXCollections.observableArrayList();
        int counter = 20;
        int prevRank = 1;
        int prevScore = allUsers.get(0).getScore();
        if (allUsers.size() < counter)
            counter = allUsers.size();
        for (int i = 0; i < counter; i++) {
            if (i == 0)
                allUsers.get(i).setRank(1);
            else if (prevScore == allUsers.get(i).getScore())
                allUsers.get(i).setRank(prevRank);
            else {
                allUsers.get(i).setRank(i + 1);
                prevScore = allUsers.get(i).getScore();
                prevRank = i + 1;
            }
            userObservableList.add(allUsers.get(i));
        }

        return userObservableList;
    }

    public void back(ActionEvent actionEvent) throws Exception {
        new MainPage().start(stage);
    }
}
