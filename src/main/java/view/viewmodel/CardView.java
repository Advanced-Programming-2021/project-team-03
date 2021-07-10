package view.viewmodel;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.viewcontroller.MainView;

public class CardView extends ImageView {
    Image backImage = new Image(String.valueOf(getClass().getResource("/assets/cards/Unknown.jpg")));
    Image frontImage;

    public CardView(int yLocation, int xLocation) {
        this.setLayoutY(yLocation);
        this.setLayoutX(xLocation);
        this.setFitHeight(85);
        this.setFitWidth(75);
    }

    public void putTheCardOnTheBack() {
        this.setImage(backImage);
    }

    public void putTheCardOnTheFront() {
        this.setImage(frontImage);
    }

    public void setFrontImage(String cardName) {
        try {
            this.frontImage = MainView.getInstance().getCardImage(cardName);
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
        this.setImage(null);
    }
}
