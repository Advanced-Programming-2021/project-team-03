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
                terraformingEffect(game, turn);
                break;
            case "Pot of Greed":
                potOfGreedEffect(game, turn);
                break;
            case "Raigeki":
                raigekiEffect(game, gameUpdates, turn);
                break;
            case "Harpie's Feather Duster":
                harpiesFeatherDusterEffect(game, gameUpdates, turn);
                break;
        }
    }

    private void harpiesFeatherDusterEffect(Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void raigekiEffect(Game game, Update gameUpdates, PlayerTurn turn) {

    }

    private void potOfGreedEffect(Game game, PlayerTurn turn) {
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
        game.getPlayerByTurn(turn).getBoard().addCardFromRemainingToInHandCards();
    }

    private void terraformingEffect(Game game, PlayerTurn turn) {
        game.getPlayerByTurn(turn).getBoard().addFieldSpellToHand();
    }
}
