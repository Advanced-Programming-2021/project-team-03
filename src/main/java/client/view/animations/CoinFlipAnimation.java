package client.view.animations;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CoinFlipAnimation extends Transition {
    private final boolean head;
    private final ImageView imageView;
    private Image image;

    public CoinFlipAnimation(ImageView imageView, boolean head) {
        this.imageView = imageView;
        this.head = head;
        this.setCycleDuration(Duration.millis(700));
        this.setCycleCount(4);
    }

    @Override
    protected void interpolate(double v) {
        if (head)
            headAnimation(v);
        else
            tailAnimation(v);
    }

    //Heart
    private void tailAnimation(double v) {
        int frame = (int) Math.floor(v * 20);
        image = new Image(String.valueOf(getClass().getResource("/assets/coin/tail/" + frame + ".png")));
        imageView.setImage(image);
    }

    //Star
    private void headAnimation(double v) {
        int frame = (int) Math.floor(v * 20);
        image = new Image(String.valueOf(getClass().getResource("/assets/coin/head/" + frame + ".png")));
        imageView.setImage(image);
    }
}
