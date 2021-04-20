package model.card;

import model.enums.MonsterEffectTypes;
import model.game.Board;
import model.game.Game;

public interface IMonsterEffect {

    void activateMonsterEffect(Monster monster, Game game);

    boolean canActivate(Monster monster, Game game);

    MonsterEffectTypes getMonsterEffectType();
}
