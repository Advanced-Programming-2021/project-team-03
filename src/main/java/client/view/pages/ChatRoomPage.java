package client.view.pages;

import client.view.controller.MainView;
import client.view.model.Message;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.List;


public class ChatRoomPage extends Application {
    private static Stage stage;
    public Pane pinnedPane;
    public Label pinnedLabel;
    public Button cancelReplyButton;
    private String nickName;
    public ScrollPane messagesPane;
    public TextArea messageText;
    public Label onlineUsersLabel;
    public static final MainView view = MainView.getInstance();
    private VBox vBox;
    private Message pinnedMessage;
    private Message replyingTo;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent startingPane = FXMLLoader.load(getClass().getResource("/view/fxml/ChatRoom.fxml"));
        primaryStage.setScene(new Scene(startingPane));
        stage = primaryStage;
    }

    @FXML
    public void initialize() {
        cancelReplyButton.setDisable(true);
        nickName = view.getNickname();
        messagesPane.setPadding(new Insets(10));
        messagesPane.setCenterShape(true);

        vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(8));
        messagesPane.setContent(vBox);
        messagesPane.setPadding(new Insets(8));

        String cssLayout2 = """
                -fx-border-color: rgb(95, 177, 236);
                -fx-border-insets: 5;
                -fx-border-width: 6;
                -fx-border-radius: 10px;
                -fx-background-color: rgba(255, 255, 255, 0.9)
                """;
        messagesPane.setStyle(cssLayout2);
        pinnedPane.setStyle(cssLayout2);
        pinnedPane.setPadding(new Insets(10));
        loadMessages();
    }

    private void loadMessages() {
        loadPinnedMessage();
        onlineUsersLabel.setText(view.onlineUsersNum() + " online users");
        vBox.getChildren().clear();

        String cssLayout = """
                -fx-border-color: rgba(95, 177, 236, 0.9);
                -fx-border-insets: 3;
                -fx-border-width: 4;
                -fx-border-radius: 10px;
                """;

        List<Message> messages = view.getAllMessages();
        for (Message message : messages) {
            VBox messageVbox = new VBox();
            setMessageVBox(message, messageVbox);
            messageVbox.setSpacing(5);

            messageVbox.setPadding(new Insets(10));
            messageVbox.setStyle(cssLayout);

            HBox alignment = new HBox(messageVbox);
            if (message.senderNickname.equals(nickName)) {
                messageVbox.setAlignment(Pos.CENTER_RIGHT);
                alignment.setAlignment(Pos.CENTER_RIGHT);
            } else {
                messageVbox.setAlignment(Pos.CENTER_LEFT);
                alignment.setAlignment(Pos.CENTER_LEFT);
            }
            alignment.setPrefWidth(510);
            vBox.getChildren().add(alignment);
        }
    }

    public void back(ActionEvent actionEvent) throws Exception {
        new MainPage().start(stage);
    }

    public void send(ActionEvent actionEvent) {
        String text = messageText.getText();
        if (text.equals("")) {
            replyingTo = null;
            return;
        }
        if (replyingTo == null) view.sendMessage(text);
        else {
            System.out.println(view.replyMessage(text, replyingTo.ID));
            cancelReply(null);
        }

        clear(null);
        loadMessages();
    }

    public void clear(ActionEvent actionEvent) {
        messageText.clear();
    }

    private void pin(Message message) {
        view.alertMaker(view.pinMessage(message.ID));
        loadMessages();
    }

    private void delete(Message message) {
        view.alertMaker(view.deleteMessage(message.ID));
        loadMessages();
    }

    private void edit(Message message, VBox messageVBox) {
        messageVBox.getChildren().clear();

        Label senderLabel = new Label(message.senderNickname);
        messageVBox.getChildren().add(senderLabel);

        TextArea textArea = new TextArea(message.text);
        textArea.setEditable(true);
        messageVBox.getChildren().add(textArea);

        Label timeLabel = new Label(message.time);
        messageVBox.getChildren().add(timeLabel);

        if (message.edited) {
            Label editedLabel = new Label("(edited)");
            messageVBox.getChildren().add(editedLabel);
        }

        Button editButton = new Button("Edit");
        Button cancelButton = new Button("Cancel");
        editButton.setOnAction(event -> editRequest(textArea.getText(), message));
        cancelButton.setOnAction(event -> loadMessages());
        messageVBox.getChildren().add(editButton);
        messageVBox.getChildren().add(cancelButton);
    }

    private void editRequest(String newText, Message message) {
        if (!newText.equals(""))
            view.editMessage(message.ID, newText);
        message.text = newText;
        message.edited = true;
        loadMessages();
    }

    private void loadPinnedMessage() {
        VBox pinnedVBox = new VBox();
        pinnedMessage = view.getPinnedMessage();
        pinnedPane.getChildren().clear();
        if (pinnedMessage == null) {
            pinnedLabel.setText("No pinned message");
            return;
        }
        pinnedLabel.setText("Pinned message");
        setMessageVBox(pinnedMessage, pinnedVBox);
        pinnedVBox.setPadding(new Insets(20));
        pinnedVBox.setSpacing(10);
        pinnedVBox.setMaxHeight(200);
        pinnedPane.getChildren().add(pinnedVBox);
    }

    private void setMessageVBox(Message message, VBox messageVBox) {
        messageVBox.getChildren().clear();

        VBox profileVBox = new VBox();
        profileVBox.setAlignment(Pos.CENTER);
        profileVBox.setStyle("""
                -fx-border-color: orange;
                -fx-border-insets: 1;
                -fx-border-width: 2;
                -fx-border-radius: 5px;
                """);
        profileVBox.setMaxWidth(120);

        ImageView profileImage = new ImageView(
                view.getProfileImageByID(view.getProfileImageNumber(message.senderUsername)));
        profileImage.setPreserveRatio(true);
        profileImage.setFitWidth(100);
        profileImage.setOnMouseClicked(event -> showProfilePopup(message));
        profileVBox.getChildren().add(profileImage);

        Label senderLabel = new Label(message.senderNickname);
        senderLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        profileVBox.getChildren().add(senderLabel);

        messageVBox.getChildren().add(profileVBox);

        Label textLabel = new Label(message.text);
        textLabel.setWrapText(true);
        textLabel.setTextAlignment(TextAlignment.LEFT);
        textLabel.setPrefWidth(200);
        textLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 13));

        messageVBox.getChildren().add(textLabel);

        Label timeLabel = new Label(message.time);
        timeLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 10));
        messageVBox.getChildren().add(timeLabel);

        if (message.deleted) {
            Label editedLabel = new Label("(deleted)");
            messageVBox.getChildren().add(editedLabel);
        } else if (message.edited) {
            Label editedLabel = new Label("(edited)");
            messageVBox.getChildren().add(editedLabel);
        }

        MenuItem pinItem;
        if (pinnedMessage != null && pinnedMessage.ID == message.ID) {
            pinItem = new MenuItem("Unpin message");
            pinItem.setOnAction(event -> unpin(null));
        } else {
            pinItem = new MenuItem("Pin message");
            pinItem.setOnAction(event -> pin(message));
        }

        MenuItem replyItem = new MenuItem("Reply");
        replyItem.setOnAction(event -> reply(message));

        MenuButton menuButton = new MenuButton("...", null, replyItem, pinItem);
        if (message.senderNickname.equals(nickName)) {
            MenuItem deleteItem = new MenuItem("Delete message");
            MenuItem editItem = new MenuItem("Edit message");
            deleteItem.setOnAction(event -> delete(message));
            editItem.setOnAction(event -> edit(message, messageVBox));
            menuButton.getItems().addAll(deleteItem, editItem);
        }
        if (!message.deleted) messageVBox.getChildren().add(menuButton);
    }

    private void showProfilePopup(Message message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Details");
        alert.setHeaderText(message.senderUsername);
        alert.setContentText(view.getUserInfo(message.senderUsername).toString());
        alert.show();
    }

    public void unpin(ActionEvent actionEvent) {
        if (pinnedMessage == null) return;
        view.alertMaker(view.pinMessage(0));
        loadPinnedMessage();
    }

    public void cancelReply(ActionEvent actionEvent) {
        replyingTo = null;
        messageText.setPromptText("type new message...");
        cancelReplyButton.setDisable(true);
        loadMessages();
    }

    public void reply(Message replyTo) {
        replyingTo = replyTo;
        messageText.setPromptText("replying to: " + replyTo.text);
        cancelReplyButton.setDisable(false);
    }
}
