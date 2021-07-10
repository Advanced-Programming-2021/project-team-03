package view.pages;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import view.animations.CoinFlipAnimation;
import view.viewcontroller.MainView;
import view.viewmodel.CardView;

import java.io.IOException;
import java.util.ArrayList;

public class GamePage extends Application {
    private static Stage stage;

    @FXML
    public Text opponentLifePoint;
    public ProgressBar playerLPBar;
    public Text playerNickname;
    public Text playerLifePoint;
    public Text opponentNickname;
    public ProgressBar opponentLPBar;
    public ImageView playerProfile;
    public ImageView opponentProfile;
    public ImageView selectedCardImage;
    public AnchorPane pane;

    private ArrayList<CardView> playerMonsters = new ArrayList<>();
    private ArrayList<CardView> playerSpellAndTraps = new ArrayList<>();
    private ArrayList<CardView> playerHand = new ArrayList<>();
    private CardView playerFieldCard;
    private CardView playerGraveyard;
    private ArrayList<CardView> opponentMonsters = new ArrayList<>();
    private ArrayList<CardView> opponentSpellAndTraps = new ArrayList<>();
    private ArrayList<CardView> opponentHand = new ArrayList<>();
    private CardView opponentFieldCard;
    private CardView opponentGraveyard;
    private final double MAX_HEALTH = 8000.0;
    private CardView selectedCard;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Game.fxml"));
        this.pane = (AnchorPane) startingPane;
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
        stage.show();
    }

    @FXML
    public void initialize() {
        ImageView coin = new ImageView();
        runCOinAnimation(coin);
        opponentLPBar.setVisible(false);
        playerLPBar.setVisible(false);
        Timeline playtime = new Timeline(
                new KeyFrame(Duration.seconds(3.5), event -> {
                    pane.getChildren().remove(coin);
                    opponentLPBar.setVisible(true);
                    playerLPBar.setVisible(true);
                    loadStartingCardViews();
                    setAllOnMouseEnteredHandler();
                    setAllOnMouseExitHandler();
                    loadMap();
                })
        );
        playtime.play();
    }

    public void pause(MouseEvent mouseEvent) throws Exception {
        new GamePausePage().start(stage);
    }

    private void runCOinAnimation(ImageView coin) {
        coin.setLayoutY(310);
        coin.setLayoutX(670);
        coin.setFitWidth(100);
        coin.setFitHeight(100);
        pane.getChildren().add(coin);
        boolean isStar = MainView.getInstance().isCoinOnStarFace();
        new CoinFlipAnimation(coin, isStar).play();
    }

    public void nextPhase(MouseEvent mouseEvent) {
    }

    private void cleanMap() {
        cleanArray(playerMonsters);
        cleanArray(playerSpellAndTraps);
        cleanArray(playerHand);
        playerFieldCard.removeImage();
        playerGraveyard.removeImage();
        cleanArray(opponentMonsters);
        cleanArray(opponentSpellAndTraps);
        cleanArray(opponentHand);
        opponentFieldCard.removeImage();
        opponentGraveyard.removeImage();
    }

    private void cleanArray(ArrayList<CardView> array) {
        for (CardView cardView : array) {
            cardView.removeImage();
        }
    }

    private void loadMap() {
        JSONObject answer = MainView.getInstance().getMap();
        String type = answer.getString("Type");
        if (type.equals("Success")) {
            JSONObject map = answer.getJSONObject("Map");
            loadOpponentMap(map);
            loadPlayerMap(map);
        }
    }

    private void loadPlayerMap(JSONObject map) {
        int LP = map.getInt("LP");
        String nickname = map.getString("NI");
        int profileImageNumber = map.getInt("IM");
        loadPlayerInfo(profileImageNumber, nickname, LP);
        JSONArray monsters = map.getJSONArray("Monsters");
        for (int i = 0; i < monsters.length(); i++) {
            JSONObject monster = monsters.getJSONObject(i);
            int position = monster.getInt("Position");
            String monsterName = monster.getString("Name");
            String faceUpSit = monster.getString("FaceUpSit");
            String attackingFormat = monster.getString("AttackingFormat");
            loadMonsterCard(position, monsterName, faceUpSit, attackingFormat, this.playerMonsters);
        }
        JSONArray spellsAndTraps = map.getJSONArray("Spells");
        for (int i = 0; i < spellsAndTraps.length(); i++) {
            JSONObject spellAndTrap = spellsAndTraps.getJSONObject(i);
            int position = spellAndTrap.getInt("Position");
            String name = spellAndTrap.getString("Name");
            boolean isActive = spellAndTrap.getBoolean("Activation");
            loadSpellAndTrapCard(position, name, isActive, this.playerSpellAndTraps);
        }
        String graveyardCardName = map.getString("Grave");
        JSONObject fieldCard = map.getJSONObject("Field");
        if (!graveyardCardName.equals("None")) {
            loadGraveyard(graveyardCardName, playerGraveyard);
        }
        loadFieldCard(fieldCard, playerFieldCard);
        JSONArray hand = map.getJSONArray("Hand");
        for (int i = 0; i < hand.length(); i++) {
            String cardName = hand.getString(i);
            loadHandCard(i, cardName, playerHand);
        }
    }

    private void loadPlayerInfo(int profileImageNumber, String nickname, int LP) {
        playerProfile.setImage(new Image(String.valueOf(getClass().getResource("/assets/profilepictures/"
                + String.valueOf(profileImageNumber) + ".png"))));
        playerNickname.setText(nickname);
        playerLifePoint.setText(String.valueOf(LP));
        playerLPBar.setProgress(LP / MAX_HEALTH);
    }

    private void loadOpponentMap(JSONObject map) {
        int opponentLP = map.getInt("OPLP");
        String opponentNickname = map.getString("OPNI");
        int opponentProfileImageNumber = map.getInt("OPIM");
        System.out.println(opponentProfileImageNumber);
        loadOpponentInfo(opponentProfileImageNumber, opponentNickname, opponentLP);
        JSONArray opponentMonsters = map.getJSONArray("OPMonsters");
        for (int i = 0; i < opponentMonsters.length(); i++) {
            JSONObject monster = opponentMonsters.getJSONObject(i);
            int position = monster.getInt("Position");
            String monsterName = monster.getString("Name");
            String faceUpSit = monster.getString("FaceUpSit");
            String attackingFormat = monster.getString("AttackingFormat");
            loadMonsterCard(position, monsterName, faceUpSit, attackingFormat, this.opponentMonsters);
        }
        JSONArray spellsAndTraps = map.getJSONArray("OPSpells");
        for (int i = 0; i < spellsAndTraps.length(); i++) {
            JSONObject spellAndTrap = spellsAndTraps.getJSONObject(i);
            int position = spellAndTrap.getInt("Position");
            String name = spellAndTrap.getString("Name");
            boolean isActive = spellAndTrap.getBoolean("Activation");
            loadSpellAndTrapCard(position, name, isActive, this.opponentSpellAndTraps);
        }
        String graveyardCardName = map.getString("OPGrave");
        JSONObject fieldCard = map.getJSONObject("OPField");
        if (!graveyardCardName.equals("None")) {
            loadGraveyard(graveyardCardName, opponentGraveyard);
        }
        loadFieldCard(fieldCard, opponentFieldCard);
        JSONArray hand = map.getJSONArray("OPHand");
        for (int i = 0; i < hand.length(); i++) {
            String cardName = hand.getString(i);
            loadHandCard(i, cardName, opponentHand);
        }
    }

    private void loadHandCard(int index, String name, ArrayList<CardView> array) {
        CardView cardView = array.get(index);
        cardView.setFrontImage(name);
        if (array == opponentHand) cardView.putTheCardOnTheBack();
        else cardView.putTheCardOnTheFront();
    }

    private void loadFieldCard(JSONObject card, CardView fieldCard) {
        boolean isActive = card.getBoolean("Activation");
        String name = card.getString("Name");
        if (!name.equals("None")) {
            fieldCard.setFrontImage(name);
            if (isActive) fieldCard.putTheCardOnTheFront();
            else fieldCard.putTheCardOnTheBack();
        }
    }

    private void loadGraveyard(String cardName, CardView graveyard) {
        graveyard.setFrontImage(cardName);
        graveyard.putTheCardOnTheFront();
    }

    private void loadSpellAndTrapCard(int position, String name, boolean isActive, ArrayList<CardView> array) {
        int index = convertPositionToIndex(position);
        CardView cardView = array.get(index);
        cardView.setFrontImage(name);
        if (isActive) cardView.putTheCardOnTheFront();
        else cardView.putTheCardOnTheBack();
    }

    private void loadOpponentInfo(int profileImageNumber, String nickname, int LP) {
        opponentProfile.setImage(new Image(String.valueOf(getClass().getResource("/assets/profilepictures/"
                + String.valueOf(profileImageNumber) + ".png"))));
        opponentNickname.setText(nickname);
        opponentLifePoint.setText(String.valueOf(LP));
        opponentLPBar.setProgress(LP / MAX_HEALTH);
        System.out.println("opponentProf");
    }

    private void loadMonsterCard(int position, String monsterName, String faceUpSit, String attackingFormat, ArrayList<CardView> array) {
        int index = convertPositionToIndex(position);
        CardView cardView = array.get(index);
        cardView.setFrontImage(monsterName);
        if (faceUpSit.equals("Up")) cardView.putTheCardOnTheFront();
        else cardView.putTheCardOnTheBack();
        if (attackingFormat.equals("ATT")) cardView.putTheCardOnAttackFormat();
        else cardView.putTheCardOnDefenceFormat();
    }

    private int convertPositionToIndex(int position) {
        int[] positions = {5, 3, 1, 2, 4};
        for (int i = 0; i < 5; i++) {
            if (positions[i] == position) return i;
        }
        return 0;
    }

    private void loadStartingCardViews() {
        Integer[] xPositions = {500, 597, 697, 798, 896};
        for (int i = 0; i < 5; i++) {
            opponentSpellAndTraps.add(new CardView(125, xPositions[i]));
            opponentMonsters.add(new CardView(244, xPositions[i]));
            playerMonsters.add(new CardView(389, xPositions[i]));
            playerSpellAndTraps.add(new CardView(511, xPositions[i]));
        }
        for (int i = 0; i < 6; i++) {
            playerHand.add(new CardView(650, 400 + i * 100));
            opponentHand.add(new CardView(-15, 400 + i * 100));
        }
        playerFieldCard = new CardView(413, 1000);
        playerGraveyard = new CardView(381, 399);
        opponentFieldCard = new CardView(216, 399);
        opponentGraveyard = new CardView(241, 1000);
        pane.getChildren().add(playerFieldCard);
        pane.getChildren().add(playerGraveyard);
        pane.getChildren().add(opponentFieldCard);
        pane.getChildren().add(opponentGraveyard);
        addArrayToPane(playerMonsters);
        addArrayToPane(playerSpellAndTraps);
        addArrayToPane(playerHand);
        addArrayToPane(opponentMonsters);
        addArrayToPane(opponentSpellAndTraps);
        addArrayToPane(opponentHand);
    }

    private void addArrayToPane(ArrayList<CardView> cards) {
        for (CardView card : cards) {
            pane.getChildren().add(card);
        }
    }

    private void setAllOnMouseEnteredHandler(){
        setOnMouseEnteredHandler(playerFieldCard);
        setOnMouseEnteredHandler(opponentFieldCard);
        setOnMouseEnteredHandlerForArray(playerHand);
        setOnMouseEnteredHandlerForArray(playerMonsters);
        setOnMouseEnteredHandlerForArray(playerSpellAndTraps);
        setOnMouseEnteredHandlerForArray(opponentHand);
        setOnMouseEnteredHandlerForArray(opponentMonsters);
        setOnMouseEnteredHandlerForArray(opponentSpellAndTraps);
    }

    private void setOnMouseEnteredHandlerForArray(ArrayList<CardView> array){
        for (CardView cardView: array){
            setOnMouseEnteredHandler(cardView);
        }
    }

    private void showSelectedCard() {
        selectedCardImage.setImage(selectedCard.getImage());
    }

    private void setOnMouseEnteredHandler(CardView cardView){
        EventHandler eventHandler = event -> {
            if (cardView.isFull()){
                selectedCard = cardView;
                showSelectedCard();
            }
        };
        cardView.setOnMouseEntered(eventHandler);
    }

    private void setAllOnMouseExitHandler(){
        setOnMouseExitedHandler(playerFieldCard);
        setOnMouseExitedHandler(opponentFieldCard);
        setOnMouseExitedHandlerForArray(playerHand);
        setOnMouseExitedHandlerForArray(playerMonsters);
        setOnMouseExitedHandlerForArray(playerSpellAndTraps);
        setOnMouseExitedHandlerForArray(opponentHand);
        setOnMouseExitedHandlerForArray(opponentMonsters);
        setOnMouseExitedHandlerForArray(opponentSpellAndTraps);
    }

    private void setOnMouseExitedHandlerForArray(ArrayList<CardView> array){
        for (CardView cardView: array){
            setOnMouseExitedHandler(cardView);
        }
    }

    private void removeSelectedCard() {
        selectedCardImage.setImage(null);
    }

    private void setOnMouseExitedHandler(CardView cardView){
        EventHandler eventHandler = event -> {
                selectedCard = null;
                removeSelectedCard();
        };
        cardView.setOnMouseExited(eventHandler);
    }
}
