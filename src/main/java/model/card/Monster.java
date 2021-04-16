package model.card;

import model.enums.MonsterEffectTypes;
import model.enums.MonsterTypes;

public class Monster extends Card{
    private int level;
    private int attack;
    private int defence;
    private MonsterTypes type;
    private MonsterEffectTypes effectType;

    @Override
    public void cardAction() {
    }

    @Override
    public String toString() {
        //TODO
        return null;
    }
}
