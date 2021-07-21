package client.view.pages;

import client.view.controller.MainView;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuctionPage extends Application {
    private static Stage stage;

    @FXML
    public ScrollPane auctionsScrollPane;
    public ScrollPane cardsScrollPane;

    private ArrayList<ImageView> userCardImages = new ArrayList<>();
    private List<String> userCards;
    private ArrayList<Text> auctionsText = new ArrayList<>();
    private ArrayList<JSONObject> auctions = new ArrayList<>();
    private ArrayList<ImageView> auctionsImages = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/Auction.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    public void initialize() {
        loadCards();
        loadAuctions();
    }

    private void loadCards() {
        userCards = MainView.getInstance().getUserCards().getJSONArray("Cards")
                .toList().stream().map(Object::toString).collect(Collectors.toList());

        for (int i = 0; i < userCards.size(); i++) userCardImages.add(new ImageView());
        setCardImages(userCards, userCardImages);

        VBox vbox = new VBox();
        for (int i = 0; i < userCardImages.size(); i++) {
            ImageView cardImage = userCardImages.get(i);
            cardImage.setPreserveRatio(true);
            cardImage.setFitWidth(200);
            int finalI = i;
            cardImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    newAuction(finalI);
                }
            });
            vbox.getChildren().add(cardImage);
        }
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));
        cardsScrollPane.setContent(vbox);
    }

    private void newAuction(int index) {
        TextInputDialog dialog = new TextInputDialog("Set Starting Price");
        dialog.setTitle("Set Starting Price");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter starting price for auction");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String price = result.get();
            if (!price.matches("\\d+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Price format is invalid");
                alert.setTitle("Price Format Error");
                alert.show();
            } else {
                String cardName = userCards.get(index);
                boolean success = MainView.getInstance().newAuction(cardName, price);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("New Auction");
                    alert.setHeaderText(null);
                    alert.setContentText("Auction created successfully.");
                    alert.showAndWait();
                    refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Auction creation failed.");
                    alert.setTitle("New Auction");
                    alert.show();
                }
            }
        }
    }

    private void refresh() {
        auctionsText = new ArrayList<>();
        auctionsImages = new ArrayList<>();
        userCardImages = new ArrayList<>();
        loadCards();
        loadAuctions();
    }

    private void setCardImages(List<String> cards, ArrayList<ImageView> cardImages) {
        for (int i = 0; i < cards.size(); i++) {
            try {
                cardImages.get(i).setImage(MainView.getInstance().getCardImage(cards.get(i)));
            } catch (Exception e) {
                cardImages.get(i).setImage(null);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Couldn't load some images");
                alert.show();
            }
        }
    }

    private void loadAuctions() {
        auctions = new ArrayList<>();
        JSONArray value = MainView.getInstance().getAllAuctions().getJSONArray("All auctions");
        for (int i = 0; i < value.length(); i++) {
            auctions.add(value.getJSONObject(i));
        }
        for (JSONObject auction : auctions) {
            StringBuilder auctionText = new StringBuilder();
            auctionText.append("Starting Price: ").append(auction.getInt("Starting Price"))
                    .append("\n").append("Highest Bid: ").append(auction.getInt("Highest Bid")).append("\n");
            long time = System.currentTimeMillis() - auction.getLong("Starting Time");
            auctionText.append("Remaining Time: ").append((600000 - time) / 1000).append(" seconds");
            Text text = new Text();
            text.setText(auctionText.toString());
            text.setFont(Font.font("Baskerville Old Face", FontWeight.BOLD, FontPosture.REGULAR, 25));
            text.setTextAlignment(TextAlignment.CENTER);
            auctionsText.add(text);
            ImageView imageView = new ImageView(MainView.getInstance().getCardImage(auction.getString("Card Name")));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(400);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    newPrice(imageView);
                }
            });
            auctionsImages.add(imageView);
        }
        VBox vbox = new VBox();
        for (int i = 0; i < auctionsText.size(); i++) {
            vbox.getChildren().add(auctionsImages.get(i));
            vbox.getChildren().add(auctionsText.get(i));
        }
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        auctionsScrollPane.setContent(vbox);
    }

    private void newPrice(ImageView imageView) {
        TextInputDialog dialog = new TextInputDialog("Set New Price");
        dialog.setTitle("Set New Price");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter your bid.");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String price = result.get();
            if (!price.matches("\\d+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Price format is invalid");
                alert.setTitle("Price Format Error");
                alert.show();
            } else {
                int auctionId = auctions.get(auctionsImages.indexOf(imageView)).getInt("Id");
                JSONObject answer = MainView.getInstance().newPrice(String.valueOf(auctionId), price);
                String type = answer.getString("Type");
                if (type.equals("Success")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("New Bid");
                    alert.setHeaderText(null);
                    alert.setContentText(answer.getString("Value"));
                    alert.showAndWait();
                    refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText(answer.getString("Value"));
                    alert.setTitle("New Bid");
                    alert.show();
                }
            }
        }
    }

    public void refreshPage(MouseEvent mouseEvent) {
        refresh();
    }

    public void back(MouseEvent mouseEvent) throws Exception {
        new ShopMenuPage().start(stage);
    }
}
