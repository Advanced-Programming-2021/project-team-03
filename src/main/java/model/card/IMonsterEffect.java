package model.card;

import model.enums.MonsterEffectTypes;
import model.game.Board;

public interface IMonsterEffect {

    void activateMonsterEffect(Monster monster, Board gameBoard);

    boolean canActivate(Monster monster, Board gameBoard);

    MonsterEffectTypes getMonsterEffectType();
}
