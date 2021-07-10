package view.viewmodel;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.viewcontroller.MainView;

public class CardView extends ImageView {
    Image backImage = new Image(String.valueOf(getClass().getResource("/assets/cards/Unknown.jpg")));
    Image frontImage;

    private boolean isFull;
    private boolean isFaceUp;

    public CardView(int yLocation, int xLocation) {
        this.setLayoutY(yLocation);
        this.setLayoutX(xLocation);
        this.setFitHeight(85);
        this.setFitWidth(75);
        this.isFaceUp = false;
        this.isFull = false;
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
    }

    public boolean isFull() {
        return isFull;
    }

}
