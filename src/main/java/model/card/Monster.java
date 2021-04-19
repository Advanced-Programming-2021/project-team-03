package model.card;

import model.enums.CardAttributes;
import model.enums.MonsterEffectTypes;
import model.enums.MonsterTypes;
import model.enums.MonsterModels;

import static model.enums.MonsterTypes.EFFECT;

public class Monster extends Card {
    private int level;
    private int baseAttack;
    private int baseDefence;
    private MonsterModels type;
    private MonsterTypes monsterType;
    private IMonsterEffect monsterEffect;

    public Monster(String cardName, int level, CardAttributes attribute, MonsterModels type, MonsterTypes monsterType,
                   int baseAttack, int baseDefence, String description, int price, String cardID) {
        super(cardName, cardID, description, price, attribute);
        this.level = level;
        this.baseAttack = baseAttack;
        this.baseDefence = baseDefence;
        this.type = type;
        this.monsterType = monsterType;
        if (monsterType.equals(EFFECT))
            monsterEffect = AllMonsterEffects.getEffectByID(cardID);
    }

    @Override
    public String toString() {
        //TODO
        return null;
    }

    public IMonsterEffect getMonsterEffect() {
        return monsterEffect;
    }
}
