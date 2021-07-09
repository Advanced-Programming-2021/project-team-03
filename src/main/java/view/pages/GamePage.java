package view.pages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.card.Card;
import org.json.JSONObject;
import view.viewcontroller.MainView;
import view.viewmodel.CardView;

import java.util.ArrayList;

public class GamePage extends Application {
    private static Stage stage;
    private AnchorPane pane;

    @FXML
    public Text opponentLifePoint;
    public ProgressBar playerLPBar;
    public Text playerNickname;
    public Text playerLifePoint;
    public Text opponentNickname;
    public ProgressBar opponentLPBar;
    public ImageView playerProfile;
    public ImageView opponentProfile;
    public ImageView selectedCard;

    private ArrayList<CardView> playerMonsters = new ArrayList<>();
    private ArrayList<CardView> playerSpellAndTraps = new ArrayList<>();
    private CardView playerFieldCard;
    private CardView playerGraveyard;
    private ArrayList<CardView> opponentMonsters = new ArrayList<>();
    private ArrayList<CardView> opponentSpellAndTraps = new ArrayList<>();
    private CardView opponentFieldCard;
    private CardView opponentGraveyard;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Game.fxml"));
        this.pane = (AnchorPane) startingPane;
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
        refreshPage();
    }

    @FXML
    public void initialize() {
    }

    public void pause(MouseEvent mouseEvent) {
    }

    public void nextPhase(MouseEvent mouseEvent) {
    }

    private void refreshPage(){
        JSONObject map = MainView.getInstance().getMap();
        loadPlayersInfo();
        loadStartingCardViews();
    }

    private void loadPlayersInfo() {

    }

    private void loadStartingCardViews(){
        Integer[] xPositions = {500,597,697,798,896};
        for (int i = 0; i < 5; i++) {
            opponentSpellAndTraps.add(new CardView(125,xPositions[i]));
            opponentMonsters.add(new CardView(244,xPositions[i]));
            playerMonsters.add(new CardView(389,xPositions[i]));
            playerSpellAndTraps.add(new CardView(511,xPositions[i]));
        }
        playerFieldCard = new CardView(413,1000);
        playerGraveyard = new CardView(381,399);
        opponentFieldCard = new CardView(216,399);
        opponentGraveyard = new CardView(241,1000);
        pane.getChildren().add(playerFieldCard);
        pane.getChildren().add(playerGraveyard);
        pane.getChildren().add(opponentFieldCard);
        pane.getChildren().add(opponentGraveyard);
        addArrayToPane(playerMonsters);
        addArrayToPane(playerSpellAndTraps);
        addArrayToPane(opponentMonsters);
        addArrayToPane(opponentSpellAndTraps);
    }

    private void addArrayToPane(ArrayList<CardView> cards){
        for (CardView card: cards){
            pane.getChildren().add(card);
        }
    }
}
