package view.viewmodel;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView extends ImageView {
    public CardView(int yLocation, int xLocation) {
        this.setLayoutY(yLocation);
        this.setLayoutX(xLocation);
        this.prefHeight(85);
        this.prefWidth(75);
        this.setImage(new Image(String.valueOf(getClass().getResource("/assets/profilepictures/1.png"))));
    }
}
