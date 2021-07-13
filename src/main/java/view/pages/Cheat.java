package view.pages;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.Main;
import view.viewcontroller.MainView;

public class Cheat extends Application {
    public TextField input;
    public Text result;

    @Override
    public void start(Stage stage) throws Exception {
        Stage cheatWindow = new Stage();
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Cheat.fxml"));
        Scene scene = new Scene(startingPane);
        cheatWindow.setScene(scene);
        cheatWindow.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/pageImages/logo.png"))));
        cheatWindow.setTitle("CHEAT");
        cheatWindow.setResizable(false);
        cheatWindow.initModality(Modality.APPLICATION_MODAL);
        cheatWindow.showAndWait();

    }

    public void submit(ActionEvent event) {
        String inputCommand = input.getText().trim();
        if (inputCommand.matches(MainView.getInstance().GAME_MENU_COMMANDS[0]))
            result.setText(MainView.getInstance().activeCheat(inputCommand, 0));
        else if (inputCommand.matches(MainView.getInstance().GAME_MENU_COMMANDS[1]))
            result.setText(MainView.getInstance().activeCheat(inputCommand, 1));
        else if (inputCommand.matches(MainView.getInstance().GAME_MENU_COMMANDS[2]))
            result.setText(MainView.getInstance().activeCheat(inputCommand, 2));
        else if (inputCommand.matches(MainView.getInstance().GAME_MENU_COMMANDS[3]))
            result.setText(MainView.getInstance().activeCheat(inputCommand, 3));
        else if (inputCommand.matches(MainView.getInstance().GAME_MENU_COMMANDS[4]))
            result.setText(MainView.getInstance().activeCheat(inputCommand, 4));
        else if (inputCommand.matches(MainView.getInstance().GAME_MENU_COMMANDS[5]))
            result.setText(MainView.getInstance().activeCheat(inputCommand, 5));
        else result.setText("WRONG INPUT!");
    }
}
