package control;

import model.card.Card;
import org.json.JSONArray;

public class ScoreBoardController {
    private static ScoreBoardController scoreBoardController;

    private ScoreBoardController() {
    }

    public static ScoreBoardController getInstance() {
        if (scoreBoardController == null)
            scoreBoardController = new ScoreBoardController();
        return scoreBoardController;
    }


    public JSONArray getScoreBoard() {
        //TODO
        return null;
    }
}
