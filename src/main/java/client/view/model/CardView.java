package client.view.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import client.view.controller.MainView;

public class CardView extends ImageView {
    Image backImage = new Image(String.valueOf(getClass().getResource("/assets/cards/Unknown.jpg")));
    Image frontImage;

    private boolean isFull;
    private boolean isFaceUp;
    private String cardName = "";
    private int index;
    private String type;
    private String owner;

    public CardView(int yLocation, int xLocation, String owner, String type, int index) {
        this.setLayoutY(yLocation);
        this.setLayoutX(xLocation);
        this.setFitHeight(85);
        this.setFitWidth(75);
        this.isFaceUp = false;
        this.isFull = false;
        this.setImage(null);
        this.owner = owner;
        this.type = type;
        this.index = index;
    }

    public void putTheCardOnTheBack() {
        this.setImage(backImage);
        this.isFaceUp = false;
    }

    public void putTheCardOnTheFront() {
        this.isFaceUp = true;
        this.setImage(frontImage);
    }

    public void setFrontImage(String cardName) {
        try {
            this.frontImage = MainView.getInstance().getCardImage(cardName);
            this.isFull = true;
            this.cardName = cardName;
        } catch (Exception exception) {
            this.frontImage = null;
            exception.printStackTrace();
        }
    }

    public void putTheCardOnAttackFormat() {
        this.setRotate(0);
    }

    public void putTheCardOnDefenceFormat() {
        this.setRotate(270);
    }

    public void removeImage() {
        this.isFull = false;
        this.setImage(null);
        this.cardName = "";
    }

    public boolean isFull() {
        return isFull;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public String getCardName() {
        return cardName;
    }

    public String getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }

    public int getPosition() {
        if (this.getOwner().equals("Opponent")) {
            if (this.type.equals("Field") || this.type.equals("Grave")) return 0;
            else if (this.type.equals("Monster") || this.type.equals("Spell")) {
                int[] positions = {4, 2, 1, 3, 5};
                return positions[index];
            } else return index + 1;
        } else {
            if (this.type.equals("Field") || this.type.equals("Grave")) return 0;
            else if (this.type.equals("Monster") || this.type.equals("Spell")) {
                int[] positions = {5, 3, 1, 2, 4};
                return positions[index];
            } else return index + 1;
        }
    }
}
