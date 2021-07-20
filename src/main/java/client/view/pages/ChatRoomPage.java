package client.view.pages;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import client.view.controller.MainView;

public class ChatRoomPage extends Application {
    private static Stage stage;
    public ScrollPane messagesPane;
    public TextArea messageText;
    public static final MainView view = MainView.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ChatRoom.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
    }

    @FXML
    public void initialize() {
        VBox vBox = new VBox();

    }



    public void back(ActionEvent actionEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void send(ActionEvent actionEvent) {
        //MainView.getInstance().send

    }

    public void clear(ActionEvent actionEvent) {
        messageText.clear();
        System.out.println("clear");
    }
}
