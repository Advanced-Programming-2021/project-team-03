package model.card;

import model.enums.*;

import java.util.ArrayList;
import java.util.function.Function;

import static model.enums.MonsterTypes.EFFECT;

public class Monster extends Card {
    private final int level;
    private final int baseAttack;
    private final int baseDefence;
    private final MonsterModels model; // monster model is the model of the card for example warrior or spell caster or ..
    private IMonsterEffect monsterEffect; // this interface contains the special monster effect if exists

    /* two enums below contains the position of the monster card in the game board */
    private AttackingFormat attackingFormat;
    private FaceUpSituation faceUpSituation;

    private ArrayList<Function<Monster, Integer>> attackSupplier; // contains all game effects which determine the attacking power of the monster

    public Monster(String cardName, int level, CardAttributes attribute, MonsterModels model, MonsterTypes monsterType,
                   int baseAttack, int baseDefence, String description, int price, String cardID) {
        super(cardName, cardID, description, price, attribute);
        this.level = level;
        this.baseAttack = baseAttack;
        this.baseDefence = baseDefence;
        this.model = model;
        // monster type is the effect type of the card which could be normal, effect or ritual
        if (monsterType.equals(EFFECT))
            monsterEffect = AllMonsterEffects.getEffectByID(cardID);
        attackSupplier = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Name: " + this.cardName + "\n" +
                "Level: " + this.level + "\n" +
                "Type: " + this.model + "\n" +
                "ATK: " + this.baseAttack + "\n" +
                "DEF: " + this.baseDefence + "\n" +
                "Description: " + this.description;
    }

    public IMonsterEffect getMonsterEffect() {
        return monsterEffect;
    }

    public AttackingFormat getAttackingFormat() {
        return attackingFormat;
    }

    public void setAttackingFormat(AttackingFormat attackingFormat) {
        this.attackingFormat = attackingFormat;
    }

    public FaceUpSituation getFaceUpSituation() {
        return faceUpSituation;
    }

    public void setFaceUpSituation(FaceUpSituation faceUpSituation) {
        this.faceUpSituation = faceUpSituation;
    }

    public ArrayList<Function<Monster, Integer>> getAttackSupplier() {
        return attackSupplier;
    }

    public void addToAttackSupplier(Function<Monster, Integer> function) {
        attackSupplier.add(function);
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefence() {
        return baseDefence;
    }

    public int getAttackingPower() {
        int AttackingPower = this.baseAttack;
        for (Function<Monster, Integer> function : attackSupplier) {
            AttackingPower += function.apply(this);
        }
        return AttackingPower;
    }

    public int getLevel() {
        return this.level;
    }
}
