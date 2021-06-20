package model.card;

import control.game.Update;
import model.game.Game;
import model.game.PlayerTurn;

public class AllSpellsEffects {
    private static AllSpellsEffects allSpellsEffects;

    private AllSpellsEffects() {
    }

    public static AllSpellsEffects getInstance() {
        if (allSpellsEffects == null)
            allSpellsEffects = new AllSpellsEffects();
        return allSpellsEffects;
    }

    public void cardActivator(SpellAndTrap spell, Game game, Update gameUpdates, PlayerTurn turn) {
        switch (spell.cardName) {
            case "Terraforming":
                terraformingEffect(game, gameUpdates, turn);
                break;
            case "Pot of Greed":
                break;
            case "Raigeki":
                break;
            case "Harpie's Feather Duster":
                break;
        }
    }

    private void terraformingEffect(Game game, Update gameUpdates, PlayerTurn turn) {

    }
}
