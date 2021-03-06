package client.view.pages;

import client.view.animations.CoinFlipAnimation;
import client.view.controller.MainView;
import client.view.model.CardView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

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
    public Text messageText;
    public ImageView gameField;
    public Image normalFieldImage;
    public Button pauseButton;
    public Button nextPhaseButton;

    private final ArrayList<CardView> playerMonsters = new ArrayList<>();
    private final ArrayList<CardView> playerSpellAndTraps = new ArrayList<>();
    private final ArrayList<CardView> playerHand = new ArrayList<>();
    private CardView playerFieldCard;
    private CardView playerGraveyard;
    private final ArrayList<CardView> opponentMonsters = new ArrayList<>();
    private final ArrayList<CardView> opponentSpellAndTraps = new ArrayList<>();
    private final ArrayList<CardView> opponentHand = new ArrayList<>();
    private CardView opponentFieldCard;
    private CardView opponentGraveyard;
    private final double MAX_HEALTH = 8000.0;
    private CardView selectedCard;
    private static MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Game.fxml"));
        this.pane = (AnchorPane) startingPane;
        Scene scene = new Scene(startingPane);
        scene.setOnKeyPressed(keyEvent -> {
            try {
                checkForCheat(keyEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        primaryStage.setScene(scene);
        stage = primaryStage;
        stage.show();
    }

    @FXML
    public void initialize() {
        ImageView coin = new ImageView();
        runCOinAnimation(coin);
        opponentLPBar.setVisible(false);
        playerLPBar.setVisible(false);
        pauseButton.setVisible(false);
        nextPhaseButton.setVisible(false);
        Timeline playtime = new Timeline(
                new KeyFrame(Duration.seconds(3.5), event -> {
                    pane.getChildren().remove(coin);
                    opponentLPBar.setVisible(true);
                    playerLPBar.setVisible(true);
                    pauseButton.setVisible(true);
                    nextPhaseButton.setVisible(true);
                    loadStartingCardViews();
                    setAllOnMouseEnteredHandler();
                    setAllOnMouseExitHandler();
                    setAllOnMouseClickedHandler();
                    loadMap();
                })
        );
        playtime.play();
        MainView.getInstance().setGamePage(this);
    }

    public void pause(MouseEvent mouseEvent) throws Exception {
        GamePausePage.setGamePage(this);
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
        JSONObject answer = MainView.getInstance().goToTheNextPhase();
        String value = answer.getString("Value");
        messageText.setText(MainView.getInstance().getPhase());
        Timeline playtime = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> messageText.setText(""))
        );
        playtime.play();
        refreshMap();
    }

    public void printMessage(String value) {
        messageText.setText(value);
        Timeline playtime = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> messageText.setText(""))
        );
        playtime.play();
        refreshMap();
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

    private boolean resultShown = false;

    private void refreshMap() {
        if (!resultShown)
            checkGameResults();
        cleanMap();
        loadMap();
    }

    private void checkGameResults() {
        if (MainView.getInstance().isGameOver()) {
            try {
                Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/Lose1.wav")).toExternalForm());
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/Lose2.wav")).toExternalForm());
                        mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.play();
                    }
                });
                mediaPlayer.play();
                GameResultPage.setGamePage(this);
                new GameResultPage().start(stage);
                resultShown = true;
            } catch (Exception ignored) {
            }
        }
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
            loadGameField();
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
                + profileImageNumber + ".png"))));
        playerNickname.setText(nickname);
        playerLifePoint.setText(String.valueOf(LP));
        playerLPBar.setProgress(LP / MAX_HEALTH);
    }

    private void loadOpponentMap(JSONObject map) {
        int opponentLP = map.getInt("OPLP");
        String opponentNickname = map.getString("OPNI");
        int opponentProfileImageNumber = map.getInt("OPIM");
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

    private void loadGameField() {
        if (playerFieldCard.isFull() && playerFieldCard.isFaceUp()) {
            try {
                gameField.setImage(MainView.getInstance().getBackgroundImage(playerFieldCard.getCardName()));
            } catch (Exception exception) {
                gameField.setImage(normalFieldImage);
            }
        } else if (opponentFieldCard.isFull() && opponentFieldCard.isFaceUp()) {
            try {
                gameField.setImage(MainView.getInstance().getBackgroundImage(opponentFieldCard.getCardName()));
            } catch (Exception exception) {
                gameField.setImage(normalFieldImage);
            }
        } else {
            gameField.setImage(normalFieldImage);
        }
    }

    private void loadGraveyard(String cardName, CardView graveyard) {
        graveyard.setFrontImage(cardName);
        graveyard.putTheCardOnTheFront();
    }

    private void loadSpellAndTrapCard(int position, String name, boolean isActive, ArrayList<CardView> array) {
        int index;
        if (array == playerSpellAndTraps) {
            index = convertPlayerPositionToIndex(position);
        } else {
            index = convertOpponentPositionToIndex(position);
        }
        CardView cardView = array.get(index);
        cardView.setFrontImage(name);
        if (isActive) cardView.putTheCardOnTheFront();
        else cardView.putTheCardOnTheBack();
    }

    private void loadOpponentInfo(int profileImageNumber, String nickname, int LP) {
        opponentProfile.setImage(new Image(String.valueOf(getClass().getResource("/assets/profilepictures/"
                + profileImageNumber + ".png"))));
        opponentNickname.setText(nickname);
        opponentLifePoint.setText(String.valueOf(LP));
        opponentLPBar.setProgress(LP / MAX_HEALTH);
    }

    private void loadMonsterCard(int position, String monsterName, String faceUpSit, String attackingFormat, ArrayList<CardView> array) {
        int index;
        if (array == playerMonsters) {
            index = convertPlayerPositionToIndex(position);
        } else {
            index = convertOpponentPositionToIndex(position);
        }
        CardView cardView = array.get(index);
        cardView.setFrontImage(monsterName);
        if (faceUpSit.equals("Up")) cardView.putTheCardOnTheFront();
        else cardView.putTheCardOnTheBack();
        if (attackingFormat.equals("ATT")) cardView.putTheCardOnAttackFormat();
        else cardView.putTheCardOnDefenceFormat();
    }

    private int convertPlayerPositionToIndex(int position) {
        int[] positions = {5, 3, 1, 2, 4};
        for (int i = 0; i < 5; i++) {
            if (positions[i] == position) return i;
        }
        return 0;
    }

    private int convertOpponentPositionToIndex(int position) {
        int[] positions = {4, 2, 1, 3, 5};
        for (int i = 0; i < 5; i++) {
            if (positions[i] == position) return i;
        }
        return 0;
    }

    private void loadStartingCardViews() {
        Integer[] xPositions = {500, 597, 697, 798, 896};
        for (int i = 0; i < 5; i++) {
            opponentSpellAndTraps.add(new CardView(125, xPositions[i], "Opponent", "Spell", i));
            opponentMonsters.add(new CardView(244, xPositions[i], "Opponent", "Monster", i));
            playerMonsters.add(new CardView(389, xPositions[i], "Myself", "Monster", i));
            playerSpellAndTraps.add(new CardView(511, xPositions[i], "Myself", "Spell", i));
        }
        for (int i = 0; i < 6; i++) {
            playerHand.add(new CardView(650, 400 + i * 100, "Myself", "Hand", i));
            opponentHand.add(new CardView(-15, 400 + i * 100, "Opponent", "Hand", i));
        }
        playerFieldCard = new CardView(413, 1000, "Myself", "Field", 0);
        playerGraveyard = new CardView(381, 399, "Myself", "Grave", 0);
        opponentFieldCard = new CardView(216, 399, "Opponent", "Field", 0);
        opponentGraveyard = new CardView(241, 1000, "Opponent", "Grave", 0);
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

    private void setAllOnMouseEnteredHandler() {
        setOnMouseEnteredHandler(playerFieldCard);
        setOnMouseEnteredHandler(opponentFieldCard);
        setOnMouseEnteredHandlerForArray(playerHand);
        setOnMouseEnteredHandlerForArray(playerMonsters);
        setOnMouseEnteredHandlerForArray(playerSpellAndTraps);
        setOnMouseEnteredHandlerForArray(opponentHand);
        setOnMouseEnteredHandlerForArray(opponentMonsters);
        setOnMouseEnteredHandlerForArray(opponentSpellAndTraps);
    }

    private void setOnMouseEnteredHandlerForArray(ArrayList<CardView> array) {
        for (CardView cardView : array) {
            setOnMouseEnteredHandler(cardView);
        }
    }

    private void showSelectedCard() {
        selectedCardImage.setImage(selectedCard.getImage());
    }

    private void setOnMouseEnteredHandler(CardView cardView) {
        EventHandler eventHandler = event -> {
            if (cardView.isFull()) {
                selectedCard = cardView;
                showSelectedCard();
            }
        };
        cardView.setOnMouseEntered(eventHandler);
    }

    private void setAllOnMouseExitHandler() {
        setOnMouseExitedHandler(playerFieldCard);
        setOnMouseExitedHandler(opponentFieldCard);
        setOnMouseExitedHandlerForArray(playerHand);
        setOnMouseExitedHandlerForArray(playerMonsters);
        setOnMouseExitedHandlerForArray(playerSpellAndTraps);
        setOnMouseExitedHandlerForArray(opponentHand);
        setOnMouseExitedHandlerForArray(opponentMonsters);
        setOnMouseExitedHandlerForArray(opponentSpellAndTraps);
    }

    private void setOnMouseExitedHandlerForArray(ArrayList<CardView> array) {
        for (CardView cardView : array) {
            setOnMouseExitedHandler(cardView);
        }
    }

    private void removeSelectedCard() {
        selectedCardImage.setImage(null);
    }

    private void setOnMouseExitedHandler(CardView cardView) {
        EventHandler eventHandler = event -> {
            selectedCard = null;
            removeSelectedCard();
        };
        cardView.setOnMouseExited(eventHandler);
    }

    public void surrender(JSONObject answer) {
        String type = answer.getString("Type");
        String value = answer.getString("Value");
        if (type.equals("Successful")) {
            try {
                Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/Lose2.wav")).toExternalForm());
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();

                GameResultPage.setGamePage(this);
                GameResultPage.setMessageString(value);
                new GameResultPage().start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(value);
            alert.show();
        }
    }

    private void setGraveyardOnMouseClicked() {
        playerGraveyard.setOnMouseClicked(mouseEvent -> showPlayerGraveyard());
        opponentGraveyard.setOnMouseClicked(mouseEvent -> showOpponentGraveyard());
    }

    private void showOpponentGraveyard() {
        ArrayList<String> cards = MainView.getInstance().showOpponentGraveyard();
        GraveyardPage graveyard = new GraveyardPage();
        graveyard.setCardNames(cards);
        try {
            graveyard.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPlayerGraveyard() {
        ArrayList<String> cards = MainView.getInstance().showPlayerGraveyard();
        GraveyardPage graveyard = new GraveyardPage();
        graveyard.setCardNames(cards);
        try {
            graveyard.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        try {
            new DuelMenuPage().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkForCheat(KeyEvent keyEvent) throws Exception {
        KeyCombination combination = new KeyCodeCombination(KeyCode.C, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN);
        if (combination.match(keyEvent))
            new Cheat().start(stage);
    }

    private void setAllOnMouseClickedHandler() {
        setGraveyardOnMouseClicked();
        for (CardView cardView : playerHand) {
            cardView.setOnMouseClicked(mouseEvent -> {
                if (cardView.isFull()) {
                    clickedOnPlayerHand(cardView, mouseEvent);
                }
            });
        }
        for (CardView cardView : playerSpellAndTraps) {
            cardView.setOnMouseClicked(mouseEvent -> {
                if (cardView.isFull()) {
                    clickedOnPlayerSpells(cardView, mouseEvent);
                }
            });
        }
        for (CardView cardView : playerMonsters) {
            cardView.setOnMouseClicked(mouseEvent -> {
                if (cardView.isFull()) {
                    clickedOnPlayerMonsters(cardView, mouseEvent);
                }
            });
        }
        playerFieldCard.setOnMouseClicked(mouseEvent -> {
            if (playerFieldCard.isFull()) {
                clickedOnPlayerFieldCard();
            }
        });
        for (CardView cardView : opponentMonsters) {
            cardView.setOnMouseClicked(mouseEvent -> {
                if (cardView.isFull()) {
                    clickedOnOpponentMonsters(cardView, mouseEvent);
                }
            });
        }
        opponentProfile.setOnMouseClicked(mouseEvent -> clickedOnOpponentProfile(opponentProfile));
    }

    private void clickedOnOpponentProfile(ImageView opponentProfile) {
        String phase = MainView.getInstance().getPhase();
        if (phase.equals("BATTLE")) {
            directAttack();
            opponentProfile.setEffect(new DropShadow(60, Color.DARKRED));
            Timeline playtime = new Timeline(
                    new KeyFrame(Duration.seconds(1.5), event -> opponentProfile.setEffect(null))
            );
            playtime.play();
        }
    }

    private void directAttack() {
        JSONObject answer = MainView.getInstance().directAttack();
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/AttackMonster.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/SadSolton.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void clickedOnOpponentMonsters(CardView cardView, MouseEvent mouseEvent) {
        String phase = MainView.getInstance().getPhase();
        if (phase.equals("BATTLE")) {
            attackCard(cardView);
        }
    }

    private void attackCard(CardView cardView) {
        JSONObject answer = MainView.getInstance().attackToMonster(cardView.getPosition());
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/AttackMonster.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/SadSolton.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void clickedOnPlayerFieldCard() {
        try {
            selectCard(playerFieldCard.getOwner(), playerFieldCard.getType(), playerFieldCard.getPosition());
            activeSpell();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickedOnPlayerMonsters(CardView cardView, MouseEvent mouseEvent) {
        String phase = MainView.getInstance().getPhase();
        if (phase.equals("BATTLE")) {
            selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
            cardView.setEffect(new DropShadow(60, Color.DARKRED));
            Timeline playtime = new Timeline(
                    new KeyFrame(Duration.seconds(1.5), event -> cardView.setEffect(null))
            );
            playtime.play();
        } else {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) { //Right click
                selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
                setMonsterPosition(cardView);
            } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
                filipSummonMonster();
            }
        }
    }

    private void setMonsterPosition(CardView cardView) {
        double rotate = cardView.getRotate();
        JSONObject answer;
        if (rotate < 10) { //now attack
            answer = MainView.getInstance().setPosition("Defense");
        } else { //now defence
            answer = MainView.getInstance().setPosition("Attack");
        }
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/SetPosition.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void filipSummonMonster() {
        JSONObject answer = MainView.getInstance().filipSummonMonster();
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/SummonMonster.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void clickedOnPlayerSpells(CardView cardView, MouseEvent mouseEvent) {
        try {
            selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
            activeSpell();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickedOnPlayerHand(CardView cardView, MouseEvent mouseEvent) {
        try {
            String cardType = MainView.getInstance().getCardType(cardView.getCardName());
            if (cardType.equals("Monster")) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) { //Right click
                    selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
                    setCard();
                } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
                    summonMonster();
                }
            } else {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) { //Right click
                    selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
                    setCard();
                } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    selectCard(cardView.getOwner(), cardView.getType(), cardView.getPosition());
                    activeSpell();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void activeSpell() {
        JSONObject answer = MainView.getInstance().activeSpell();
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/ActiveSpell.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void summonMonster() {
        JSONObject answer = MainView.getInstance().summonMonster();
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/SummonMonster.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void setCard() {
        JSONObject answer = MainView.getInstance().setCard();
        String type = answer.getString("Type");
        if (type.equals("Successful")) {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("/assets/soundtrack/SetCard.wav")).toExternalForm());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            refreshMap();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(answer.getString("Value"));
            alert.show();
        }
    }

    private void selectCard(String owner, String type, int position) {
        MainView.getInstance().selectCard(owner, type, position);
    }
}
