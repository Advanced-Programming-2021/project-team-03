package model.card;

import control.game.Update;
import model.game.Game;
import model.game.PlayerTurn;

public class AllTrapsEffects {
    private static AllTrapsEffects instance;

    private AllTrapsEffects(){}

    public static AllTrapsEffects getInstance(){
        if (instance == null) instance = new AllTrapsEffects();
        return instance;
    }

    public void trapHoleEffect(Game game, Update gameUpdates, PlayerTurn turn){

    }
}
