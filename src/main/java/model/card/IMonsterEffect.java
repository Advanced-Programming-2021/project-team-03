package model.card;

import control.game.Update;
import model.enums.MonsterEffectTypes;
import model.game.Game;

public interface IMonsterEffect {

    void activateMonsterEffect(Monster monster, Update update);

    boolean canActivate(Monster monster, Update update);

    MonsterEffectTypes getMonsterEffectType();
}
