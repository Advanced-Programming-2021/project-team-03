package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import view.viewcontroller.MainView;

public class ImportExportPage extends Application {
    private static Stage stage;

    @FXML
    private TextField cardNameField;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ImportExportMenu.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        primaryStage.show();
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void importCard(MouseEvent mouseEvent) {
        String cardName = cardNameField.getText();
        if (cardName.equals("")) return;
        MainView.getInstance().alertMaker(cardNameField, MainView.getInstance().importExportCard(cardName, "Import card"));
    }

    public void exportCard(MouseEvent mouseEvent) {
        String cardName = cardNameField.getText();
        if (cardName.equals("")) return;
        MainView.getInstance().alertMaker(cardNameField, MainView.getInstance().importExportCard(cardName, "Export card"));
    }
}
