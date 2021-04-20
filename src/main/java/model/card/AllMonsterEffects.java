package model.card;

import model.enums.MonsterEffectTypes;
import model.game.Board;
import model.game.Game;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static model.enums.FaceUpSituation.*;
import static model.enums.MonsterEffectTypes.*;

public class AllMonsterEffects {
    static HashMap<String, IMonsterEffect> effectID;

    public static IMonsterEffect getEffectByID(String id) {
        return effectID.get(id);
    }

    static { // define each monster effect and add it to the hashmap
        effectID = new HashMap<>();

        /* effect of the command knight card in the game */
        IMonsterEffect commandKnightEffect = new IMonsterEffect() {
            @Override
            public void activateMonsterEffect(Monster self, Game game) {
                for (Monster monster : game.getCardBoard(self).getMonstersInField().values()) { // getting all monsters in friendly board
                    monster.addToAttackSupplier(monster1 -> 400);
                }
            }

            @Override
            public boolean canActivate(Monster self, Game game) {
                return self.getFaceUpSituation().equals(FACE_UP);
            }

            @Override
            public MonsterEffectTypes getMonsterEffectType() {
                return CONTINUOUS;
            }
        };
        effectID.put("41", commandKnightEffect);


    }
}
