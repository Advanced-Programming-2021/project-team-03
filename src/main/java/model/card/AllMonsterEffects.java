package model.card;

import model.enums.MonsterEffectTypes;
import model.game.Board;

import java.util.HashMap;

import static model.enums.MonsterEffectTypes.CONTINUOUS;

public class AllMonsterEffects {
    static HashMap<String, IMonsterEffect> effectID;

    public static IMonsterEffect getEffectByID(String id) {
        return effectID.get(id);
    }

    static { // define each monster effect and add it to the hashmap
        effectID = new HashMap<>();
        IMonsterEffect commandKnightEffect = new IMonsterEffect() {
            @Override
            public void activateMonsterEffect(Monster monster, Board gameBoard) {

            }

            @Override
            public boolean canActivate(Monster monster, Board gameBoard) {
                return false;
            }

            @Override
            public MonsterEffectTypes getMonsterEffectType() {
                return CONTINUOUS;
            }
        };
        effectID.put("41", commandKnightEffect);
    }
}
